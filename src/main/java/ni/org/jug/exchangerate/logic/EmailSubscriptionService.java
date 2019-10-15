package ni.org.jug.exchangerate.logic;

import ni.org.jug.exchangerate.exception.InvalidDataException;
import ni.org.jug.exchangerate.exception.UserSubscriptionNotFoundException;
import ni.org.jug.exchangerate.model.Bank;
import ni.org.jug.exchangerate.model.CentralBankExchangeRate;
import ni.org.jug.exchangerate.model.CommercialBankExchangeRate;
import ni.org.jug.exchangerate.model.EmailTask;
import ni.org.jug.exchangerate.model.UserSubscription;
import ni.org.jug.exchangerate.repository.BankRepository;
import ni.org.jug.exchangerate.repository.CentralBankExchangeRateRepository;
import ni.org.jug.exchangerate.repository.CommercialBankExchangeRateRepository;
import ni.org.jug.exchangerate.repository.EmailTaskRepository;
import ni.org.jug.exchangerate.repository.UserSubscriptionRepository;
import ni.org.jug.exchangerate.util.HtmlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmailSubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSubscriptionService.class);

    private static final HtmlGenerator EMAIL_TEMPLATE_FOR_ACTIVATION = new HtmlGenerator();
    static {
        EMAIL_TEMPLATE_FOR_ACTIVATION.html5()
                .head()
                    .style()
                        .styleAppend("body {")
                        .styleAppend("    font-family: Arial, Helvetica, sans-serif;")
                        .styleAppend("    font-size: 18px;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend(".panel {")
                        .styleAppend("    margin-top: 15px;")
                        .styleAppend("    margin-bottom: 15px;")
                        .styleAppend("    padding: 5px 20px;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend(".note {")
                        .styleAppend("    background-color: #ddd;")
                        .styleAppend("    border-left: 5px solid #4caf50;")
                        .styleAppend("}")
                    .closeStyle()
                .closeHead()
                .body()
                    .p("Hola <strong>%s</strong>, para poder recibir los datos de la compra/venta de d&#243;lares en los bancos " +
                            "comerciales, debe presionar el siguiente enlace: <a href=\"%s\">Presione aqu&#237;</a>.")
                    .div("panel", "note")
                        .p("<strong>Nota:</strong> Una vez dado de alta en nuestro sistema, recibir&#225; un correo diario con los datos " +
                                "de la compra/venta y un enlace al pie del correo con el cual podr&#225; darse de baja de nuestro sistema. Si " +
                                "por alguna raz&#243;n los datos de la compra/venta cambian, se volver&#225; a enviar un correo con los " +
                                "nuevos datos.")
                    .closeDiv()
                .closeBody()
                .closeHtml();
    }

    private static final HtmlGenerator EMAIL_TEMPLATE_FOR_EXCHANGE_RATE_DATA = new HtmlGenerator();
    private static final String INDENTATION_AT_INSERTION_POINT;
    static {
        EMAIL_TEMPLATE_FOR_EXCHANGE_RATE_DATA.html5()
                .head()
                    .style()
                        .styleAppend("body {")
                        .styleAppend("    font-family: Arial, Helvetica, sans-serif;")
                        .styleAppend("    padding: 10px;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("table {")
                        .styleAppend("    border-collapse: collapse;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("td, th {")
                        .styleAppend("    border: 1px solid #ddd;")
                        .styleAppend("    padding: 6px;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("th {")
                        .styleAppend("    padding-top: 10px;")
                        .styleAppend("    padding-bottom: 10px;")
                        .styleAppend("    text-align: left;")
                        .styleAppend("    background-color: #4caf50;")
                        .styleAppend("    color: white;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("tfoot td {")
                        .styleAppend("    padding-top: 10px;")
                        .styleAppend("    padding-bottom: 10px;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("tr:nth-child(even) {")
                        .styleAppend("    background-color: #f2f2f2;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("tr:hover {")
                        .styleAppend("    background-color: #ddd;")
                        .styleAppend("}")
                        .styleAppend()
                        .styleAppend("p {")
                        .styleAppend("    margin-top: 20px;")
                        .styleAppend("}")
                    .closeStyle()
                .closeHead()
                .body()
                    .p("<p>Los siguientes datos corresponden a la fecha: <strong>%s</strong>.</p>")
                    .addPlaceholder("%s");

        INDENTATION_AT_INSERTION_POINT = EMAIL_TEMPLATE_FOR_EXCHANGE_RATE_DATA.getIndentation();

        EMAIL_TEMPLATE_FOR_EXCHANGE_RATE_DATA
                    .p("Si ya no desea seguir recibiendo este correo, recuerde que puede darse de <strong>baja</strong> en cualquier " +
                                    "momento con el siguiente enlace: <a href=\"%s\">Presione aqu&#237;</a>.")
                .closeBody()
                .closeHtml();
    }

    private static final String HORIZONTAL_RULE =
            "----------------------------------------------------------------------------------------------------";
    private static final String BANK_LOGO_TEMPLATE = "<img src=\"cid:%1$s\" alt=\"%1$s\" width=\"40px\" height=\"40px\">";
    private static final String TENDENCY_INDICATOR = "<img src=\"cid:%s\" width=\"15px\" height=\"15px\">";
    private static final DateTimeFormatter NIO_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    @Autowired
    CommercialBankExchangeRateRepository commercialBankExchangeRateRepository;

    @Autowired
    CentralBankExchangeRateRepository centralBankExchangeRateRepository;

    @Autowired
    UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    EmailTaskRepository emailTaskRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    JavaMailSender mailSender;

    private AtomicInteger counter = new AtomicInteger();
    private Map<String, ClassPathResource> logos = new HashMap<>();

    @PostConstruct
    public void init() {
        for (Bank bank : bankRepository.findAll()) {
            String key = bank.getDescription().getShortDescription();
            String path = key + ".png";
            logos.put(key, new ClassPathResource(path));
        }
        logos.put("up", new ClassPathResource("up.png"));
        logos.put("equal", new ClassPathResource("equal.png"));
        logos.put("down", new ClassPathResource("down.png"));
    }

    private String generateToken() {
        long milliOfDay = LocalDateTime.now().getLong(ChronoField.MILLI_OF_DAY);
        StringBuilder token = new StringBuilder(100);
        token.append(counter.incrementAndGet()).append('-').append(milliOfDay);
        return token.toString();
    }

    private void sendEmailToEnableSubscription(String email, String fullName, String url) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        messageHelper.setTo(email);
        messageHelper.setSubject("Servicio de Compra/Venta de dolares en los Bancos Comerciales");
        messageHelper.setText(EMAIL_TEMPLATE_FOR_ACTIVATION.asHtml(fullName, url), true);

        mailSender.send(message);
    }

    public void createSubscription(String fullName, String email, UriComponentsBuilder uriComponentsBuilder) throws MessagingException {
        UserSubscription userSubscription = userSubscriptionRepository.findByEmail(email);
        String token = generateToken();

        if (userSubscription == null) {
            LOGGER.debug("Crear nueva suscripcion con email [{}] y nombre completo [{}]", email, fullName);

            userSubscription = new UserSubscription();
            userSubscription.setFullName(fullName);
            userSubscription.setEmail(email);
            userSubscription.setToken(token);
            userSubscription.setActive(false);
            userSubscriptionRepository.save(userSubscription);
        } else {
            LOGGER.debug("Actualizar suscripcion con email [{}] y nombre completo [{}]", email, fullName);

            userSubscription.setFullName(fullName);
            userSubscription.setToken(token);
            userSubscription.setActive(false);
        }

        String urlToActivate = uriComponentsBuilder.path("/activate/{email}/{token}").buildAndExpand(email, token).encode().toUriString();

        sendEmailToEnableSubscription(email, fullName, urlToActivate);
    }

    public void activateSubscription(String email, String token) {
        UserSubscription userSubscription = userSubscriptionRepository.findByEmail(email);
        validateUserSubscription(userSubscription, email, token);
        userSubscription.setActive(Boolean.TRUE);
    }

    public void deactivateSubscription(String email, String token) {
        UserSubscription userSubscription = userSubscriptionRepository.findByEmail(email);
        validateUserSubscription(userSubscription, email, token);
        userSubscription.setActive(Boolean.FALSE);
    }

    private void validateUserSubscription(UserSubscription userSubscription, String email, String token) {
        if (userSubscription == null) {
            throw new UserSubscriptionNotFoundException(email);
        }
        if (!userSubscription.getToken().equals(token)) {
            throw new InvalidDataException("El token [" + token + "] es incorrecto");
        }
    }

    private BigDecimal getCurrentOfficialExchangeRate() {
        Integer officialExchangeRateId = CentralBankExchangeRate.calculateId(LocalDate.now());
        Optional<CentralBankExchangeRate> officialExchangeRateData = centralBankExchangeRateRepository.findById(officialExchangeRateId);
        BigDecimal officialExchangeRate = officialExchangeRateData.map(CentralBankExchangeRate::getAmount).orElse(BigDecimal.ZERO);
        return officialExchangeRate;
    }

    private String constructHtmlTableWithExchangeRateData() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.plusDays(-1);
        Map<String, Map<LocalDate, CommercialBankExchangeRate>> exchangeRateByBankAndDate;

        exchangeRateByBankAndDate = commercialBankExchangeRateRepository.findByDateBetween(yesterday, today)
                .stream()
                .collect(Collectors.groupingBy(exchangeRate -> exchangeRate.getBank().getDescription().getShortDescription(),
                        HashMap::new, Collectors.toMap(CommercialBankExchangeRate::getDate, Function.identity())));

        boolean todayDataIsPresent = exchangeRateByBankAndDate.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .map(map -> map.entrySet())
                .flatMap(Set::stream)
                .map(Map.Entry::getKey)
                .filter(date -> today.equals(date))
                .findAny().isPresent();

        if (!todayDataIsPresent) {
            LOGGER.info("No hay datos de compra/venta para la fecha actual");
            return "";
        }

        HtmlGenerator html = new HtmlGenerator(INDENTATION_AT_INSERTION_POINT);
        html.table()
                .tr()
                .th("Banco")
                .th("Venta")
                .th("Compra")
                .th("Tipo de Cambio Oficial")
                .closeTr();

        BigDecimal currentOfficialExchangeRate = getCurrentOfficialExchangeRate();

        for (Map.Entry<String, Map<LocalDate, CommercialBankExchangeRate>> bankEntry : exchangeRateByBankAndDate.entrySet()) {
            CommercialBankExchangeRate exchangeRateToday = bankEntry.getValue().get(today);
            CommercialBankExchangeRate exchangeRateYesterday = bankEntry.getValue().get(yesterday);

            if (exchangeRateToday == null) {
                LOGGER.info("El dia de hoy no se encontraron datos de compra/venta para el banco {}", bankEntry.getKey());
                continue;
            }

            String bankLogo = String.format(BANK_LOGO_TEMPLATE, bankEntry.getKey());
            String tendencyImg = "";

            if (exchangeRateYesterday != null) {
                String tendency;
                switch (exchangeRateToday.getSell().compareTo(exchangeRateYesterday.getSell())) {
                    case -1:
                        tendency = "down";
                        break;
                    case 0:
                        tendency = "equal";
                        break;
                    case 1:
                        tendency = "up";
                        break;
                    default:
                        tendency = "";
                }

                tendencyImg = "&nbsp;&nbsp;&nbsp;" + String.format(TENDENCY_INDICATOR, tendency);
            } else {
                tendencyImg = "";
            }

            html.tr()
                    .td(bankLogo)
                    .td(HtmlGenerator.asStrong(exchangeRateToday.getSell(), exchangeRateToday.getBestSellPrice()) + tendencyImg)
                    .td(HtmlGenerator.asStrong(exchangeRateToday.getBuy(), exchangeRateToday.getBestBuyPrice()))
                    .td(currentOfficialExchangeRate)
                    .closeTr();
        }

        html.tfoot()
                .tr()
                .td("<strong>Nota:</strong> Las mejores opciones de compra/venta est&#225;n marcadas con <strong>negrita</strong>",
                        4)
                .closeTr()
                .closeTfoot()
                .closeTable();

        return html.asHtml();
    }

    public void sendEmailWithExchangeRateData(String httpUrl) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<UserSubscription> subscriptions = userSubscriptionRepository.findActiveSubsciptionsWithNoEmailSent(start, end);

        if (!subscriptions.isEmpty()) {
            String htmlTableWithData = constructHtmlTableWithExchangeRateData();

            if (htmlTableWithData.isEmpty()) {
                return;
            }

            String today = NIO_DATE_FORMATTER.format(LocalDate.now());

            for (UserSubscription subscription : subscriptions) {
                LOGGER.info("Enviando correo electronico a [{}]", subscription.getEmail());

                String url = UriComponentsBuilder.fromHttpUrl(httpUrl).path("/deactivate/{email}/{token}").buildAndExpand(
                        subscription.getEmail(), subscription.getToken()).encode().toUriString();
                String emailContent = EMAIL_TEMPLATE_FOR_EXCHANGE_RATE_DATA.asHtml(today, htmlTableWithData, url);

                LOGGER.debug(HORIZONTAL_RULE);
                LOGGER.debug("Email:");
                LOGGER.debug(emailContent);
                LOGGER.debug(HORIZONTAL_RULE);

                MimeMessage message = mailSender.createMimeMessage();

                try {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
                    messageHelper.setTo(subscription.getEmail());
                    messageHelper.setSubject("Datos de la compra/venta de dolares en los Bancos Comerciales");
                    messageHelper.setText(emailContent, true);

                    for (Map.Entry<String, ClassPathResource> logoEntry : logos.entrySet()) {
                        messageHelper.addInline(logoEntry.getKey(), logoEntry.getValue());
                    }

                    mailSender.send(message);

                    EmailTask emailTask = new EmailTask();
                    emailTask.setUserSubscription(subscription);
                    emailTask.setDate(LocalDateTime.now());
                    emailTaskRepository.save(emailTask);
                } catch (MessagingException | MailException me) {
                    String error = String.format("Ha ocurrido un error durante el envio del correo electronico a la direccion [%s]",
                            subscription.getEmail());
                    LOGGER.error(error, me);
                }
            }
        } else {
            LOGGER.info("No se encontraron usuarios activos pendientes de enviar correo");
        }
    }

}

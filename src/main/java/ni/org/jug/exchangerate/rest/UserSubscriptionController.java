package ni.org.jug.exchangerate.rest;

import ni.org.jug.exchangerate.logic.EmailSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;

/**
 *
 * @author aalaniz
 */
@RestController
@RequestMapping("/api/userSubscriptions")
public class UserSubscriptionController {

    @Autowired
    EmailSubscriptionService emailSubscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createSubscription(@RequestParam String fullName, @RequestParam String email) throws MessagingException {
        emailSubscriptionService.createSubscription(fullName, email, ServletUriComponentsBuilder.fromCurrentRequest());
    }

    @GetMapping("/activate/{email}/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateSubscription(@PathVariable String email, @PathVariable String token) {
        emailSubscriptionService.activateSubscription(email, token);
    }

    @GetMapping("/deactivate/{email}/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateSubscription(@PathVariable String email, @PathVariable String token) {
        emailSubscriptionService.deactivateSubscription(email, token);
    }

    @PostMapping("/email")
    public String sendEmail() {
        String currentEndpointPath = "/email";
        String currentPath = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        String parentPath = currentPath.substring(0, currentPath.length() - currentEndpointPath.length());
        emailSubscriptionService.sendEmailWithExchangeRateData(parentPath);
        return "Se inicio el proceso de envio de correos";
    }

}

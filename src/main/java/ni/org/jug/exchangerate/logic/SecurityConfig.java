package ni.org.jug.exchangerate.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author aalaniz
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ENV_USER_NAME = "APP_USER";
    private static final String ENV_USER_PASSWORD = "APP_PASSWORD";
    private static final String DEFAULT_CREDENTIALS = "jugni";

    @Autowired
    Environment environment;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String user = environment.getProperty(ENV_USER_NAME, DEFAULT_CREDENTIALS);
        String password = environment.getProperty(ENV_USER_PASSWORD, DEFAULT_CREDENTIALS);

        auth.inMemoryAuthentication()
                .withUser(user).password(passwordEncoder.encode(password)).roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/banks/*/cookies").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/banks/*/cookies/*").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/banks/*/cookies/").authenticated()
                .anyRequest().permitAll()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

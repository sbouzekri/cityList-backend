package co.thecodest.citylist.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/api/account").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/cities/**").hasAuthority("ROLE_ALLOW_EDIT")
                .antMatchers("/api/**").authenticated()
                .and().httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").authorities("ROLE_ALLOW_EDIT", "ROLE_ALLOW_DELETE", "ROLE_ALLOW_CREATE");
        auth.inMemoryAuthentication().withUser("user").password("{noop}user").authorities("ROLE_ALLOW_DELETE", "ROLE_ALLOW_CREATE");
    }
}

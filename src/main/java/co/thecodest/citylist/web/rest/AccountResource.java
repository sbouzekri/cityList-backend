package co.thecodest.citylist.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountResource {
    @GetMapping(path = "")
    public boolean authenticated() {
        return true;
    }
}

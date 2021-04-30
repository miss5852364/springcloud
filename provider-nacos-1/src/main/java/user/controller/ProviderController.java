package user.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

    @Value("${server.port}")
    String value;

    @GetMapping(value = "/echo/{string}")
    public String echo(@PathVariable String string) {
        return "hi the first " + string + ":" + value;
    }
}

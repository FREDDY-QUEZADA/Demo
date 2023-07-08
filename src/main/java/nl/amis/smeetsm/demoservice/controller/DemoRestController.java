package nl.amis.smeetsm.demoservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * DemoRestController.
 */
@RestController
public class DemoRestController {
    /**
     * @return Hi there.
     */
    @GetMapping("/rest/demo")
    public String demoReply() {
        return (new StringBuilder().append("Hi there")).toString();
    }

    @GetMapping("/rest/hello")
    public String hello(@RequestParam String name){
        return "Hello " + name;
    }
}

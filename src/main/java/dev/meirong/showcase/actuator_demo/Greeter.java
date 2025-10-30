package dev.meirong.showcase.actuator_demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Greeter {
    
    @GetMapping("/greet")
	public String greet() {
		return "Hello, Actuator!";
	}
}

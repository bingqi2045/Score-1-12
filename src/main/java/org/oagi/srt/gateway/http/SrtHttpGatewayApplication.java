package org.oagi.srt.gateway.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "org.oagi.srt")
@RestController
public class SrtHttpGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrtHttpGatewayApplication.class, args);
    }
}

package com.practice.samplespringboot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentinel AML Platform API")
                        .version("1.0.0")
                        .description("Real-Time Money Laundering Detection Platform API providing transaction ingestion, detection engine evaluation, alert dispatching, and dynamic rule configuration.")
                        .contact(new Contact().name("Sentinel AML Engineering Team").email("compliance@meridiantrust.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}

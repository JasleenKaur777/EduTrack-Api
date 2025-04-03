package com.tka.sams.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tka.sams.api.controller")) // Change this if needed
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "EduTrack API",
                "API documentation for EduTrack application",
                "1.0",
                "https://api.edutrack.com/terms",
                new Contact("Jasleen Team", "https://api.edutrack.com", "jasleen2229006@gmail.com"),
                "API License",
                "https://api.edutrack.com/license",
                Collections.emptyList()
        );
    }
}

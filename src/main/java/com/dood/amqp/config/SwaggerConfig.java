package com.dood.amqp.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/error")))//exclude spring error controllers// for now
                .build()
                .directModelSubstitute(LocalDate.class, java.sql.Date.class) //from springfox docs
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Springboot Elasticsearch Demo API",
                "Various ways to exercise the Elasticsearch",
                "1.0",
                "TOS",
                new Contact("coderdude1", "https://github.com/coderdude1/ElasticSearchDemo", "email"),
                "License: APL 2.0",
                "License URL: https://github.com/coderdude1/ElasticSearchDemo/blob/develop/License.md");
    }
}

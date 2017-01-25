package com.dood.amqp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Simple controller to redirect root url requests to the swagger API
 */
@Controller //Note if this is @RestController redirect will not work
@ApiIgnore //No need to have swagger document this controller
public class RootController {
    public static final String REDIRECT_SWAGGER_UI_HTML = "redirect:/swagger-ui.html";

    @RequestMapping({"/"})
    public String redirectToSwagger() {
        return REDIRECT_SWAGGER_UI_HTML;
    }
}

package com.amazonaws.cognito.devauthsample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegisterController {

    @RequestMapping("/")
    public String defaultHome() {
        return "register";
    }

    @RequestMapping("/jsp/register")
    public String register() {
        return "register";
    }

    @RequestMapping("/jsp/register-failure")
    public String registerFailure() {
        return "register-failure";
    }

    @RequestMapping("/jsp/register-success")
    public String registerSuccess() {
        return "register-success";
    }

    @RequestMapping("/jsp/mpe")
    public String mpe() {
        return "mpe";
    }

    @RequestMapping("/jsp/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/jsp/error")
    public String error() {
        return "error";
    }

    @RequestMapping("/jsp/pagenotfounderror")
    public String pageNotFound() {
        return "pagenotfounderror";
    }

    @RequestMapping("/jsp/validation_error")
    public String validationError() {
        return "validation_error";
    }
}

package com.security.demo.controller;

import com.security.demo.security.oidc.AppOidcUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/menu");
    }

    @GetMapping("/menu")
    public String menu(Authentication authentication, Model model) {
        model.addAttribute("authentication", authentication);
        if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken
                && oauth2AuthenticationToken.getPrincipal() instanceof AppOidcUser appOidcUser) {
            model.addAttribute("appOidcUser", appOidcUser);
        }
        return "menu";
    }
}

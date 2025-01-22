package com.ilyadudnikov.cloudfilestorage.controllers;

import com.ilyadudnikov.cloudfilestorage.dto.UserRegistrationDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.PasswordsDoNotMatchException;
import com.ilyadudnikov.cloudfilestorage.exeptions.UserWithThisNameAlreadyExistsException;
import com.ilyadudnikov.cloudfilestorage.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        if (userIsAuthenticated()) {
            return "redirect:/";
        }

        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup(@ModelAttribute("user") UserRegistrationDto userRegistrationDto) {
        if (userIsAuthenticated()) {
            return "redirect:/";
        }

        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signupProcessing(@ModelAttribute("user") @Valid UserRegistrationDto userRegistrationDto,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            userService.register(userRegistrationDto);
        } catch (UserWithThisNameAlreadyExistsException e) {
            bindingResult.rejectValue("username", "error.username", e.getMessage());
            return "auth/signup";
        } catch (PasswordsDoNotMatchException e) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", e.getMessage());
            return "auth/signup";
        }

        return "redirect:/auth/login";
    }

    private boolean userIsAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }
}

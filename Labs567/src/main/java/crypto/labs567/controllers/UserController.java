package crypto.labs567.controllers;

import crypto.labs567.dto.UserInfoDto;
import crypto.labs567.dto.UserRegistrationDto;
import crypto.labs567.service.UserPersistenceService;
import crypto.labs567.service.security.UserPrincipal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserPersistenceService userPersistenceService;

    public UserController(UserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    @PostMapping
    public ModelAndView register(@Validated UserRegistrationDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return registrationErrors(bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList()));
        }

        try {
            userPersistenceService.saveUser(userDto);
            return new ModelAndView("login");
        } catch (DataIntegrityViolationException e) {
            return registrationErrors(Collections.singletonList(e.getMessage()));
        }
    }

    private ModelAndView registrationErrors(List<String> errors) {
        ModelAndView errorMV = new ModelAndView("register");
        errorMV.getModel().put("errors", errors);
        return errorMV;
    }

    @GetMapping
    public ModelAndView info(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ModelAndView mv = new ModelAndView("info");
        UserInfoDto userInfo = userPersistenceService.getUserInfo(userPrincipal.getUsername());
        mv.getModel().put("userInfo", userInfo);
        return mv;
    }
}

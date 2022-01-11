package crypto.labs567.controllers;

import crypto.labs567.dto.UserDto;
import crypto.labs567.service.UserPersistenceService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
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
    public ModelAndView register(@Validated UserDto userDto, BindingResult bindingResult) {
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
}

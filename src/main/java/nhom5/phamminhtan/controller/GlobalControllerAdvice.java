package nhom5.phamminhtan.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.service.AuthenticationFacade;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    
    private final AuthenticationFacade authenticationFacade;
    
    @ModelAttribute
    public void addUserAttributes(Model model) {
        var user = authenticationFacade.getCurrentUser();
        if (user != null) {
            model.addAttribute("currentUserName", user.getFullName());
            model.addAttribute("currentUserEmail", user.getEmail());
            model.addAttribute("currentUser", user);
        }
    }
}

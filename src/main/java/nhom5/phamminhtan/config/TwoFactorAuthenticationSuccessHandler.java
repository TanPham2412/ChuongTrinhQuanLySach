package nhom5.phamminhtan.config;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.service.UserService;

@Component
public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final UserService userService;
    
    public TwoFactorAuthenticationSuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        
        // Find user by username or email
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElse(null);
        
        if (user != null && user.getTwoFactorEnabled() != null && user.getTwoFactorEnabled()) {
            // Store user info in session for 2FA verification
            HttpSession session = request.getSession();
            session.setAttribute("2fa_required", true);
            session.setAttribute("2fa_username", username);
            
            // Redirect to 2FA verification page
            response.sendRedirect("/2fa/verify");
        } else {
            // No 2FA required, proceed to default success URL
            response.sendRedirect("/books");
        }
    }
}

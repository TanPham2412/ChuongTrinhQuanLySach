package nhom5.phamminhtan.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
        
        logger.error("OAuth2 Authentication Failed!", exception);
        logger.error("Exception type: {}", exception.getClass().getName());
        logger.error("Exception message: {}", exception.getMessage());
        
        if (exception.getCause() != null) {
            logger.error("Caused by: {}", exception.getCause().getClass().getName());
            logger.error("Cause message: {}", exception.getCause().getMessage());
            exception.getCause().printStackTrace();
        }
        
        setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}

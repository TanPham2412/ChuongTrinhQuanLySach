package nhom5.phamminhtan.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationFacade {
    
    private final UserRepository userRepository;
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        // Nếu đăng nhập bằng username/password
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        
        // Nếu đăng nhập bằng OAuth2 (Google, Facebook...)
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            String email = oauth2User.getAttribute("email");
            return userRepository.findByEmail(email).orElse(null);
        }
        
        return null;
    }
    
    public String getCurrentUserName() {
        User user = getCurrentUser();
        return user != null ? user.getFullName() : "Guest";
    }
    
    public String getCurrentUserEmail() {
        User user = getCurrentUser();
        return user != null ? user.getEmail() : "";
    }
}

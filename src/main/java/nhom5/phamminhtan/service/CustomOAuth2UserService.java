package nhom5.phamminhtan.service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.AuthProvider;
import nhom5.phamminhtan.model.Role;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.repository.RoleRepository;
import nhom5.phamminhtan.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            // Lấy thông tin từ OAuth2
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String providerId = oauth2User.getAttribute("sub"); // Google ID
            String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
            
            logger.info("OAuth2 Login attempt - Email: {}, Name: {}, Provider: {}", email, name, registrationId);
            
            // Tìm hoặc tạo user
            User user = processOAuth2User(email, name, providerId, registrationId);
            
            logger.info("OAuth2 User processed - ID: {}, Email: {}, Roles: {}", 
                       user.getId(), user.getEmail(), user.getRoles().size());
            
            // Chuyển đổi roles thành authorities
            Collection<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());
            
            // Trả về OAuth2User với authorities từ database
            return new DefaultOAuth2User(
                    authorities,
                    oauth2User.getAttributes(),
                    "email"  // Key để lấy principal name
            );
        } catch (Exception e) {
            logger.error("Error processing OAuth2 user", e);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + e.getMessage());
        }
    }
    
    private User processOAuth2User(String email, String name, String providerId, String registrationId) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        User user;
        if (userOptional.isPresent()) {
            // Cập nhật thông tin nếu user đã tồn tại
            user = userOptional.get();
            user.setFullName(name);
            user.setProviderId(providerId);
            logger.info("Updating existing user: {}", email);
        } else {
            // Tạo user mới
            logger.info("Creating new OAuth2 user: {}", email);
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Dùng email làm username
            user.setFullName(name);
            user.setPassword(null); // Không cần password cho OAuth2
            user.setEnabled(true);
            user.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            user.setProviderId(providerId);
            
            // Gán role USER mặc định
            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role USER không tồn tại"));
            user.getRoles().add(userRole);
            logger.info("Added ROLE_USER to new user");
        }
        
        User savedUser = userRepository.save(user);
        logger.info("User saved successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
}

package nhom5.phamminhtan.service;

import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.model.AuthProvider;
import nhom5.phamminhtan.model.Role;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.repository.RoleRepository;
import nhom5.phamminhtan.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public User registerUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Role USER không tồn tại!"));
        roles.add(userRole);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
}

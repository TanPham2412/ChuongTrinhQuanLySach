package nhom5.phamminhtan.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Size(min = 3, max = 100, message = "Username phải từ 3-100 ký tự")
    @Column(unique = true, nullable = false)
    private String username;
    
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    @Column(nullable = true)
    private String password;
    
    @Email(message = "Email không hợp lệ")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String fullName;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    
    private String providerId;
}

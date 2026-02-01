package nhom5.phamminhtan.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.dto.TwoFactorRequest;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.service.CustomUserDetailsService;
import nhom5.phamminhtan.service.TwoFactorService;
import nhom5.phamminhtan.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final TwoFactorService twoFactorService;
    private final CustomUserDetailsService userDetailsService;
    
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/2fa/verify")
    public String show2FAVerification(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Boolean twoFARequired = (Boolean) session.getAttribute("2fa_required");
        
        if (twoFARequired == null || !twoFARequired) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không yêu cầu xác thực 2 lớp!");
            return "redirect:/login";
        }
        
        model.addAttribute("twoFactorRequest", new TwoFactorRequest());
        return "2fa-verify";
    }
    
    @PostMapping("/2fa/verify")
    public String verify2FA(@Valid @ModelAttribute("twoFactorRequest") TwoFactorRequest request,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã xác thực không hợp lệ!");
            return "redirect:/2fa/verify";
        }
        
        String username = (String) session.getAttribute("2fa_username");
        if (username == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc đã hết hạn!");
            return "redirect:/login";
        }
        
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElse(null);
        
        if (user == null || user.getTwoFactorEnabled() == null || !user.getTwoFactorEnabled()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Người dùng không hợp lệ!");
            return "redirect:/login";
        }
        
        // Verify the 2FA code
        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã xác thực không chính xác!");
            return "redirect:/2fa/verify";
        }
        
        // 2FA verification successful - complete the authentication
        session.removeAttribute("2fa_required");
        session.removeAttribute("2fa_username");
        
        // Re-authenticate the user with full access
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        return "redirect:/books";
    }
    

}

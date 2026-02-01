package nhom5.phamminhtan.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.zxing.WriterException;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nhom5.phamminhtan.dto.ChangePasswordRequest;
import nhom5.phamminhtan.dto.TwoFactorRequest;
import nhom5.phamminhtan.model.User;
import nhom5.phamminhtan.service.TwoFactorService;
import nhom5.phamminhtan.service.UserService;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorService twoFactorService;
    
    private String getUsername(Principal principal) {
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }
        return principal.getName();
    }
    
    @GetMapping
    public String showProfile(Principal principal, Model model) {
        String username = getUsername(principal);
        
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("user", user);
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());
        model.addAttribute("twoFactorRequest", new TwoFactorRequest());
        
        return "profile";
    }
    
    @PostMapping("/change-password")
    public String changePassword(Principal principal,
                                @Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest request,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin!");
            return "redirect:/profile";
        }
        
        String username = getUsername(principal);
        
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user has password (OAuth users may not have password)
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Tài khoản đăng nhập bằng " + user.getProvider() + " không thể đổi mật khẩu!");
            return "redirect:/profile";
        }
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không chính xác!");
            return "redirect:/profile";
        }
        
        // Check if new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận không khớp!");
            return "redirect:/profile";
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateUser(user);
        
        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
    
    @GetMapping("/2fa/setup")
    public String setup2FA(Principal principal, 
                          Model model,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        
        String username = getUsername(principal);
        
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getTwoFactorEnabled() != null && user.getTwoFactorEnabled()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xác thực 2 lớp đã được bật!");
            return "redirect:/profile";
        }
        
        try {
            TwoFactorService.TwoFactorSetupData setupData = 
                twoFactorService.generateSetupData(user.getUsername());
            
            // Store secret in session temporarily
            session.setAttribute("tempTwoFactorSecret", setupData.getSecret());
            
            model.addAttribute("qrCodeImage", setupData.getQrCodeImage());
            model.addAttribute("secret", setupData.getSecret());
            model.addAttribute("twoFactorRequest", new TwoFactorRequest());
            
            return "2fa-setup";
        } catch (WriterException | IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Không thể tạo mã QR. Vui lòng thử lại!");
            return "redirect:/profile";
        }
    }
    
    @PostMapping("/2fa/enable")
    public String enable2FA(Principal principal,
                           @Valid @ModelAttribute("twoFactorRequest") TwoFactorRequest request,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã xác thực không hợp lệ!");
            return "redirect:/profile/2fa/setup";
        }
        
        String tempSecret = (String) session.getAttribute("tempTwoFactorSecret");
        if (tempSecret == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên làm việc đã hết hạn!");
            return "redirect:/profile";
        }
        
        // Verify the code
        if (!twoFactorService.verifyCode(tempSecret, request.getCode())) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Mã xác thực không chính xác! Vui lòng thử lại.");
            return "redirect:/profile/2fa/setup";
        }
        
        String username = getUsername(principal);
        
        // Enable 2FA for user
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setTwoFactorEnabled(true);
        user.setTwoFactorSecret(tempSecret);
        userService.updateUser(user);
        
        // Clear temp secret from session
        session.removeAttribute("tempTwoFactorSecret");
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "Xác thực 2 lớp đã được bật thành công!");
        return "redirect:/profile";
    }
    
    @PostMapping("/2fa/disable")
    public String disable2FA(Principal principal,
                            @ModelAttribute("twoFactorRequest") TwoFactorRequest request,
                            RedirectAttributes redirectAttributes) {
        
        String username = getUsername(principal);
        
        User user = userService.findByUsername(username)
            .or(() -> userService.findByEmail(username))
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getTwoFactorEnabled() == null || !user.getTwoFactorEnabled()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xác thực 2 lớp chưa được bật!");
            return "redirect:/profile";
        }
        
        // Verify the code before disabling
        if (request.getCode() == null || 
            !twoFactorService.verifyCode(user.getTwoFactorSecret(), request.getCode())) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Mã xác thực không chính xác!");
            return "redirect:/profile";
        }
        
        // Disable 2FA
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userService.updateUser(user);
        
        redirectAttributes.addFlashAttribute("successMessage", 
            "Xác thực 2 lớp đã được tắt!");
        return "redirect:/profile";
    }
}

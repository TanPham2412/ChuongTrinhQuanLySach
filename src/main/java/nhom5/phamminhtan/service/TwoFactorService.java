package nhom5.phamminhtan.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TwoFactorService {
    
    private final GoogleAuthenticator googleAuthenticator;
    
    public TwoFactorService() {
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder configBuilder = 
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
        this.googleAuthenticator = new GoogleAuthenticator(configBuilder.build());
    }
    
    /**
     * Generate a new secret key for 2FA using Base32 encoding
     */
    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes).replaceAll("=", "");
    }
    
    /**
     * Generate QR code URL for Google Authenticator app
     */
    public String generateQrCodeUrl(String username, String secret, String issuer) {
        try {
            return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                URLEncoder.encode(issuer, "UTF-8"),
                URLEncoder.encode(username, "UTF-8"),
                secret,
                URLEncoder.encode(issuer, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode URL", e);
        }
    }
    
    /**
     * Generate QR code image as base64 string
     */
    public String generateQrCodeImage(String qrCodeUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 250, 250);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    
    /**
     * Verify the OTP code provided by user
     */
    public boolean verifyCode(String secret, int code) {
        try {
            return googleAuthenticator.authorize(secret, code);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate setup data for enabling 2FA
     */
    public TwoFactorSetupData generateSetupData(String username) throws WriterException, IOException {
        String secret = generateSecretKey();
        String issuer = "Nhà Sách Online";
        String qrCodeUrl = generateQrCodeUrl(username, secret, issuer);
        String qrCodeImage = generateQrCodeImage(qrCodeUrl);
        
        return new TwoFactorSetupData(secret, qrCodeUrl, qrCodeImage);
    }
    
    /**
     * Data class for 2FA setup
     */
    public static class TwoFactorSetupData {
        private final String secret;
        private final String qrCodeUrl;
        private final String qrCodeImage;
        
        public TwoFactorSetupData(String secret, String qrCodeUrl, String qrCodeImage) {
            this.secret = secret;
            this.qrCodeUrl = qrCodeUrl;
            this.qrCodeImage = qrCodeImage;
        }
        
        public String getSecret() {
            return secret;
        }
        
        public String getQrCodeUrl() {
            return qrCodeUrl;
        }
        
        public String getQrCodeImage() {
            return qrCodeImage;
        }
    }
}

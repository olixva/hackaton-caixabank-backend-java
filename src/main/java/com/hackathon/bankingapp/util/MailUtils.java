package com.hackathon.bankingapp.util;

import com.hackathon.bankingapp.exceptions.EmailSendingException;
import com.hackathon.bankingapp.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailUtils {

    private final JavaMailSender javaMailSender;

    public void sendPasswordResetEmail(User user, String otpToken) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());

            String subject = "Your OTP for password reset";

            helper.setText("OTP:" + otpToken);
            helper.setSubject(subject);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Error sending email to " + user.getEmail());
        }
    }

    public void sendAssetPurchaseMail(User user, String assetSymbol, BigDecimal assetQuantity, BigDecimal cashAmount, BigDecimal currentHoldings, StringBuilder assetSummaryBuilder, BigDecimal netWorth) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());

            helper.setText(
                    String.format(Locale.US,
                            """
                                    Dear Nuwe Test,
                                    
                                    You have successfully purchased %.2f units of %s for a total amount of $%.2f.
                                    
                                    Current holdings of %s: %.2f units
                                    
                                    Summary of current assets:
                                    %s
                                    Account Balance: $%.2f
                                    Net Worth: $%.2f
                                   
                                    Thank you for using our investment services.
                                    
                                    Best Regards,
                                    Investment Management Team
                                    """,
                            assetQuantity, // Amount of the asset purchased
                            assetSymbol, // Simbol of the asset purchased
                            cashAmount, // Total amount of the purchase
                            assetSymbol, // Simbol of the asset purchased
                            currentHoldings, // Current holdings of the asset
                            assetSummaryBuilder.toString(), // Asset summary
                            user.getAccount().getBalance(), // Account balance
                            netWorth // Net worth
                    )
            );

            helper.setSubject("Investment Purchase Confirmation");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Error sending email to " + user.getEmail());
        }
    }

    public void sendAssetSaleMail(User user, String assetSymbol, BigDecimal assetQuantitySold, BigDecimal totalGainLoss, BigDecimal currentHoldings, StringBuilder assetSummaryBuilder, BigDecimal accountBalance, BigDecimal netWorth) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());

            helper.setText(
                    String.format(Locale.US,
                            """
                                    Dear Nuwe Test,
                                    
                                    You have successfully sold %.2f units of %s.
                                    
                                    Total Gain/Loss: $%.2f
                                    
                                    Current holdings of %s: %.2f units
                                    
                                    Summary of current assets:
                                    %s
                                    Account Balance: $%.2f
                                    Net Worth: $%.2f
                                   
                                    Thank you for using our investment services.
                                    
                                    Best Regards,
                                    Investment Management Team
                                    """,
                            assetQuantitySold, // Amount of the asset sold
                            assetSymbol, // Simbol of the asset sold
                            totalGainLoss, // Total gain/loss
                            assetSymbol, // Simbol of the asset sold
                            currentHoldings, // Current holdings of the asset
                            assetSummaryBuilder.toString(), // Asset summary
                            accountBalance, // Account balance
                            netWorth // Net worth
                    )
            );

            helper.setSubject("Investment Sale Confirmation");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Error sending email to " + user.getEmail());
        }
    }
}
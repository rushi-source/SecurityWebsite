package com.example.SecurityWebsite.Service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.SecurityWebsite.Model.ContactRequest;
import com.example.SecurityWebsite.Model.ResumeRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Resume Email
    public void sendResumeEmail(ResumeRequest request, MultipartFile file)
            throws MessagingException, IOException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("rushikeshgadekar491@gmail.com");
        helper.setSubject("📄 New Resume Submission");

        String body = """
                New Resume Submission Details:

                Name: %s
                Email: %s
                Phone: %s
                Service: %s
                Job Position: %s
                """.formatted(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getService(),
                request.getJobPosition()
        );

        helper.setText(body);

        // ✅ File Validation
        if (file != null && !file.isEmpty()) {

            // Allow only PDF or DOC/DOCX
            String fileName = file.getOriginalFilename().toLowerCase();

            if (!(fileName.endsWith(".pdf") ||
                  fileName.endsWith(".doc") ||
                  fileName.endsWith(".docx"))) {

                throw new IllegalArgumentException(
                        "Only PDF, DOC, DOCX files are allowed");
            }

            // Max file size 5MB
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "File size must be less than 5MB");
            }

            helper.addAttachment(
                    file.getOriginalFilename(),
                    new ByteArrayResource(file.getBytes())
            );
        }

        mailSender.send(message);
    }

    // ✅ Contact Email
    public void sendContactEmail(ContactRequest request)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo("yourgmail@gmail.com");
        helper.setSubject("📩 New Contact Message");

        String body = """
                New Contact Message:

                Name: %s
                Email: %s
                Phone: %s
                Service: %s
                Message:
                %s
                """.formatted(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getService(),
                request.getMessage()
        );

        helper.setText(body);

        mailSender.send(message);
    }
}

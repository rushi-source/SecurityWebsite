package com.example.SecurityWebsite.Service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.SecurityWebsite.Model.ContactRequest;
import com.example.SecurityWebsite.Model.ResumeRequest;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    private final String TO_EMAIL = "rushikeshgadekar491@gmail.com";
    private final String FROM_EMAIL = "rushikeshgadekar491@gmail.com";

    // ===========================
    // ✅ Resume Email (With Attachment)
    // ===========================
    public void sendResumeEmail(ResumeRequest request, MultipartFile file)
            throws IOException {

        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            throw new RuntimeException("SendGrid API key is missing!");
        }

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(TO_EMAIL);
        String subject = "📄 New Resume Submission";

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

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        // ✅ File Validation & Attachment
        if (file != null && !file.isEmpty()) {

            String fileName = file.getOriginalFilename().toLowerCase();

            if (!(fileName.endsWith(".pdf") ||
                  fileName.endsWith(".doc") ||
                  fileName.endsWith(".docx"))) {

                throw new IllegalArgumentException(
                        "Only PDF, DOC, DOCX files are allowed");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "File size must be less than 5MB");
            }

            Attachments attachment = new Attachments();
            attachment.setFilename(file.getOriginalFilename());
            attachment.setType(file.getContentType());
            attachment.setDisposition("attachment");

            String encodedFile = Base64.getEncoder()
                    .encodeToString(file.getBytes());

            attachment.setContent(encodedFile);

            mail.addAttachments(attachment);
        }

        sendEmail(mail);
    }

    // ===========================
    // ✅ Contact Email
    // ===========================
    public void sendContactEmail(ContactRequest request)
            throws IOException {

        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            throw new RuntimeException("SendGrid API key is missing!");
        }

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(TO_EMAIL);
        String subject = "📩 New Contact Message";

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

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        sendEmail(mail);
    }

    // ===========================
    // ✅ Common Send Method
    // ===========================
    private void sendEmail(Mail mail) throws IOException {

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        if (response.getStatusCode() >= 400) {
            throw new RuntimeException(
                    "SendGrid Error: " + response.getStatusCode()
                    + " - " + response.getBody());
        }
    }
}
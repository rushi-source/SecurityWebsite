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

    // ✅ Admin email (where you want to receive submissions)
    private final String ADMIN_EMAIL = "rushikeshgadekar491@gmail.com";

    // ✅ Sender email (must be verified in SendGrid)
    private final String FROM_EMAIL = "rushikeshgadekar491@gmail.com";

    // ===========================
    // ✅ Resume Email (To Admin + Attachment)
    // ===========================
    public void sendResumeEmailToAdmin(ResumeRequest request, MultipartFile file)
            throws IOException {

        validateApiKey();

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(ADMIN_EMAIL);
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

        // ✅ Attachment validation + add file
        addResumeAttachment(mail, file);

        sendEmail(mail);
    }

    // ===========================
    // ✅ Resume Confirmation Email (To User)
    // ===========================
    public void sendResumeConfirmationToUser(ResumeRequest request)
            throws IOException {

        validateApiKey();

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email is missing!");
        }

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(request.getEmail());

        String subject = "✅ Resume Submitted Successfully";

        String body = """
                Hi %s,

                Your resume has been submitted successfully.
                Our team will review your details and contact you soon.

                Thanks & Regards,
                Surya Security Team
                """.formatted(request.getName() != null ? request.getName() : "Candidate");

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        sendEmail(mail);
    }

    // ===========================
    // ✅ Contact Email (To Admin)
    // ===========================
    public void sendContactEmail(ContactRequest request)
            throws IOException {

        validateApiKey();

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(ADMIN_EMAIL);
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
    // ✅ (Optional) Contact Confirmation Email (To User)
    // ===========================
    public void sendContactConfirmationToUser(ContactRequest request)
            throws IOException {

        validateApiKey();

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email is missing!");
        }

        Email from = new Email(FROM_EMAIL);
        Email to = new Email(request.getEmail());

        String subject = "✅ Message Received Successfully";

        String body = """
                Hi %s,

                We received your message successfully.
                Our team will contact you soon.

                Thanks & Regards,
                Surya Security Team
                """.formatted(request.getName() != null ? request.getName() : "Customer");

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        sendEmail(mail);
    }

    // ===========================
    // ✅ Helpers
    // ===========================
    private void validateApiKey() {
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            throw new RuntimeException("SendGrid API key is missing!");
        }
    }

    private void addResumeAttachment(Mail mail, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a valid file");
        }

        String originalName = file.getOriginalFilename();
        String fileName = (originalName == null) ? "" : originalName.toLowerCase();

        if (!(fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx"))) {
            throw new IllegalArgumentException("Only PDF, DOC, DOCX files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        Attachments attachment = new Attachments();
        attachment.setFilename(originalName);
        attachment.setType(file.getContentType());
        attachment.setDisposition("attachment");

        String encodedFile = Base64.getEncoder().encodeToString(file.getBytes());
        attachment.setContent(encodedFile);

        mail.addAttachments(attachment);
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
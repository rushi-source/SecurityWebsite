package com.example.SecurityWebsite.Controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.SecurityWebsite.Model.ContactRequest;
import com.example.SecurityWebsite.Model.ResumeRequest;
import com.example.SecurityWebsite.Service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")




public class FormController {

    @Autowired
    private EmailService emailService;

    @PostMapping(value = "/submit-resume", consumes = {"multipart/form-data"})
    public String submitResume(
            @Valid @ModelAttribute ResumeRequest request,
            @RequestParam("file") MultipartFile file)
            throws MessagingException, IOException {

        if (file == null || file.isEmpty()) {
            return "Please upload a valid file";
        }

        emailService.sendResumeEmail(request, file);

        return "Resume submitted successfully!";
    }

    @PostMapping("/contact")
    public String contact(@Valid @RequestBody ContactRequest request)
            throws MessagingException {

        emailService.sendContactEmail(request);

        return "Message sent successfully!";
    }

    @GetMapping("/")
    public String home() {
        return "Backend is running successfully!";
    }
}

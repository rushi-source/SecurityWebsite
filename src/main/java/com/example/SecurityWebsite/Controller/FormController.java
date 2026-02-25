package com.example.SecurityWebsite.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.example.SecurityWebsite.Model.ContactRequest;
import com.example.SecurityWebsite.Model.ResumeRequest;
import com.example.SecurityWebsite.Service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://surya-blond.vercel.app")
public class FormController {

    @Autowired
    private EmailService emailService;

    // ✅ Resume API
    @PostMapping(value = "/submit-resume", consumes = {"multipart/form-data"})
    public String submitResume(
            @Valid @ModelAttribute ResumeRequest request,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            return "Please upload a valid file";
        }

        emailService.sendResumeEmail(request, file);

        return "Resume submitted successfully!";
    }

    // ✅ Contact API
    @PostMapping("/contact")
    public String contact(@Valid @RequestBody ContactRequest request)
            throws IOException {

        emailService.sendContactEmail(request);

        return "Message sent successfully!";
    }

    // ✅ Health Check
    @GetMapping("/")
    public String home() {
        return "Backend is running successfully!";
    }
}
package com.example.SecurityWebsite.Controller;

import java.io.IOException;
import java.util.Map;

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

    // ==========================
    // ✅ Resume API
    // ==========================
    @PostMapping(value = "/submit-resume", consumes = {"multipart/form-data"})
    public Map<String, String> submitResume(
            @Valid @ModelAttribute ResumeRequest request,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            return Map.of("message", "Please upload a valid file");
        }

        emailService.sendResumeEmail(request, file);

        return Map.of("message", "Resume submitted successfully!");
    }

    // ==========================
    // ✅ Contact API
    // ==========================
    @PostMapping("/contact")
    public Map<String, String> contact(
            @Valid @RequestBody ContactRequest request)
            throws IOException {

        emailService.sendContactEmail(request);

        return Map.of("message", "Message sent successfully!");
    }

    // ==========================
    // ✅ Health Check
    // ==========================
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "Backend is running successfully!");
    }
}
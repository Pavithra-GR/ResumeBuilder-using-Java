
    package com.resume.resumebuilder.controller;

    import com.itextpdf.kernel.pdf.PdfDocument;
    
    import com.itextpdf.kernel.pdf.PdfWriter;
    import com.itextpdf.layout.Document;
    import com.itextpdf.layout.element.Paragraph;
    import com.itextpdf.layout.element.Text;
    import com.itextpdf.layout.properties.TextAlignment;
import com.resume.resumebuilder.entity.Resume;
    import com.resume.resumebuilder.entity.User;
    import com.resume.resumebuilder.repository.ResumeRepository;
    import com.resume.resumebuilder.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

    @Controller
    public class ResumeController {

        @Autowired
        private ResumeRepository resumeRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @GetMapping("/register")
        public String showRegisterForm() {
            return "register";
        }

        @PostMapping("/register")
        public String registerUser(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
            try {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                redirectAttributes.addFlashAttribute("message", "Registration successful! Please log in.");
                return "redirect:/login";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
                return "redirect:/register";
            }
        }

        @GetMapping("/login")
        public String showLoginForm() {
            return "login";
        }


        @GetMapping("/logout")
        public String showLogout() {
        	
            return "logout";
        }  
    

        @GetMapping("/create-resume")
        public String showResumeForm(Model model) {
            model.addAttribute("resume", new Resume());
            return "resume-form";
        }

        @PostMapping("/submit-resume")
        public String handleForm(@ModelAttribute Resume resume, RedirectAttributes redirectAttributes) {
            try {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                resume.setUser(user);
                resumeRepository.save(resume);
                redirectAttributes.addFlashAttribute("message", "Resume created successfully!");
                return "redirect:/home";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error saving resume: " + e.getMessage());
                return "redirect:/create-resume";
            }
        }

        @GetMapping("/home")
        public String showHome(Model model) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            model.addAttribute("username", username);
            return "home";
        }

        @GetMapping("/view-resumes")
        public String viewResumes(Model model) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            List<Resume> resumes = resumeRepository.findByUser(user);
            model.addAttribute("resumes", resumes);
            return "view-resumes";
        }

        @GetMapping("/edit-resume/{id}")
        public String showEditResumeForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
            try {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                Resume resume = resumeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resume not found"));
                if (!resume.getUser().getId().equals(user.getId())) {
                    redirectAttributes.addFlashAttribute("error", "You can only edit your own resumes.");
                    return "redirect:/view-resumes";
                }
                model.addAttribute("resume", resume);
                return "resume-form";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error loading resume: " + e.getMessage());
                return "redirect:/view-resumes";
            }
        }

        @PostMapping("/update-resume")
        public String updateResume(@ModelAttribute Resume resume, RedirectAttributes redirectAttributes) {
            try {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                Resume existingResume = resumeRepository.findById(resume.getId())
                    .orElseThrow(() -> new RuntimeException("Resume not found"));
                if (!existingResume.getUser().getId().equals(user.getId())) {
                    redirectAttributes.addFlashAttribute("error", "You can only edit your own resumes.");
                    return "redirect:/view-resumes";
                }
                existingResume.setFullName(resume.getFullName());
                existingResume.setEmail(resume.getEmail());
                existingResume.setPhone(resume.getPhone());
                existingResume.setLinkedin(resume.getLinkedin());
                existingResume.setEducation(resume.getEducation());
                existingResume.setExperience(resume.getExperience());
                existingResume.setSkills(resume.getSkills());
                existingResume.setProjects(resume.getProjects());
                existingResume.setAchievements(resume.getAchievements());
                existingResume.setCertification(resume.getCertification());
                resumeRepository.save(existingResume);
                redirectAttributes.addFlashAttribute("message", "Resume updated successfully!");
                return "redirect:/view-resumes";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error updating resume: " + e.getMessage());
                return "redirect:/edit-resume/" + resume.getId();
            }
        }

        @PostMapping("/delete-resume/{id}")
        public String deleteResume(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            try {
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                Resume resume = resumeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resume not found"));
                if (!resume.getUser().getId().equals(user.getId())) {
                    redirectAttributes.addFlashAttribute("error", "You can only delete your own resumes.");
                    return "redirect:/view-resumes";
                }
                resumeRepository.delete(resume);
                redirectAttributes.addFlashAttribute("message", "Resume deleted successfully!");
                return "redirect:/view-resumes";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error deleting resume: " + e.getMessage());
                return "redirect:/view-resumes";
            }}
            @GetMapping("/download-resume/{id}")
            public void downloadResume(@PathVariable Long id, HttpServletResponse response, RedirectAttributes redirectAttributes) throws IOException {
                try {
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                    Resume resume = resumeRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Resume not found"));
                    if (!resume.getUser().getId().equals(user.getId())) {
                        throw new RuntimeException("You can only download your own resumes.");
                    }

                    // Set response headers for PDF download
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=resume_" + resume.getFullName().replace(" ", "_") + ".pdf");

                    // Create PDF using iText
                    PdfWriter writer = new PdfWriter(response.getOutputStream());
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf);

                    // Add resume content
                    // Header
                    Paragraph name = new Paragraph(resume.getFullName())
                        .setFontSize(20)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER);
                    document.add(name);

                    Paragraph contact = new Paragraph()
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER);
                    contact.add(new Text("Email: " + resume.getEmail() + " | "));
                    contact.add(new Text("Phone: " + resume.getPhone() + " | "));
                    contact.add(new Text("LinkedIn: " + resume.getLinkedin()));
                    document.add(contact);

                    document.add(new Paragraph("\n"));

                    // Education
                    document.add(new Paragraph("Education").setFontSize(14).setBold());
                    for (String line : resume.getEducation().split("\n")) {
                        document.add(new Paragraph("• " + line).setFontSize(10));
                    }

                    // Experience
                    document.add(new Paragraph("\nExperience").setFontSize(14).setBold());
                    if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
                        for (String line : resume.getExperience().split("\n")) {
                            document.add(new Paragraph("• " + line).setFontSize(10));
                        }
                    } else {
                        document.add(new Paragraph("No experience provided.").setFontSize(10));
                    }

                    // Skills
                    document.add(new Paragraph("\nSkills").setFontSize(14).setBold());
                    for (String line : resume.getSkills().split("\n")) {
                        document.add(new Paragraph("• " + line).setFontSize(10));
                    }

                    // Projects
                    document.add(new Paragraph("\nProjects").setFontSize(14).setBold());
                    for (String line : resume.getProjects().split("\n")) {
                        document.add(new Paragraph("• " + line).setFontSize(10));
                    }

                    // Achievements
                    document.add(new Paragraph("\nAchievements").setFontSize(14).setBold());
                    if (resume.getAchievements() != null && !resume.getAchievements().isEmpty()) {
                        for (String line : resume.getAchievements().split("\n")) {
                            document.add(new Paragraph("• " + line).setFontSize(10));
                        }
                    } else {
                        document.add(new Paragraph("No achievements provided.").setFontSize(10));
                    }

                    // Certifications
                    document.add(new Paragraph("\nCertifications").setFontSize(14).setBold());
                    for (String line : resume.getCertification().split("\n")) {
                        document.add(new Paragraph("• " + line).setFontSize(10));
                    }

                    // Close document
                    document.close();

                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Error generating PDF: " + e.getMessage());
                    response.sendRedirect("/view-resumes");
                }
        }
    }
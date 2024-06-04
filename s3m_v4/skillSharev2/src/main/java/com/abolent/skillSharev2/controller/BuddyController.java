package com.abolent.skillSharev2.controller;


import com.abolent.skillSharev2.model.Buddy;
import com.abolent.skillSharev2.model.MessageDTO;
import com.abolent.skillSharev2.model.Post;
import com.abolent.skillSharev2.model.Skill;
import com.abolent.skillSharev2.repository.BuddyRepository;
import com.abolent.skillSharev2.repository.SkillRepository;
import com.abolent.skillSharev2.service.BuddyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;

@Controller
public class BuddyController {

    @Autowired
    private BuddyService buddyService;

    @Autowired
    private BuddyRepository buddyRepository;

    @Autowired
    private SkillRepository skillRepository;

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) Long skill, Authentication authentication) {
        List<Buddy> buddies = (skill == null) ? buddyRepository.findAll() : buddyRepository.findBySkillsId(skill);
        List<Skill> skills = skillRepository.findAll();
        String email = authentication.getName();
        Buddy buddy = buddyRepository.findByEmail(email).orElse(null);
        model.addAttribute("buddies", buddies);
        model.addAttribute("skills", skills);
        model.addAttribute("buddy", buddy);
        return "home";
    }


    @GetMapping("/post")
    public String showPostForm() {
        return "post";
    }

    @PostMapping("/post")
    public String createPost(@RequestParam String content, Authentication authentication) {
        String email = authentication.getName();
        Buddy buddy = buddyRepository.findByEmail(email).orElse(null);
        if (buddy != null) {
            Post post = new Post();
            post.setContent(content);
            post.setBuddy(buddy);
            buddy.getPosts().add(post);
            buddyRepository.save(buddy);
        }
        return "redirect:/";
    }



    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        Buddy buddy = buddyRepository.findByEmail(email).orElse(null);
        model.addAttribute("buddy", buddy);
        model.addAttribute("skills", skillRepository.findAll());
        return "profile";
    }


    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Buddy updatedBuddy, Authentication authentication) {
        String email = authentication.getName();
        Buddy currentBuddy = buddyRepository.findByEmail(email).orElse(null);
        if (currentBuddy != null) {
            currentBuddy.setBio(updatedBuddy.getBio());
            currentBuddy.setSkills(new HashSet<>(updatedBuddy.getSkills()));
            buddyRepository.save(currentBuddy);
        }
        return "redirect:/profile";
    }


    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication) {
        String email = authentication.getName();
        Buddy currentBuddy = buddyRepository.findByEmail(email).orElse(null);
        if (currentBuddy != null) {
            currentBuddy.getSkills().clear(); // Remove all skill associations
            buddyRepository.save(currentBuddy); // Save the buddy to update the database
            buddyRepository.delete(currentBuddy); // Now delete the buddy
        }
        return "redirect:/login";
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("buddy", new Buddy());
        model.addAttribute("skills", skillRepository.findAll());
        return "register";
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }


    @PostMapping("/register")
    public String registerBuddy(Buddy buddy) {
        buddyService.registerBuddy(buddy);
        return "redirect:/login";
    }



    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }




    @GetMapping("/profile/{email}")
    public String viewBuddyProfile(@PathVariable String email, Model model) {
        Buddy buddy = buddyRepository.findByEmail(email).orElse(null);
        if (buddy != null) {
            model.addAttribute("buddy", buddy);
        }
        return "buddy_profile"; // Name of the Thymeleaf template for viewing a buddy's profile
    }


    @GetMapping("/profile/{email}/message")
    public String showMessageForm(@PathVariable String email, Model model) {
        model.addAttribute("receiverEmail", email);
        return "message_form";
    }

    @PostMapping("/profile/{email}/message")
    public String sendMessage(@PathVariable String email, @RequestParam String content, Authentication authentication) {
        String senderEmail = authentication.getName();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderEmail(senderEmail);
        messageDTO.setReceiverEmail(email);
        messageDTO.setContent(content);

        // Use RestTemplate or WebClient to send the messageDTO to the messaging microservice
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://localhost:8081/messages", messageDTO, MessageDTO.class);

        return "redirect:/profile/" + email;
    }


    @GetMapping("/messages")
    public String viewMessages(Model model, Authentication authentication) {
        String email = authentication.getName();
        // Use RestTemplate or WebClient to retrieve messages from the messaging microservice
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<MessageDTO>> response = restTemplate.exchange(
                "http://localhost:8081/messages/" + email,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MessageDTO>>() {}
        );
        List<MessageDTO> messages = response.getBody();
        model.addAttribute("messages", messages);
        return "messages";
    }


    @PostMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete("http://localhost:8081/messages/" + id);
        return "redirect:/messages/sent";
    }



    @PostMapping("/messages/update/{id}")
    public String updateMessage(@PathVariable Long id, @RequestParam String content, Authentication authentication) {
        String senderEmail = authentication.getName();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(id);
        messageDTO.setSenderEmail(senderEmail);
        messageDTO.setContent(content);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put("http://localhost:8081/messages/" + id, messageDTO);

        return "redirect:/messages/sent";
    }

    @GetMapping("/messages/sent")
    public String viewSentMessages(Model model, Authentication authentication) {
        String email = authentication.getName();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<MessageDTO>> response = restTemplate.exchange(
                "http://localhost:8081/messages/sent/" + email,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<MessageDTO>>() {}
        );
        List<MessageDTO> sentMessages = response.getBody();
        model.addAttribute("sentMessages", sentMessages);
        model.addAttribute("currentEmail", email);
        return "sent_messages";
    }



}

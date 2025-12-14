package ai.synact.synact.controllers;

import ai.synact.synact.dto.ContactMessageRequest;
import ai.synact.synact.services.ContactMessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class ContactSubmitProcess {

    @Autowired
    ContactMessageService contactMessageService;

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactMessageRequest request,
                                BindingResult bindingResult,
                                HttpServletRequest httpRequest,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeMenu", "contact");
            return "/main/contact";
        }

        contactMessageService.save(request, httpRequest);

        // PRG: avoid duplicate insert on refresh
        return "redirect:/web/home";
    }

}

package ai.synact.synact.controllers;

import ai.synact.synact.dto.ContactMessageRequest;
import ai.synact.synact.services.ContactMessageService;
import ai.synact.synact.services.RecaptchaV3Service;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class ContactSubmitProcess {

    private final ContactMessageService contactMessageService;
    private final RecaptchaV3Service recaptchaV3Service;

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactForm") ContactMessageRequest request,
                                BindingResult bindingResult,
                                @RequestParam(name="recaptchaToken", required=false) String recaptchaToken,
                                HttpServletRequest httpRequest,
                                Model model) {

        model.addAttribute("activeMenu", "contact");

        if (bindingResult.hasErrors()) return "/main/contact";

        var rc = recaptchaV3Service.verify(recaptchaToken, "contact_submit", httpRequest);
        if (!rc.isOk()) {
            model.addAttribute("recaptchaError", rc.getMessage());
            return "/main/contact";
        }

        contactMessageService.save(request, httpRequest);
        return "redirect:/web/contact?success=1";
    }

//    @PostMapping("/contact")
//    public String submitContact(@Valid @ModelAttribute("contactForm") ContactMessageRequest request,
//                                BindingResult bindingResult,
//                                HttpServletRequest httpRequest,
//                                Model model) {
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("activeMenu", "contact");
//            return "/main/contact";
//        }
//
//        contactMessageService.save(request, httpRequest);
//
//        // PRG: avoid duplicate insert on refresh
//        return "redirect:/web/home";
//    }

}

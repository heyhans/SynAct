package ai.synact.synact.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class main {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/about")
    public String aboutUs(Model model) {
        model.addAttribute("activeMenu", "about");
        return "/main/about-us";
    }

    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("activeMenu", "blog");
        return "/main/blog";
    }

    @GetMapping("/blog-detail")
    public String blogDetail(Model model) {
        model.addAttribute("activeMenu", "blog");
        return "/main/blog-detail";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("activeMenu", "contact");
        return "/main/contact";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("activeMenu", "home");
        return "/main/home";
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {
        model.addAttribute("activeMenu", "portfolio");
        return "/main/portfolio";
    }

    @GetMapping("/portfolio-detail")
    public String portfolioDetail(Model model) {
        model.addAttribute("activeMenu", "portfolio");
        return "/main/portfolio-detail";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("activeMenu", "services");
        return "/main/services";
    }
}

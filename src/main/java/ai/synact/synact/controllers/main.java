package ai.synact.synact.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/about-us")
    public String aboutUs() {
        return "/main/about-us";
    }

    @GetMapping("/blog")
    public String blog() {
        return "/main/blog";
    }

    @GetMapping("/blog-detail")
    public String blogDetail() {
        return "/main/blog-detail";
    }

    @GetMapping("/contact")
    public String contact() {
        return "/main/contact";
    }

    @GetMapping("/home")
    public String home() {
        return "/main/home";
    }

    @GetMapping("/portfolio")
    public String portfolio() {
        return "/main/portfolio";
    }

    @GetMapping("/portfolio-detail")
    public String portfolioDetail() {
        return "/main/portfolio-detail";
    }

    @GetMapping("/services")
    public String services() {
        return "/main/services";
    }
}

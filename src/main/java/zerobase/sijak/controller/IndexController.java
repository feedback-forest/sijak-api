package zerobase.sijak.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Value("${client-id}")
    private String CLIENT_ID;

    @Value("${redirect-uri}")
    private String REDIRECT_URI;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("CLIENT_ID", CLIENT_ID);
        model.addAttribute("REDIRECT_URI", REDIRECT_URI);
        return "index";
    }

}

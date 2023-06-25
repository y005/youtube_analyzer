package com.example.project01.youtube.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
    @RequestMapping("main")
    public String mainPage() {
        return "index";
    }
}

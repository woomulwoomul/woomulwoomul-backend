package com.woomulwoomul.adminapi.controller.user

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class UserController {

    @GetMapping("/")
    fun home(): String {
        return "home"
    }

    @GetMapping("/dashboard")
    fun dashboard(principal: Principal?): String {
        return "dashboard"
    }
}
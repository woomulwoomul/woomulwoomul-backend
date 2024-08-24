package com.woomulwoomul.adminapi.controller.user

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UserController {

    @GetMapping("/")
    fun home(): String {
        return "home"
    }
}
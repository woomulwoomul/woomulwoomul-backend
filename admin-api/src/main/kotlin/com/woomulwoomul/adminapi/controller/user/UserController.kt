package com.woomulwoomul.adminapi.controller.user

import com.woomulwoomul.adminapi.service.user.UserService
import com.woomulwoomul.core.common.request.PageOffsetRequest
import com.woomulwoomul.core.common.utils.ModelUtils
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/")
    fun getIndex(): String {
        return "index"
    }

    @GetMapping("/home")
    fun getHome(): String {
        return "home"
    }

    @GetMapping("/users")
    fun getAllUsers(@RequestParam(name = "page-from", required = false) pageFrom: Long?,
                    @RequestParam(name = "page-size", required = false) pageSize: Long?,
                    model: Model): String {
        val pageOffsetRequest = PageOffsetRequest.of(pageFrom, pageSize)

        val response = userService.getAllUsers(pageOffsetRequest)

        ModelUtils.addPageDataAttribute(pageOffsetRequest, response, model)

        return "user/users"
    }
}
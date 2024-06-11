package com.woomulwoomul.woomulwoomulbackend.config.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {

        if (request != null) {
            println("=========Request Details=========")
            println("Headers:")
            request.headerNames.asIterator().forEachRemaining { headerName ->
                println("$headerName: ${request.getHeader(headerName)}")
            }

            println("Parameters:")
            request.parameterNames.asIterator().forEachRemaining { paramName ->
                println("$paramName: ${request.getParameter(paramName)}")
            }

            println("Attributes:")
            request.attributeNames.asIterator().forEachRemaining { attrName ->
                println("$attrName: ${request.getAttribute(attrName)}")
            }
        }

        // Print Response details
        if (response != null) {
            println("=========Response Details=========")
            println("Headers:")
            response.headerNames.forEach { headerName ->
                println("$headerName: ${response.getHeader(headerName)}")
            }
            println("Status: ${response.status}")
        }

        // Print Authentication details
        if (authentication != null) {
            println("=========Authentication Details=========")
            println("Principal: ${authentication.principal}")
            println("Authorities: ${authentication.authorities.joinToString(", ") { it.authority }}")
            println("Details: ${authentication.details}")
            println("Credentials: ${authentication.credentials}")
            println("Is Authenticated: ${authentication.isAuthenticated}")
            println("Name: ${authentication.name}")
        }
    }

}
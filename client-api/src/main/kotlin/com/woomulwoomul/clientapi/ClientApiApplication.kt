package com.woomulwoomul.clientapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.woomulwoomul.clientapi", "com.woomulwoomul.core"])
class ClientApiApplication

fun main(args: Array<String>) {
	runApplication<ClientApiApplication>(*args)
}

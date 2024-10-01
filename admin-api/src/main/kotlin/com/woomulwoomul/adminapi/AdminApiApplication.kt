package com.woomulwoomul.adminapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.woomulwoomul.adminapi", "com.woomulwoomul.core"])
class AdminApiApplication

fun main(args: Array<String>) {
	runApplication<AdminApiApplication>(*args)
}

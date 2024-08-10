package com.woomulwoomul.adminserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.woomulwoomul.adminserver", "com.woomulwoomul.core"])
class AdminServerBackendApplication

fun main(args: Array<String>) {
	runApplication<AdminServerBackendApplication>(*args)
}

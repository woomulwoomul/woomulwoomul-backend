package com.woomulwoomul.clientserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.woomulwoomul.clientserver", "com.woomulwoomul.core"])
class ClientServerApplication

fun main(args: Array<String>) {
	runApplication<ClientServerApplication>(*args)
}

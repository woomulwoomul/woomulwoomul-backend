package com.woomulwoomul.batchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.woomulwoomul.batchapi", "com.woomulwoomul.core"])
class BatchApiApplication

fun main(args: Array<String>) {
    runApplication<BatchApiApplication>(*args)
}
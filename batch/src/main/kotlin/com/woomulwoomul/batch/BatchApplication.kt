package com.woomulwoomul.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.woomulwoomul.batch", "com.woomulwoomul.core"])
class BatchApplication

fun main(args: Array<String>) {
    runApplication<BatchApplication>(*args)
}
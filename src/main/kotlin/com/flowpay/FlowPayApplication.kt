package com.flowpay

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlowPayApplication

fun main(args: Array<String>) {
    runApplication<FlowPayApplication>(*args)
}

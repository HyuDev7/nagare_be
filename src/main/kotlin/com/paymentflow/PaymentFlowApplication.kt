package com.paymentflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentFlowApplication

fun main(args: Array<String>) {
    runApplication<PaymentFlowApplication>(*args)
}

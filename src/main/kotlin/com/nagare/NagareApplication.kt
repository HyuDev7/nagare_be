package com.nagare

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NagareApplication

fun main(args: Array<String>) {
    runApplication<NagareApplication>(*args)
}

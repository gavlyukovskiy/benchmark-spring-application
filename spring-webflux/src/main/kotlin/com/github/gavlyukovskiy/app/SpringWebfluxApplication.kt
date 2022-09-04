package com.github.gavlyukovskiy.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<BenchmarkSpringWebfluxApplication>(*args)
}

@SpringBootApplication
class BenchmarkSpringWebfluxApplication

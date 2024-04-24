package br.com.pratofeito

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["br.com.pratofeito.*", "br.com.pratofeito.courier.domain"])
open class PratoFeitoWebSocketApplication

fun main(args: Array<String>) {
    runApplication<PratoFeitoWebSocketApplication>(*args)
}
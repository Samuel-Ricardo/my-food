package br.com.pratofeito

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PratoFeitoWebSocketApplication

fun main(args: Array<String>) {
    runApplication<PratoFeitoWebSocketApplication>(*args)
}
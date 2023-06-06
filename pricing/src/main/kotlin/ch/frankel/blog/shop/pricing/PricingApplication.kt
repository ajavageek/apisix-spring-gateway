package ch.frankel.blog.shop.pricing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.function.server.*

@SpringBootApplication
class PricingApplication

class PriceHandler(private val repository: PriceRepository) {

    suspend fun prices(req: ServerRequest): ServerResponse {
        val idsString = req.queryParam("id")
        return idsString.orElse(null)?.let {
            val ids = it.split(',').mapNotNull { id -> id.toLongOrNull() }
            val prices = repository.findAllById(ids)
            ServerResponse.ok().bodyAndAwait(prices)
        } ?: ServerResponse.ok().bodyAndAwait(repository.findAll())
    }

    suspend fun price(req: ServerRequest): ServerResponse {
        val idString = req.pathVariable("id")
        val id = idString.toLongOrNull()
            ?: return ServerResponse.badRequest().bodyValueAndAwait("$idString is not a valid product ID")
        return repository.findById(id)?.let {
            ServerResponse.ok().bodyValueAndAwait(it)
        } ?: ServerResponse.notFound().buildAndAwait()
    }
}

fun beans() = org.springframework.context.support.beans {
    bean {
        val handler = PriceHandler(ref())
        coRouter {
            GET("/prices")(handler::prices)
            GET("/prices/{id}")(handler::price)
        }
    }
}


fun main(args: Array<String>) {
    runApplication<PricingApplication>(*args) {
        addInitializers(beans())
    }
}

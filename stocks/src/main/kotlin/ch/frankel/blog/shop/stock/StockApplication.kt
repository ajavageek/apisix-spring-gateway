package ch.frankel.blog.shop.stock

import ch.frankel.blog.shop.stock.transfer.out.asOutStocks
import kotlinx.coroutines.flow.fold
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.*

@SpringBootApplication
class StockApplication

class StockHandler(private val repository: StockRepository) {

    suspend fun stocks(req: ServerRequest): ServerResponse {
        val idsString = req.queryParam("id")
        return idsString.orElse(null)?.let {
            val ids = it.split(',').mapNotNull { id -> id.toLongOrNull() }
            val stocks = repository.findByProductIds(ids).asOutStocks()
            ServerResponse.ok().bodyValueAndAwait(stocks)
        } ?: ServerResponse.ok().bodyAndAwait(repository.findAll())
    }

    suspend fun stockByProductId(req: ServerRequest): ServerResponse {
        val idString = req.pathVariable("id")
        val id = idString.toLongOrNull()
            ?: return ServerResponse.badRequest().bodyValueAndAwait("$idString is not a valid product ID")
        val stocks = repository.findByProductId(id).asOutStocks()
        return ServerResponse.ok().bodyValueAndAwait(stocks)
    }
}

fun beans() = beans {
    bean {
        val handler = StockHandler(StockRepository(ref()))
        coRouter {
            GET("/stocks")(handler::stocks)
            GET("/stocks/{id}")(handler::stockByProductId)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<StockApplication>(*args) {
        addInitializers(beans())
    }
}

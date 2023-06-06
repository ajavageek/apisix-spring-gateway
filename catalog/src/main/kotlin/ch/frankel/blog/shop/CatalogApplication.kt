package ch.frankel.blog.shop

import ch.frankel.blog.shop.transfer.`in`.Price
import ch.frankel.blog.shop.transfer.`in`.Stocks as InStocks
import ch.frankel.blog.shop.transfer.`in`.Stock as InStock
import ch.frankel.blog.shop.transfer.out.ProductWithDetailsBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.*

@SpringBootApplication
@EnableConfigurationProperties(value = [AppProperties::class])
class CatalogApplication

class PriceHandler(private val props: AppProperties, private val repository: ProductRepository) {

    private val client = WebClient.builder().build()

    suspend fun products(req: ServerRequest): ServerResponse {
        val idsString = req.queryParam("id").orElse(null)
        return if (idsString != null) {
            val ids = idsString.split(',').mapNotNull { id -> id.toLongOrNull() }
            val products = repository.findAllById(ids)
            val productWithDetails = getProductWithDetails(idsString, products)
            ServerResponse.ok().bodyAndAwait(productWithDetails)
        } else {
            val products = repository.findAll()
            val allIdsString = products
                .map { product -> product.id }
                .asFlux()
                .collectList()
                .awaitSingle()
                .joinToString(",")
            val productWithDetails = getProductWithDetails(allIdsString, products)
            ServerResponse.ok().bodyAndAwait(productWithDetails)
        }
    }

    suspend fun product(req: ServerRequest): ServerResponse {
        val idString = req.pathVariable("id")
        val id = idString.toLongOrNull()
            ?: return ServerResponse.badRequest().bodyValueAndAwait("$idString is not a valid product ID")
        return repository.findById(id)?.let {
            coroutineScope {
                val price = async(Dispatchers.IO) {
                    client.get().uri("${props.pricingEndpoint}/${it.id}").retrieve().bodyToMono<Price>().awaitSingle()
                }.await()
                val stocks = async(Dispatchers.IO) {
                    client.get().uri("${props.stockEndpoint}/${it.id}").retrieve().bodyToMono<InStocks>()
                        .awaitSingle()
                }.await()
                val product = it.withDetails(price, stocks.stocks.single { it.productId == id })
                ServerResponse.ok().bodyValueAndAwait(product)
            }
        } ?: ServerResponse.notFound().buildAndAwait()
    }

    private suspend fun getProductWithDetails(idsString: String, products: Flow<Product>) = coroutineScope {
        val prices = async(Dispatchers.IO) {
            client.get()
                .uri("${props.pricingEndpoint}?id=$idsString")
                .retrieve()
                .bodyToFlux(Price::class.java)
                .asFlow()
        }.await()
        val stocks = async(Dispatchers.IO) {
            client.get()
                .uri("${props.stockEndpoint}?id=$idsString")
                .retrieve()
                .bodyToFlux(InStocks::class.java)
                .flatMap { it.stocks.asFlow().asFlux() }
                .asFlow()
        }.await()
        products.map {
            val price = prices.filter { price -> price.productId == it.id  }.single()
            val stock = stocks.filter { stock -> stock.productId == it.id }.single()
            it.withDetails(price, stock)
        }
    }

    private fun Product.withDetails(price: Price, stock: InStock) = ProductWithDetailsBuilder(this)
        .withPrice(price)
        .withStock(stock)
        .build()
}

fun beans() = org.springframework.context.support.beans {
    bean {
        val handler = PriceHandler(ref(), ref())
        coRouter {
            GET("/products")(handler::products)
            GET("/products/{id}")(handler::product)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<CatalogApplication>(*args) {
        addInitializers(beans())
    }
}


package ch.frankel.blog.shop.transfer.out

import ch.frankel.blog.shop.Product
import ch.frankel.blog.shop.transfer.`in`.Price
import ch.frankel.blog.shop.transfer.`in`.Stock as InStock

data class Stock(val city: String, val state: String, val quantity: Int)

data class ProductWithDetails(
    private val product: Product,
    private val inPrice: Price,
    private val inStocks: InStock
) {
    val id = product.id
    val name = product.name
    val description = product.description
    val price = inPrice.price
    val stocks = inStocks.toListStocks()

    private fun InStock.toListStocks(): List<Stock> {
        return warehouses.map { Stock(it.city, it.state, it.quantity) }
    }
}

class ProductWithDetailsBuilder(private val product: Product) {

    private var price: Price? = null
    private var stock: InStock? = null

    fun withPrice(price: Price): ProductWithDetailsBuilder {
        this.price = price
        return this
    }

    fun withStock(stock: InStock): ProductWithDetailsBuilder {
        this.stock = stock
        return this
    }

    fun build(): ProductWithDetails {
        if (price != null && stock != null) {
            return ProductWithDetails(product, price!!, stock!!)
        }
        throw IllegalStateException()
    }
}
package ch.frankel.blog.shop.stock.transfer.out

import ch.frankel.blog.shop.stock.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

data class Stocks(val stocks: List<Product>)
data class Product(val productId: Long, val warehouses: List<Warehouse>)
data class Warehouse(val city: String, val state: String, val quantity: Int)

internal suspend fun Flow<Stock>.asOutStocks(): Stocks {
    val products = fold(hashMapOf<Long, List<Stock>>()) { map, stock ->
        map.merge(stock.productId, listOf(stock)) { list1, list2 ->
            list1 + list2
        }
        map
    }.toList()
    .map { Product(it.first, it.second.toListProduct()) }
    return Stocks(products)
}

private fun List<Stock>.toListProduct(): List<Warehouse> {
    return map { Warehouse(it.warehouse.city, it.warehouse.state.name, it.level) }
}


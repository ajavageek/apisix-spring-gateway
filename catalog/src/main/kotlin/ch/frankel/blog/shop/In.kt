package ch.frankel.blog.shop.transfer.`in`

data class Stocks(val stocks: List<Stock>)
data class Stock(val productId: Long, val warehouses: List<Warehouse>)
data class Warehouse(val city: String, val state: String, val quantity: Int)
data class Price(val productId: Long, val price: Double)

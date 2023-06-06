package ch.frankel.blog.shop.stock

import com.fasterxml.jackson.annotation.JsonIgnore
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.annotation.Id
import org.springframework.r2dbc.core.DatabaseClient

@JvmInline
value class State(val name: String)
data class Warehouse(@Id val id: Long, val city: String, val state: State)
data class Stock(@Id val productId: Long, val warehouse: Warehouse, val level: Int)

class StockRepository(private val client: DatabaseClient) {

    private val selectFromInnerJoin = "SELECT * FROM stock AS s INNER JOIN warehouse AS w ON s.warehouse_id = w.id"

    fun findByProductId(productId: Long) = client.sql("$selectFromInnerJoin WHERE s.product_id = :productId")
        .bind("productId", productId)
        .map(rowToStock)
        .all()
        .asFlow()

    fun findByProductIds(productIds: List<Long>) = client.sql("$selectFromInnerJoin WHERE s.product_id IN (:productIds)")
        .bind("productIds", productIds)
        .map(rowToStock)
        .all()
        .asFlow()

    fun findAll() = client.sql(selectFromInnerJoin)
        .map(rowToStock)
        .all()
        .asFlow()
}

private val rowToStock = { row: Row, _: RowMetadata ->
    val productId = row.get("PRODUCT_ID").toString().toLong()
    val warehouseId = row.get("WAREHOUSE_ID").toString().toLong()
    val city = row.get("CITY", String::class.java)!!
    val state = State(row.get("STATE", String::class.java)!!)
    val level = row.get("LEVEL").toString().toInt()
    val warehouse = Warehouse(warehouseId, city, state)
    Stock(productId, warehouse, level)
}

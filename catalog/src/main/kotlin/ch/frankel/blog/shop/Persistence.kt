package ch.frankel.blog.shop

import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

data class Product(@Id val id: Long, val name: String, val description: String)

interface ProductRepository : CoroutineCrudRepository<Product, Long>


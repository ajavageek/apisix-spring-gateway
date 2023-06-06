package ch.frankel.blog.shop.pricing

import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

data class Price(@Id val productId: Long, val price: Double)

interface PriceRepository : CoroutineCrudRepository<Price, Long>

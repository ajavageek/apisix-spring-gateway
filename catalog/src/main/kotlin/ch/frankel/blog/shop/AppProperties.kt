package ch.frankel.blog.shop

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(private val stocks: Stocks, private val pricing: Pricing, val referer: String?) {
    val stockEndpoint = stocks.endpoint
    val pricingEndpoint = pricing.endpoint
}

data class Stocks(val endpoint: String)
data class Pricing(val endpoint: String)

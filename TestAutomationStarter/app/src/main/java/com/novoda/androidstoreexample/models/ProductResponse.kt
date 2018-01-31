package com.novoda.androidstoreexample.models

import java.util.*

data class ProductResponse(val products: Array<Product>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductResponse

        if (!Arrays.equals(products, other.products)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(products)
    }
}
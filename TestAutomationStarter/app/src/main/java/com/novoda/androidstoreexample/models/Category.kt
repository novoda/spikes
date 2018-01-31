package com.novoda.androidstoreexample.models

data class Category(val title: String, val image: String) {
    override fun toString(): String {
        return title
    }
}

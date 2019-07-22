package com.novoda.playground.multiplatform.common

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String,
    val gallery: Gallery,
    val header: RecipeHeader,
    val ingredients: Ingredients,
    val utensils: Utensils,
    val shouldKnows: ShouldKnows = ShouldKnows(emptyList())
)

@Serializable
data class Gallery(
    val items: List<Image>
)

@Serializable
data class RecipeHeader(
    val title: String,
    val servingUnits: String,
    val difficulty: Difficulty,
    val duration: Duration
)

@Serializable
data class Difficulty(
    val label: String,
    val level: Int
)

@Serializable
data class Image(
    val url: String
)

@Serializable
data class Duration(
    val preparation: String,
    val baking: String,
    val waiting: String,
    val total: String
)

@Serializable
data class Ingredients(
    val items: List<Ingredient>
)

@Serializable
data class Ingredient(
    val amount: String,
    val name: String
)

@Serializable
data class Utensils(
    val items: List<String>
)

@Serializable
data class ShouldKnows(
    val items: List<String>
)

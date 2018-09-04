package com.novoda.gol.patterns

class PatternRepository {

    companion object {
        fun patterns(): List<PatternEntity> = listOf(Blinker.create(), Glider.create())
    }
}


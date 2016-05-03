package com.odai.architecturedemo.cats.model

import com.odai.architecturedemo.cat.model.Cat

data class Cats (private val list: List<Cat>) {

    fun size(): Int {
        return list.size;
    }

    fun get(p1: Int): Cat {
        return list[p1]
    }

    fun contains(cat: Cat): Boolean {
        return list.contains(cat)
    }

    fun add(cat: Cat): Cats {
        return Cats(list.plus(cat))
    }

    fun remove(cat: Cat): Cats {
        return Cats(list.minus(cat))
    }

    fun isEmpty(): Boolean {
        return list.isEmpty();
    }

    fun <R> fold(initial: R, operation: (R, Cat) -> R): R = list.fold(initial, operation)

    fun first(predicate: (Cat) -> Boolean): Cat? = list.first(predicate);

};

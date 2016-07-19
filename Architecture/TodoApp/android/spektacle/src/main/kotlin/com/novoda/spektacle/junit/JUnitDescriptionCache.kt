package com.novoda.spektacle.junit

import com.novoda.spektacle.ActionType
import com.novoda.spektacle.SpektacleTree
import org.junit.runner.Description
import java.io.Serializable
import java.util.*

data class JUnitUniqueId(val id: Int) : Serializable {
    companion object {
        var id = 0
        fun next() = JUnitUniqueId(id++)
    }
}

class JUnitDescriptionCache<T>() {
    val cache: IdentityHashMap<SpektacleTree<T>, Description> = IdentityHashMap()

    fun get(key: SpektacleTree<T>): Description {
        return cache.getOrPut(key, { create(key) })
    }

    private fun create(key: SpektacleTree<T>): Description {
        when (key.type) {
            ActionType.IT ->
                return Description.createSuiteDescription(key.description, JUnitUniqueId.next())
            ActionType.ON -> {
                val description = Description.createSuiteDescription(key.description, JUnitUniqueId.next())
                key.children.forEach {
                    description.addChild(this.get(it))
                }
                return description
            }
            ActionType.GIVEN -> {
                val description = Description.createSuiteDescription(key.description, JUnitUniqueId.next())
                key.children.forEach {
                    description.addChild(this.get(it))
                }
                return description
            }
            ActionType.INIT -> {
                val description = Description.createSuiteDescription(key.description, JUnitUniqueId.next())
                key.children.forEach {
                    description.addChild(this.get(it))
                }
                return description
            }
        }
    }

}

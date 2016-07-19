package com.novoda.spektacle.junit;

import com.novoda.spektacle.Spektacle
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class JUnitSpektacleRunner<Any>(val specificationClass: Class<*>) : Runner() {
    private val junitDescriptionCache = JUnitDescriptionCache<Any>()
    val spek: Spektacle<Any>

    init {
        if (Spektacle::class.java.isAssignableFrom(specificationClass) && !specificationClass.isLocalClass) {
            spek = specificationClass.newInstance() as Spektacle<Any>
        } else {
            throw RuntimeException(specificationClass.canonicalName + " must be a subclass of "
                    + Spektacle::class.qualifiedName
                    + " in order to use " + JUnitSpektacleRunner::class.simpleName)
        }
    }

    override fun getDescription(): Description? {
        return junitDescriptionCache.get(spek.tree)
    }

    override fun run(junitNotifier: RunNotifier?) {
        spek.run(JUnitNotifier(junitNotifier!!, junitDescriptionCache))
    }
}

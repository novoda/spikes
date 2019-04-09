package com.novoda.ksigner

internal data class CanonicalHeaders constructor(
    val names: String,
    val canonicalizedHeaders: String,
    private val internalMap: Map<String, List<String>>
) {

    fun getFirstValue(headerName: String): String? = internalMap[headerName.toLowerCase()]?.get(0)

    internal class Builder {

        private val internalMap = mutableMapOf<String, MutableList<String>>()

        fun add(name: String, value: String): Builder {
            val lowerCaseName = name.toLowerCase()
            internalMap.getOrPut(lowerCaseName) { mutableListOf() }.add(value)
            return this
        }

        fun build(): CanonicalHeaders {
            val names = internalMap.keys
                .toTypedArray()
                .sortedArray()
                .joinToString(separator = ";") { it.toLowerCase() }

            return CanonicalHeaders(names, canonicalizedHeaders(), internalMap)
        }

        private fun canonicalizedHeaders(): String {
            val canonicalizedHeadersBuilder = StringBuilder()
            internalMap.entries
                .sortedBy { it.key }
                .forEach { header ->
                    canonicalizedHeadersBuilder
                        .append(header.key.toLowerCase())
                        .append(':')
                        .append(header.value
                            .map { normalizeHeaderValue(it) }
                            .joinToString(separator = ","))
                        .append("\n")
                }

            return canonicalizedHeadersBuilder.toString()
        }

        private fun normalizeHeaderValue(value: String): String {
            /*
             * Strangely, the AWS test suite expects us to handle lines in
             * multi-line values as individual values, even though this is not
             * mentioned in the specs.
             */
            return value
                .split("\n")
                .map { it.trim() }
                .map { it.replace(" +", " ") }
                .joinToString(separator = ",")
        }
    }
}

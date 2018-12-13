package com.novoda.testsupport

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource

fun <T> testDataSourceFactory(data: List<T>): DataSource.Factory<Int, T> = TestDataSuourceFactory(data)

private class TestDataSuourceFactory<T>(private val data: List<T>) : DataSource.Factory<Int, T>() {
    override fun create() = TestDataSource(data)

}

// copy of ListDataSource (internal)
private class TestDataSource<T>(val list: List<T>) : PositionalDataSource<T>() {
    override fun loadInitial(
        params: PositionalDataSource.LoadInitialParams,
        callback: PositionalDataSource.LoadInitialCallback<T>
    ) {
        val totalCount = list.size

        val position = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
        val loadSize = PositionalDataSource.computeInitialLoadSize(params, position, totalCount)

        // for simplicity, we could return everything immediately,
        // but we tile here since it's expected behavior
        val sublist = list.subList(position, position + loadSize)
        callback.onResult(sublist, position, totalCount)
    }

    override fun loadRange(
        params: PositionalDataSource.LoadRangeParams,
        callback: PositionalDataSource.LoadRangeCallback<T>
    ) {
        callback.onResult(
            list.subList(
                params.startPosition,
                params.startPosition + params.loadSize
            )
        )
    }

}

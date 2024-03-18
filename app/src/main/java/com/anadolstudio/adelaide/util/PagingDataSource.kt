package com.anadolstudio.adelaide.util

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow

const val DEFAULT_PAGE_SIZE = 20

@Suppress("TooGenericExceptionCaught")
class PagingDataSource<T : Any>(
    private val pageSize: Int,
    private val load: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val currentPage = params.key ?: DEFAULT_PAGE
            val content = load.invoke(currentPage, pageSize)

            LoadResult.Page(
                data = content,
                prevKey = null,
                nextKey = if (content.size < pageSize) null else currentPage + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val DEFAULT_PAGE = 0
    }
}

fun <T : Any> pagingFlow(
    pageSize: Int = DEFAULT_PAGE_SIZE,
    load: suspend (pageNumber: Int, pageSize: Int) -> List<T>,
): Flow<PagingData<T>> {
    return Pager(
        config = PagingConfig(pageSize, enablePlaceholders = false),
        initialKey = 0,
        pagingSourceFactory = { PagingDataSource(pageSize, load) }
    ).flow
}

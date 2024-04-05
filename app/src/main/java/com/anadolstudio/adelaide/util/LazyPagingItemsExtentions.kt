package com.anadolstudio.adelaide.util

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyPagingItems<T>.isComplete(): Boolean = loadState.refresh is LoadState.NotLoading
fun <T : Any> LazyPagingItems<T>.isContent(): Boolean = loadState.refresh is LoadState.NotLoading && itemCount != 0
fun <T : Any> LazyPagingItems<T>.isEmpty(): Boolean = loadState.refresh is LoadState.NotLoading && itemCount == 0
fun <T : Any> LazyPagingItems<T>.isLoading(): Boolean = loadState.refresh is LoadState.Loading
fun <T : Any> LazyPagingItems<T>.isError(): Boolean = loadState.refresh is LoadState.Error
fun <T : Any> LazyPagingItems<T>.isLoadingNextPage(): Boolean = loadState.append is LoadState.Loading
fun <T : Any> LazyPagingItems<T>.isErrorNextPage(): Boolean = loadState.append is LoadState.Error

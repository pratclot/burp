package com.pratclot.repo

import android.location.Location
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.pratclot.burp.FindSomeQuery

class YelpPagingSource(
    val apolloClient: ApolloClient,
    val count: Int = 3,
    val location: Location
) : PagingSource<Int, FindSomeQuery.Business>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, FindSomeQuery.Business> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val await = try {
                apolloClient
                    .query(
                        FindSomeQuery(
                            Input.fromNullable(location.latitude),
                            Input.fromNullable(location.longitude),
                            Input.fromNullable(count),
                            Input.fromNullable(nextPageNumber)
                        )
                    )
                    .await()
            } catch (e: ApolloException) {
                Log.e("APOLLO", "HAS FUCKED UP $e")
                return LoadResult.Error(e)
            }
            val data = (await as Response<FindSomeQuery.Data>).data
            val response = data?.search?.business?.filterNotNull() ?: emptyList()
            return LoadResult.Page(
                data = response,
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber + count
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, FindSomeQuery.Business>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

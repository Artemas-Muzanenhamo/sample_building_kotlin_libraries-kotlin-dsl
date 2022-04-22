package zencargo.roadtracking.adapter.graphql.pagination

import graphql.relay.Connection

interface ConnectionWrapper<T> : Connection<T> {
    /**
     * @return a list of T
     */
    fun getNodes(): List<T>

    /**
     * @return the total amount of elements
     */
    fun getTotalCount(): Long
}

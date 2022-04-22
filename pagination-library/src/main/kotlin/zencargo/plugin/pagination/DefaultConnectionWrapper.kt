package zencargo.roadtracking.adapter.graphql.pagination

import graphql.com.google.common.collect.ImmutableList
import graphql.relay.Edge
import graphql.relay.PageInfo

class DefaultConnectionWrapper<T>(edges: List<Edge<T>>, nodes: List<T>, pageInfo: PageInfo, totalCount: Long = 0) : ConnectionWrapper<T> {

    private val edges: ImmutableList<Edge<T>> = ImmutableList.copyOf(edges)
    private val nodes: ImmutableList<T> = ImmutableList.copyOf(nodes)
    private val pageInfo: PageInfo = pageInfo
    private val totalCount: Long = totalCount

    override fun getEdges(): List<Edge<T>> {
        return edges
    }

    override fun getNodes(): List<T> {
        return nodes
    }

    override fun getPageInfo(): PageInfo {
        return pageInfo
    }

    override fun getTotalCount(): Long {
        return totalCount
    }

    override fun toString(): String {
        return "DefaultConnection{edges=$edges, nodes=$nodes, pageInfo=$pageInfo, totalCount=$totalCount}"
    }
}

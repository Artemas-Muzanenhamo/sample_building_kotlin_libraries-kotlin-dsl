package zencargo.roadtracking.adapter.graphql.pagination

import graphql.relay.DefaultConnectionCursor
import graphql.relay.DefaultEdge
import graphql.relay.DefaultPageInfo
import graphql.relay.PageInfo
import graphql.schema.DataFetchingEnvironment
import org.slf4j.Logger
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Base64
import kotlin.streams.toList

@Component
class SpringDataPageableConnection(private val log: Logger) {

    companion object {
        const val AFTER_PARAM: String = "after"
        const val BEFORE_PARAM: String = "before"
        const val FIRST_PARAM: String = "first"
        const val LAST_PARAM: String = "last"
    }

    fun <T> get(pageableResult: Page<T>): ConnectionWrapper<T> {
        if (pageableResult.isEmpty) {
            return emptyConnection()
        }

        var offset = pageableResult.pageable.offset
        val edges = pageableResult.get().map { el -> DefaultEdge<T>(el, DefaultConnectionCursor(createCursor(offset++))) }.toList()

        val firstEdge = edges.first()
        val lastEdge = edges.last()

        val pageInfo: PageInfo = DefaultPageInfo(
            firstEdge.cursor,
            lastEdge.cursor,
            pageableResult.hasPrevious(),
            pageableResult.hasNext()
        )

        return DefaultConnectionWrapper(
            edges,
            pageableResult.content,
            pageInfo,
            pageableResult.totalElements
        )
    }

    fun getPageRequest(dfe: DataFetchingEnvironment, sort: Sort, defaultPageSize: Int = 50): PageRequest {
        var page = 0
        var size = defaultPageSize
        val first: Int? = dfe.getArgument<Int>(FIRST_PARAM)
        val last: Int? = dfe.getArgument<Int>(LAST_PARAM)

        if (first != null) {
            size = getPageSize(first, defaultPageSize)
            val afterOffset: Int = getOffsetFromCursor(dfe.getArgument<String>(AFTER_PARAM), -1)
            // 0 based index
            page = Math.floorDiv(afterOffset + 1, size)
        } else if (last != null) {
            size = getPageSize(last, defaultPageSize)
            val beforeOffset: Int = getOffsetFromCursor(dfe.getArgument<String>(BEFORE_PARAM), size)
            // 0 based index
            page = Math.floorDiv(beforeOffset - 1, size)
        }
        log.debug("Page: {} with size: {} and sorting: {}", page, size, sort)
        return PageRequest.of(page, size, sort)
    }

    private fun createCursor(offset: Long): String {
        val bytes: ByteArray = offset.toString().toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun <T> emptyConnection(): ConnectionWrapper<T> {
        val pageInfo: PageInfo = DefaultPageInfo(null, null, false, false)
        return DefaultConnectionWrapper(emptyList(), emptyList(), pageInfo)
    }

    private fun getOffsetFromCursor(cursor: String?, defaultValue: Int): Int {
        if (cursor != null) {
            try {
                val decode = Base64.getDecoder().decode(cursor)
                val string = String(decode, StandardCharsets.UTF_8)
                return string.toInt()
            } catch (nfe: NumberFormatException) {
                log.warn("The cursor was not created by this class: {}. Return default value: {}", cursor, defaultValue)
            } catch (e: IllegalArgumentException) {
                log.warn("The cursor is not in base64 format: {}. Return default value: {}", cursor, defaultValue)
            }
        }
        return defaultValue
    }

    private fun getPageSize(size: Int, defaultPageSize: Int): Int {
        if (size < 0) {
            log.warn("The page size must not be negative: {}. Set to default value: {}", size, defaultPageSize)
            return defaultPageSize
        }
        return size
    }
}

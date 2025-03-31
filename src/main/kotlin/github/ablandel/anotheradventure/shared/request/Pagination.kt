package github.ablandel.anotheradventure.shared.request

const val REQUEST_PAGINATION_CURSOR_QUERY = "cursor"
const val REQUEST_PAGINATION_OFFSET_QUERY = "offset"
const val REQUEST_PAGINATION_LIMIT_QUERY = "limit"
const val PAGINATION_DEFAULT_OFFSET = 0
const val PAGINATION_DEFAULT_LIMIT = 20

const val RESPONSE_PAGINATION_CURSOR_HEADER = "Pagination-Cursor"
const val RESPONSE_PAGINATION_OFFSET_HEADER = "Pagination-Offset"
const val RESPONSE_PAGINATION_DEFAULT_LIMIT_HEADER = "Pagination-Default-Limit"
const val RESPONSE_PAGINATION_COUNT_HEADER = "Pagination-Count"
const val RESPONSE_PAGINATION_TOTAL_COUNT_HEADER = "Pagination-Total-Count"

data class Pagination(
    val offset: Int,
    val limit: Int,
    val cursor: Long?,
) {
    constructor(offset: Int? = null, limit: Int? = null, cursor: Long? = null) : this(
        offset = if (offset != null && offset >= 0) offset else PAGINATION_DEFAULT_OFFSET,
        limit = if (limit != null && limit > 0) limit else PAGINATION_DEFAULT_LIMIT,
        cursor = if (cursor != null && cursor > 0) cursor else null,
    )
}

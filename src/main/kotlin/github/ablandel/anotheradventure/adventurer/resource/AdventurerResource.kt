package github.ablandel.anotheradventure.adventurer.resource

import github.ablandel.anotheradventure.adventurer.dto.AdventurerDTO
import github.ablandel.anotheradventure.adventurer.service.AdventurerService
import github.ablandel.anotheradventure.shared.request.PAGINATION_DEFAULT_LIMIT
import github.ablandel.anotheradventure.shared.request.Pagination
import github.ablandel.anotheradventure.shared.request.REQUEST_PAGINATION_CURSOR_QUERY
import github.ablandel.anotheradventure.shared.request.REQUEST_PAGINATION_LIMIT_QUERY
import github.ablandel.anotheradventure.shared.request.REQUEST_PAGINATION_OFFSET_QUERY
import github.ablandel.anotheradventure.shared.request.RESPONSE_PAGINATION_COUNT_HEADER
import github.ablandel.anotheradventure.shared.request.RESPONSE_PAGINATION_CURSOR_HEADER
import github.ablandel.anotheradventure.shared.request.RESPONSE_PAGINATION_DEFAULT_LIMIT_HEADER
import github.ablandel.anotheradventure.shared.request.RESPONSE_PAGINATION_OFFSET_HEADER
import github.ablandel.anotheradventure.shared.request.RESPONSE_PAGINATION_TOTAL_COUNT_HEADER
import github.ablandel.anotheradventure.shared.request.dto.CountDTO
import github.ablandel.anotheradventure.shared.validation.ValidationContext.OnCreation
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class AdventurerResource(
    private val adventurerService: AdventurerService,
) {
    @GetMapping("/v1/adventurers")
    fun findWithPagination(
        @RequestParam(required = false, name = REQUEST_PAGINATION_CURSOR_QUERY) cursor: Long?,
        @RequestParam(required = false, name = REQUEST_PAGINATION_OFFSET_QUERY) offset: Int?,
        @RequestParam(required = false, name = REQUEST_PAGINATION_LIMIT_QUERY) limit: Int?,
        response: HttpServletResponse,
    ): List<AdventurerDTO> {
        val pagination = Pagination(offset = offset, limit = limit, cursor = cursor)
        val adventurerDtos = adventurerService.findWithPagination(pagination)
        val totalCount = adventurerService.count()
        if (offset != null) {
            response.setHeader(
                RESPONSE_PAGINATION_OFFSET_HEADER,
                pagination.offset.toString(),
            )
        }
        if (adventurerDtos.isNotEmpty()) {
            response.setHeader(
                RESPONSE_PAGINATION_CURSOR_HEADER,
                adventurerDtos[adventurerDtos.size - 1].id.toString(),
            )
        }
        if (limit == null) {
            response.setHeader(RESPONSE_PAGINATION_DEFAULT_LIMIT_HEADER, PAGINATION_DEFAULT_LIMIT.toString())
        }
        response.setHeader(RESPONSE_PAGINATION_COUNT_HEADER, adventurerDtos.size.toString())
        response.setHeader(RESPONSE_PAGINATION_TOTAL_COUNT_HEADER, totalCount.toString())
        return adventurerDtos
    }

    @GetMapping("/v1/adventurers/count")
    fun count(): CountDTO = CountDTO(adventurerService.count())

    @GetMapping("/v1/adventurers/{id}")
    fun findById(
        @PathVariable("id") id: Long,
    ): AdventurerDTO = adventurerService.findById(id)

    @PostMapping("/v1/adventurers")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Validated(
            OnCreation::class,
        ) @RequestBody adventurerDto: AdventurerDTO,
    ): AdventurerDTO = adventurerService.create(adventurerDto)

    @DeleteMapping("/v1/adventurers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(
        @PathVariable("id") id: Long,
    ) = adventurerService.deleteById(id)
}

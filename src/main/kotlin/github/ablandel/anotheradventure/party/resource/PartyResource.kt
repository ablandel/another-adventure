package github.ablandel.anotheradventure.party.resource

import github.ablandel.anotheradventure.party.dto.PartyDTO
import github.ablandel.anotheradventure.party.service.PartyService
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
class PartyResource(
    private val partyService: PartyService,
) {
    @GetMapping("/v1/parties")
    fun findWithPagination(
        @RequestParam(required = false, name = REQUEST_PAGINATION_CURSOR_QUERY) cursor: Long?,
        @RequestParam(required = false, name = REQUEST_PAGINATION_OFFSET_QUERY) offset: Int?,
        @RequestParam(required = false, name = REQUEST_PAGINATION_LIMIT_QUERY) limit: Int?,
        response: HttpServletResponse,
    ): List<PartyDTO> {
        val pagination = Pagination(offset = offset, limit = limit, cursor = cursor)
        val partyDtos = partyService.findWithPagination(pagination)
        val totalCount = partyService.count()
        if (offset != null) {
            response.setHeader(
                RESPONSE_PAGINATION_OFFSET_HEADER,
                pagination.offset.toString(),
            )
        }
        if (partyDtos.isNotEmpty()) {
            response.setHeader(
                RESPONSE_PAGINATION_CURSOR_HEADER,
                partyDtos[partyDtos.size - 1].id.toString(),
            )
        }
        if (limit == null) {
            response.setHeader(RESPONSE_PAGINATION_DEFAULT_LIMIT_HEADER, PAGINATION_DEFAULT_LIMIT.toString())
        }
        response.setHeader(RESPONSE_PAGINATION_COUNT_HEADER, partyDtos.size.toString())
        response.setHeader(RESPONSE_PAGINATION_TOTAL_COUNT_HEADER, totalCount.toString())
        return partyDtos
    }

    @GetMapping("/v1/parties/count")
    fun count(): CountDTO = CountDTO(partyService.count())

    @GetMapping("/v1/parties/{id}")
    fun findById(
        @PathVariable("id") id: Long,
    ): PartyDTO = partyService.findById(id)

    @PostMapping("/v1/parties")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Validated(
            OnCreation::class,
        ) @RequestBody partyDTO: PartyDTO,
    ): PartyDTO = partyService.create(partyDTO)

    @DeleteMapping("/v1/parties/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(
        @PathVariable("id") id: Long,
    ) = partyService.deleteById(id)
}

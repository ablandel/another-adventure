package github.ablandel.anotheradventure.adventurer.service

import github.ablandel.anotheradventure.adventurer.dto.AdventurerDTO
import github.ablandel.anotheradventure.adventurer.dto.toEntity
import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.adventurer.entity.toDTO
import github.ablandel.anotheradventure.adventurer.exception.AdventurerAlreadyExistException
import github.ablandel.anotheradventure.adventurer.exception.AdventurerDoesNotExistException
import github.ablandel.anotheradventure.adventurer.repository.AdventurerRepository
import github.ablandel.anotheradventure.party.exception.FounderCannotBeDeletedException
import github.ablandel.anotheradventure.party.exception.PartyDoesNotExistException
import github.ablandel.anotheradventure.party.repository.PartyRepository
import github.ablandel.anotheradventure.shared.request.Pagination
import org.springframework.stereotype.Service

@Service
class AdventurerService(
    private val adventurerRepository: AdventurerRepository,
    private val partyRepository: PartyRepository,
) {
    fun findWithPagination(pagination: Pagination): List<AdventurerDTO> {
        val adventurers =
            when {
                pagination.cursor != null ->
                    adventurerRepository.findWithLimitAndCursor(
                        pagination.cursor,
                        pagination.limit,
                    )

                else -> adventurerRepository.findWithLimitAndOffset(pagination.offset, pagination.limit)
            }
        return adventurers.map { it.toDTO() }.toList()
    }

    fun findById(id: Long): AdventurerDTO {
        val adventurer =
            adventurerRepository.findById(id).orElseThrow {
                AdventurerDoesNotExistException(id)
            }
        return adventurer.toDTO()
    }

    fun create(adventurerDto: AdventurerDTO): AdventurerDTO {
        adventurerRepository.findByName(adventurerDto.name).ifPresent { _: Adventurer? ->
            throw AdventurerAlreadyExistException()
        }
        adventurerDto.partyId?.let {
            partyRepository.findById(it).orElseThrow {
                PartyDoesNotExistException(it)
            }
        }
        val adventurer = adventurerDto.toEntity()
        return adventurerRepository.save(adventurer).toDTO()
    }

    fun deleteById(id: Long) {
        val adventurer =
            adventurerRepository.findById(id).orElseThrow {
                AdventurerDoesNotExistException(id)
            }
        if (adventurer.party != null) {
            partyRepository.findById(adventurer.party.id!!).ifPresent {
                when {
                    it.founder.id == id -> throw FounderCannotBeDeletedException()
                }
            }
        }
        return adventurerRepository.delete(adventurer)
    }

    fun count() = adventurerRepository.count()
}

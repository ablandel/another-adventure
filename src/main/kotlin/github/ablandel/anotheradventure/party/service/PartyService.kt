package github.ablandel.anotheradventure.party.service

import github.ablandel.anotheradventure.adventurer.entity.cloneWithParty
import github.ablandel.anotheradventure.adventurer.exception.AdventurerDoesNotExistException
import github.ablandel.anotheradventure.adventurer.exception.AdventurersDoNotExistException
import github.ablandel.anotheradventure.adventurer.repository.AdventurerRepository
import github.ablandel.anotheradventure.party.dto.PartyDTO
import github.ablandel.anotheradventure.party.dto.toEntity
import github.ablandel.anotheradventure.party.entity.Party
import github.ablandel.anotheradventure.party.entity.toDTO
import github.ablandel.anotheradventure.party.exception.AdventurerIsAlreadyTheFounderOfAnotherPartyException
import github.ablandel.anotheradventure.party.exception.AdventurersAreAlreadyInAnotherPartyException
import github.ablandel.anotheradventure.party.exception.FounderMustBeInAdventurerListException
import github.ablandel.anotheradventure.party.exception.PartyAlreadyExistException
import github.ablandel.anotheradventure.party.exception.PartyDoesNotExistException
import github.ablandel.anotheradventure.party.repository.PartyRepository
import github.ablandel.anotheradventure.shared.request.Pagination
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartyService(
    private val partyRepository: PartyRepository,
    private val adventurerRepository: AdventurerRepository,
) {
    fun findWithPagination(pagination: Pagination): List<PartyDTO> {
        val parties =
            when {
                pagination.cursor != null ->
                    partyRepository.findWithLimitAndCursor(
                        pagination.cursor,
                        pagination.limit,
                    )

                else -> partyRepository.findWithLimitAndOffset(pagination.offset, pagination.limit)
            }
        return parties.map { it.toDTO() }.toList()
    }

    fun findById(id: Long): PartyDTO {
        val party =
            partyRepository.findById(id).orElseThrow {
                AdventurerDoesNotExistException(id)
            }
        return party.toDTO()
    }

    @Transactional
    fun create(partyDto: PartyDTO): PartyDTO {
        partyRepository.findByName(partyDto.name).ifPresent { _: Party? ->
            throw PartyAlreadyExistException()
        }
        partyDto.founderId.let {
            adventurerRepository.findById(it).orElseThrow {
                AdventurerDoesNotExistException(it)
            }
            if (partyRepository.countByFounderId(it) > 0) {
                throw AdventurerIsAlreadyTheFounderOfAnotherPartyException()
            }
        }
        val uniqueAdventurerIds = partyDto.adventurerIds.toSet().toMutableList()
        if (!uniqueAdventurerIds.contains(partyDto.founderId)) {
            throw FounderMustBeInAdventurerListException()
        }
        val existingAdventurers =
            adventurerRepository.findInIds(uniqueAdventurerIds.toSet().toList())
        val existingAdventurersIds = existingAdventurers.map { it.id!! }
        val missingAdventurersIds = uniqueAdventurerIds - existingAdventurersIds
        if (missingAdventurersIds.isNotEmpty()) {
            throw AdventurersDoNotExistException(missingAdventurersIds)
        }
        val adventurersAlreadyInParties = existingAdventurers.filter { it.party != null }
        if (adventurersAlreadyInParties.isNotEmpty()) {
            throw AdventurersAreAlreadyInAnotherPartyException(adventurersAlreadyInParties.map { it.id!! })
        }
        val party = partyDto.toEntity()
        val savedParty = partyRepository.save(party)
        val updatedAdventurers = existingAdventurers.map { it.cloneWithParty(savedParty) }
        adventurerRepository.saveAll(updatedAdventurers)
        return savedParty.toDTO()
    }

    @Transactional
    fun deleteById(id: Long) {
        val party =
            partyRepository.findById(id).orElseThrow {
                PartyDoesNotExistException(id)
            }
        party.adventurers.forEach { adventurerRepository.save(it.cloneWithParty(party = null)) }
        return partyRepository.delete(party)
    }

    fun count() = partyRepository.count()
}

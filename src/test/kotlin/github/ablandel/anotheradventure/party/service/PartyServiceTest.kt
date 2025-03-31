package github.ablandel.anotheradventure.party.service

import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.adventurer.exception.AdventurerDoesNotExistException
import github.ablandel.anotheradventure.adventurer.exception.AdventurersDoNotExistException
import github.ablandel.anotheradventure.adventurer.repository.AdventurerRepository
import github.ablandel.anotheradventure.party.dto.PartyDTO
import github.ablandel.anotheradventure.party.entity.Party
import github.ablandel.anotheradventure.party.exception.AdventurerIsAlreadyTheFounderOfAnotherParty
import github.ablandel.anotheradventure.party.exception.AdventurersAreAlreadyInAnotherParty
import github.ablandel.anotheradventure.party.exception.FounderMustBeInAdventurerList
import github.ablandel.anotheradventure.party.exception.PartyAlreadyExistException
import github.ablandel.anotheradventure.party.exception.PartyDoesNotExistException
import github.ablandel.anotheradventure.party.repository.PartyRepository
import github.ablandel.anotheradventure.shared.request.Pagination
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class PartyServiceTest(
    @Mock private val partyRepository: PartyRepository,
    @Mock private val adventurerRepository: AdventurerRepository,
) {
    @InjectMocks
    private lateinit var partyService: PartyService

    @Nested
    internal inner class FindWithPagination {
        @Test
        fun `findWithPagination with cursor`() {
            `when`(
                partyRepository.findWithLimitAndCursor(
                    limit = 10,
                    cursor = 0,
                ),
            ).thenReturn(
                listOf(
                    Party(
                        name = "name1",
                        founder = Adventurer(id = 1, name = "name1"),
                        adventurers = emptyList(),
                    ),
                    Party(
                        name = "name2",
                        founder = Adventurer(id = 2, name = "name2"),
                        adventurers =
                            listOf(
                                Adventurer(id = 3, name = "name3"),
                                Adventurer(id = 4, name = "name4"),
                            ),
                    ),
                ),
            )
            val actual =
                partyService.findWithPagination(
                    Pagination(
                        limit = 10,
                        cursor = 0,
                        offset = 10,
                    ),
                )
            assertEquals(
                listOf(
                    PartyDTO(name = "name1", founderId = 1, adventurerIds = emptyList()),
                    PartyDTO(name = "name2", founderId = 2, adventurerIds = listOf(3, 4)),
                ),
                actual,
            )
            verify(partyRepository, never()).findWithLimitAndOffset(anyInt(), anyInt())
        }

        @Test
        fun `findWithPagination with offset`() {
            `when`(
                partyRepository.findWithLimitAndOffset(
                    limit = 10,
                    offset = 0,
                ),
            ).thenReturn(
                listOf(
                    Party(
                        name = "name1",
                        founder = Adventurer(id = 1, name = "name1"),
                        adventurers = emptyList(),
                    ),
                    Party(
                        name = "name2",
                        founder = Adventurer(id = 2, name = "name2"),
                        adventurers =
                            listOf(
                                Adventurer(id = 3, name = "name3"),
                                Adventurer(id = 4, name = "name4"),
                            ),
                    ),
                ),
            )
            val actual =
                partyService.findWithPagination(
                    Pagination(
                        limit = 10,
                        offset = 0,
                    ),
                )
            assertEquals(
                listOf(
                    PartyDTO(name = "name1", founderId = 1, adventurerIds = emptyList()),
                    PartyDTO(name = "name2", founderId = 2, adventurerIds = listOf(3, 4)),
                ),
                actual,
            )
            verify(partyRepository, never()).findWithLimitAndCursor(anyLong(), anyInt())
        }
    }

    @Nested
    internal inner class FindById {
        @Test
        fun `findById when the party does not exist`() {
            `when`(
                partyRepository.findById(1),
            ).thenReturn(Optional.empty())
            assertThrows(AdventurerDoesNotExistException::class.java) {
                partyService.findById(1)
            }
        }

        @Test
        fun `findById when the party exists`() {
            `when`(
                partyRepository.findById(1),
            ).thenReturn(
                Optional.of(
                    Party(
                        name = "name1",
                        founder = Adventurer(id = 1, name = "name1"),
                        adventurers = emptyList(),
                    ),
                ),
            )
            val actual = partyService.findById(1)
            assertEquals(actual, PartyDTO(name = "name1", founderId = 1, adventurerIds = emptyList()))
        }
    }

    @Nested
    internal inner class Create {
        @Test
        fun `create when another party already exists with the same name`() {
            `when`(
                partyRepository.findByName("name"),
            ).thenReturn(
                Optional.of(
                    Party(
                        id = 1,
                        name = "",
                        founder = Adventurer(name = ""),
                        adventurers = emptyList(),
                    ),
                ),
            )
            assertThrows(PartyAlreadyExistException::class.java) {
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = emptyList()))
            }
            verify(partyRepository, never()).save(any())
            verify(adventurerRepository, never()).saveAll(anyList())
        }

        @Test
        fun `create when the founder does not exist`() {
            `when`(partyRepository.findByName("name")).thenReturn(Optional.empty())
            `when`(adventurerRepository.findById(2)).thenReturn(Optional.empty())
            assertThrows(AdventurerDoesNotExistException::class.java) {
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = emptyList()))
            }
            verify(partyRepository, never()).save(any())
            verify(adventurerRepository, never()).saveAll(anyList())
        }

        @Test
        fun `create when the founder is already the founder of another party`() {
            `when`(partyRepository.findByName("name")).thenReturn(Optional.empty())
            `when`(adventurerRepository.findById(2)).thenReturn(Optional.of(Adventurer(id = 2, name = "")))
            `when`(partyRepository.countByFounderId(2)).thenReturn(1)
            assertThrows(AdventurerIsAlreadyTheFounderOfAnotherParty::class.java) {
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = emptyList()))
            }
            verify(partyRepository, never()).save(any())
            verify(adventurerRepository, never()).saveAll(anyList())
        }

        @Test
        fun `create when the founder is not in the adventurer list`() {
            `when`(partyRepository.findByName("name")).thenReturn(Optional.empty())
            `when`(adventurerRepository.findById(2)).thenReturn(Optional.of(Adventurer(id = 2, name = "")))
            `when`(partyRepository.countByFounderId(2)).thenReturn(0)
            assertThrows(FounderMustBeInAdventurerList::class.java) {
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = emptyList()))
            }
            verify(partyRepository, never()).save(any())
            verify(adventurerRepository, never()).saveAll(anyList())
        }

        @Test
        fun `create when some adventurers in the adventurer list do not exist`() {
            val founder = Adventurer(id = 2, name = "")
            `when`(partyRepository.findByName("name")).thenReturn(Optional.empty())
            `when`(adventurerRepository.findById(2)).thenReturn(Optional.of(founder))
            `when`(partyRepository.countByFounderId(2)).thenReturn(0)
            `when`(adventurerRepository.findInIds(listOf(2, 3, 4))).thenReturn(listOf(founder))
            assertThrows(AdventurersDoNotExistException::class.java) {
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = listOf(2, 2, 3, 4, 4)))
            }
            verify(partyRepository, never()).save(any())
            verify(adventurerRepository, never()).saveAll(anyList())
        }

        @Test
        fun `create when some adventurers in the adventurer list are already in another party`() {
            val founder = Adventurer(id = 2, name = "")
            `when`(partyRepository.findByName("name")).thenReturn(Optional.empty())
            `when`(adventurerRepository.findById(2)).thenReturn(Optional.of(founder))
            `when`(partyRepository.countByFounderId(2)).thenReturn(0)
            `when`(adventurerRepository.findInIds(listOf(2, 3, 4, 5))).thenReturn(
                listOf(
                    founder,
                    Adventurer(id = 3, name = ""),
                    Adventurer(
                        id = 4,
                        name = "",
                        party =
                            Party(
                                id = 6,
                                name = "",
                                founder = Adventurer(name = ""),
                                adventurers = emptyList(),
                            ),
                    ),
                    Adventurer(
                        id = 5,
                        name = "",
                        party =
                            Party(
                                id = 7,
                                name = "",
                                founder = Adventurer(name = ""),
                                adventurers = emptyList(),
                            ),
                    ),
                ),
            )
            assertThrows(AdventurersAreAlreadyInAnotherParty::class.java) {
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = listOf(2, 2, 3, 4, 4, 5)))
            }
            verify(partyRepository, never()).save(any())
            verify(adventurerRepository, never()).saveAll(anyList())
        }

        @Test
        fun `create when the creation succeed`() {
            val founder = Adventurer(id = 2, name = "")
            val adventurer = Adventurer(id = 3, name = "")
            val savedParty =
                Party(
                    id = 42,
                    name = "name",
                    founder = founder,
                    adventurers = listOf(founder, adventurer),
                )
            `when`(partyRepository.findByName("name")).thenReturn(Optional.empty())
            `when`(adventurerRepository.findById(2)).thenReturn(Optional.of(founder))
            `when`(partyRepository.countByFounderId(2)).thenReturn(0)
            `when`(adventurerRepository.findInIds(listOf(2, 3))).thenReturn(
                listOf(
                    founder,
                    adventurer,
                ),
            )
            `when`(
                partyRepository.save(
                    Party(
                        name = "name",
                        founder = founder,
                        adventurers = listOf(founder, adventurer),
                    ),
                ),
            ).thenReturn(savedParty)
            assertEquals(
                PartyDTO(
                    id = 42,
                    name = "name",
                    founderId = 2,
                    adventurerIds = listOf(2, 3),
                ),
                partyService.create(PartyDTO(name = "name", founderId = 2, adventurerIds = listOf(2, 3))),
            )
            verify(adventurerRepository, times(1)).saveAll(
                listOf(
                    Adventurer(id = 2, name = "", party = savedParty),
                    Adventurer(id = 3, name = "", party = savedParty),
                ),
            )
        }
    }

    @Nested
    internal inner class DeleteById {
        @Test
        fun `deleteById when the party does not exist`() {
            `when`(
                partyRepository.findById(1),
            ).thenReturn(Optional.empty())
            assertThrows(PartyDoesNotExistException::class.java) {
                partyService.deleteById(1)
            }
            verify(partyRepository, never()).delete(any())
        }

        @Test
        fun `deleteById when the party exists`() {
            `when`(
                partyRepository.findById(1),
            ).thenReturn(
                Optional.of(
                    Party(
                        name = "name1",
                        founder = Adventurer(id = 1, name = "name1"),
                        adventurers = emptyList(),
                    ),
                ),
            )
            partyService.deleteById(1)
        }
    }

    @Test
    fun count() {
        `when`(partyRepository.count()).thenReturn(42)
        assertEquals(42, partyService.count())
    }
}

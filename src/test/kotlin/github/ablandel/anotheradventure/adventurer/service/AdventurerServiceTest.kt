package github.ablandel.anotheradventure.adventurer.service

import github.ablandel.anotheradventure.adventurer.dto.AdventurerDTO
import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.adventurer.exception.AdventurerAlreadyExistException
import github.ablandel.anotheradventure.adventurer.exception.AdventurerDoesNotExistException
import github.ablandel.anotheradventure.adventurer.repository.AdventurerRepository
import github.ablandel.anotheradventure.party.entity.Party
import github.ablandel.anotheradventure.party.exception.FounderCannotBeDeletedException
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
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class AdventurerServiceTest(
    @Mock private val adventurerRepository: AdventurerRepository,
    @Mock private val partyRepository: PartyRepository,
) {
    @InjectMocks
    private lateinit var adventurerService: AdventurerService

    @Nested
    internal inner class FindWithPagination {
        @Test
        fun `findWithPagination with cursor`() {
            `when`(
                adventurerRepository.findWithLimitAndCursor(
                    limit = 10,
                    cursor = 0,
                ),
            ).thenReturn(
                listOf(
                    Adventurer(id = 1, name = "name1"),
                    Adventurer(id = 2, name = "name2"),
                ),
            )
            val actual =
                adventurerService.findWithPagination(
                    Pagination(
                        limit = 10,
                        cursor = 0,
                        offset = 10,
                    ),
                )
            assertEquals(
                listOf(
                    AdventurerDTO(id = 1, name = "name1"),
                    AdventurerDTO(id = 2, name = "name2"),
                ),
                actual,
            )
            verify(adventurerRepository, never()).findWithLimitAndOffset(anyInt(), anyInt())
        }

        @Test
        fun `findWithPagination with offset`() {
            `when`(
                adventurerRepository.findWithLimitAndOffset(
                    limit = 10,
                    offset = 0,
                ),
            ).thenReturn(
                listOf(
                    Adventurer(id = 1, name = "name1"),
                    Adventurer(id = 2, name = "name2"),
                ),
            )
            val actual =
                adventurerService.findWithPagination(
                    Pagination(
                        limit = 10,
                        offset = 0,
                    ),
                )
            assertEquals(
                listOf(
                    AdventurerDTO(id = 1, name = "name1"),
                    AdventurerDTO(id = 2, name = "name2"),
                ),
                actual,
            )
            verify(adventurerRepository, never()).findWithLimitAndCursor(anyLong(), anyInt())
        }
    }

    @Nested
    internal inner class FindById {
        @Test
        fun `findById when the adventurer does not exist`() {
            `when`(
                adventurerRepository.findById(1),
            ).thenReturn(Optional.empty())
            assertThrows(AdventurerDoesNotExistException::class.java) {
                adventurerService.findById(1)
            }
        }

        @Test
        fun `findById when the adventurer exists`() {
            `when`(
                adventurerRepository.findById(1),
            ).thenReturn(Optional.of(Adventurer(id = 1, name = "name1")))
            val actual = adventurerService.findById(1)
            assertEquals(AdventurerDTO(id = 1, name = "name1"), actual)
        }
    }

    @Nested
    internal inner class Create {
        @Test
        fun `create when another adventurer already exists with the same name`() {
            `when`(
                adventurerRepository.findByName("name"),
            ).thenReturn(Optional.of(Adventurer(id = 1, name = "name")))
            assertThrows(AdventurerAlreadyExistException::class.java) {
                adventurerService.create(AdventurerDTO(name = "name"))
            }
            verify(adventurerRepository, never()).save(any())
        }

        @Test
        fun `create when the party does not exist`() {
            `when`(
                adventurerRepository.findByName("name"),
            ).thenReturn(Optional.empty())
            `when`(
                partyRepository.findById(2),
            ).thenReturn(Optional.empty())
            assertThrows(PartyDoesNotExistException::class.java) {
                adventurerService.create(AdventurerDTO(name = "name", partyId = 2))
            }
            verify(adventurerRepository, never()).save(any())
        }

        @Test
        fun `create when the creation succeed (without party)`() {
            `when`(
                adventurerRepository.findByName("name"),
            ).thenReturn(Optional.empty())
            `when`(
                adventurerRepository.save(Adventurer(name = "name")),
            ).thenReturn(Adventurer(id = 1, name = "name"))
            assertEquals(
                AdventurerDTO(id = 1, name = "name"),
                adventurerService.create(
                    AdventurerDTO(name = "name"),
                ),
            )
        }

        @Test
        fun `create when the creation succeed (with party)`() {
            val party =
                Party(
                    id = 2,
                    name = "",
                    founder = Adventurer(name = ""),
                    adventurers = emptyList(),
                )
            `when`(
                adventurerRepository.findByName("name"),
            ).thenReturn(Optional.empty())
            `when`(
                partyRepository.findById(2),
            ).thenReturn(Optional.of(party))
            `when`(
                adventurerRepository.save(Adventurer(name = "name", party = party)),
            ).thenReturn(Adventurer(id = 1, name = "name", party = party))
            assertEquals(
                AdventurerDTO(id = 1, name = "name", partyId = 2),
                adventurerService.create(
                    AdventurerDTO(name = "name", partyId = 2),
                ),
            )
        }
    }

    @Nested
    internal inner class DeleteById {
        @Test
        fun `deleteById when the adventurer does not exist`() {
            `when`(
                adventurerRepository.findById(1),
            ).thenReturn(Optional.empty())
            assertThrows(AdventurerDoesNotExistException::class.java) {
                adventurerService.deleteById(1)
            }
            verify(adventurerRepository, never()).delete(any())
            verify(partyRepository, never()).findById(anyLong())
        }

        @Test
        fun `deleteById when the adventurer exists`() {
            `when`(
                adventurerRepository.findById(1),
            ).thenReturn(Optional.of(Adventurer(id = 1, name = "name1")))
            adventurerService.deleteById(1)
            verify(partyRepository, never()).findById(anyLong())
        }

        @Test
        fun `deleteById when the adventurer is not a party founder`() {
            `when`(
                adventurerRepository.findById(1),
            ).thenReturn(
                Optional.of(
                    Adventurer(
                        id = 1,
                        name = "name1",
                        party =
                            Party(
                                id = 42,
                                name = "",
                                founder = Adventurer(name = ""),
                                adventurers = emptyList(),
                            ),
                    ),
                ),
            )
            `when`(partyRepository.findById(42)).thenReturn(
                Optional.of(
                    Party(
                        id = 42,
                        name = "",
                        founder = Adventurer(id = 2, name = ""),
                        adventurers = emptyList(),
                    ),
                ),
            )
            adventurerService.deleteById(1)
        }

        @Test
        fun `deleteById when the adventurer is a party founder`() {
            `when`(
                adventurerRepository.findById(1),
            ).thenReturn(
                Optional.of(
                    Adventurer(
                        id = 1,
                        name = "name1",
                        party =
                            Party(
                                id = 42,
                                name = "",
                                founder = Adventurer(name = ""),
                                adventurers = emptyList(),
                            ),
                    ),
                ),
            )
            `when`(partyRepository.findById(42)).thenReturn(
                Optional.of(
                    Party(
                        id = 42,
                        name = "",
                        founder = Adventurer(id = 1, name = ""),
                        adventurers = emptyList(),
                    ),
                ),
            )
            assertThrows(FounderCannotBeDeletedException::class.java) {
                adventurerService.deleteById(1)
            }
            verify(adventurerRepository, never()).delete(any())
        }
    }

    @Test
    fun count() {
        `when`(adventurerRepository.count()).thenReturn(42)
        assertEquals(42, adventurerService.count())
    }
}

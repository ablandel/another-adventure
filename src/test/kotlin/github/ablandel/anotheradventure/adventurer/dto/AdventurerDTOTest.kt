package github.ablandel.anotheradventure.adventurer.dto

import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.party.entity.Party
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AdventurerDTOTest {
    @Nested
    internal inner class ToEntity {
        @Test
        fun `toEntity when the ID is null`() {
            val expected = Adventurer(name = "name")
            val adventurerDTO = AdventurerDTO(name = "name")
            assertEquals(expected, adventurerDTO.toEntity())
        }

        @Test
        fun `toEntity when no party set`() {
            val expected = Adventurer(id = 42, name = "name")
            val adventurerDTO = AdventurerDTO(id = 42, name = "name")
            assertEquals(expected, adventurerDTO.toEntity())
        }

        @Test
        fun `toEntity with party`() {
            val expected =
                Adventurer(
                    id = 42,
                    name = "name",
                    party =
                        Party(
                            id = 666,
                            name = "",
                            founder = Adventurer(name = ""),
                            adventurers = emptyList(),
                        ),
                )
            val adventurerDTO = AdventurerDTO(id = 42, name = "name", partyId = 666)
            assertEquals(expected, adventurerDTO.toEntity())
        }
    }
}

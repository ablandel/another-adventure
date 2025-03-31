package github.ablandel.anotheradventure.party.dto

import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.party.entity.Party
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PartyDTOTest {
    @Nested
    internal inner class ToEntity {
        @Test
        fun `toEntity when the ID is null`() {
            val expected =
                Party(
                    name = "name",
                    founder = Adventurer(id = 666, name = ""),
                    adventurers = emptyList(),
                )
            val partyDTO = PartyDTO(name = "name", founderId = 666, adventurerIds = emptyList())
            assertEquals(expected, partyDTO.toEntity())
        }

        @Test
        fun `toEntity with multiple adventurers`() {
            val expected =
                Party(
                    id = 42,
                    name = "name",
                    founder = Adventurer(id = 666, name = ""),
                    adventurers =
                        listOf(
                            Adventurer(id = 1, name = ""),
                            Adventurer(id = 2, name = ""),
                            Adventurer(id = 3, name = ""),
                        ),
                )
            val partyDTO = PartyDTO(id = 42, name = "name", founderId = 666, adventurerIds = listOf(1, 2, 3))
            assertEquals(expected, partyDTO.toEntity())
        }
    }
}

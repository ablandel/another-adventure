package github.ablandel.anotheradventure.party.entity

import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.party.dto.PartyDTO
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PartyTest {
    @Nested
    internal inner class ToDTO {
        @Test
        fun `toEntity when the ID is null`() {
            val expected = PartyDTO(name = "name", founderId = 666, adventurerIds = emptyList())
            val party =
                Party(
                    name = "name",
                    founder = Adventurer(id = 666, name = ""),
                    adventurers = emptyList(),
                )
            assertEquals(expected, party.toDTO())
        }

        @Test
        fun `toEntity with multiple adventurers`() {
            val expected = PartyDTO(name = "name", founderId = 666, adventurerIds = listOf(1, 2, 3))
            val party =
                Party(
                    name = "name",
                    founder = Adventurer(id = 666, name = ""),
                    adventurers =
                        listOf(
                            Adventurer(id = 1, name = ""),
                            Adventurer(id = 2, name = ""),
                            Adventurer(id = 3, name = ""),
                        ),
                )
            assertEquals(expected, party.toDTO())
        }
    }
}

package github.ablandel.anotheradventure.adventurer.entity

import github.ablandel.anotheradventure.adventurer.dto.AdventurerDTO
import github.ablandel.anotheradventure.party.entity.Party
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AdventurerTest {
    @Nested
    internal inner class ToDTO {
        @Test
        fun `toEntity when the ID is null`() {
            val expected = AdventurerDTO(name = "name", partyId = 42)
            val adventurer =
                Adventurer(
                    name = "name",
                    party =
                        Party(
                            id = 42,
                            name = "",
                            founder = Adventurer(name = ""),
                            adventurers = emptyList(),
                        ),
                )
            assertEquals(expected, adventurer.toDTO())
        }

        @Test
        fun `toEntity when the party ID is null`() {
            val expected = AdventurerDTO(id = 42, name = "name")
            val adventurer =
                Adventurer(
                    id = 42,
                    name = "name",
                )
            assertEquals(expected, adventurer.toDTO())
        }
    }

    @Test
    fun cloneWithParty() {
        val adventurer =
            Adventurer(
                id = 42,
                name = "name",
            )
        val party =
            Party(
                id = 42,
                name = "",
                founder = Adventurer(name = ""),
                adventurers = emptyList(),
            )
        val expected =
            Adventurer(
                id = 42,
                name = "name",
                party = party,
            )
        assertEquals(expected, adventurer.cloneWithParty(party))
    }
}

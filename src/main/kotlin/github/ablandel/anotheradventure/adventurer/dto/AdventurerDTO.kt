package github.ablandel.anotheradventure.adventurer.dto

import com.fasterxml.jackson.annotation.JsonInclude
import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.party.entity.Party
import github.ablandel.anotheradventure.shared.validation.ValidationContext.OnCreation
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class AdventurerDTO(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Null(groups = [OnCreation::class])
    val id: Long? = null,
    @field:Size(
        min = 1,
        max = 40,
        groups = [OnCreation::class],
    )
    val name: String,
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Positive(
        groups = [OnCreation::class],
        message = "has an invalid ID value",
    )
    val partyId: Long? = null,
)

fun AdventurerDTO.toEntity(): Adventurer =
    Adventurer(
        id = id,
        name = name,
        party =
            partyId?.let {
                Party(
                    id = it,
                    name = "",
                    founder = Adventurer(name = ""),
                    adventurers = emptyList(),
                )
            },
    )

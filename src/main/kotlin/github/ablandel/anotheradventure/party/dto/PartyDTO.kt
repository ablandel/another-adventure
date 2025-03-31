package github.ablandel.anotheradventure.party.dto

import com.fasterxml.jackson.annotation.JsonInclude
import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.party.entity.Party
import github.ablandel.anotheradventure.shared.validation.ValidationContext.OnCreation
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class PartyDTO(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    @field:Null(groups = [OnCreation::class])
    val id: Long? = null,
    @field:Size(
        min = 1,
        max = 60,
        groups = [OnCreation::class],
    )
    val name: String,
    @field:Positive(
        groups = [OnCreation::class],
        message = "has an invalid ID value",
    )
    val founderId: Long,
    @field:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:NotEmpty(
        groups = [OnCreation::class],
        message = "must at least include the founder ID",
    )
    val adventurerIds: List<Long>,
)

fun PartyDTO.toEntity(): Party =
    Party(
        id = id,
        name = name,
        founder =
            Adventurer(
                id = founderId,
                name = "",
            ),
        adventurers = adventurerIds.map { Adventurer(id = it, name = "") },
    )

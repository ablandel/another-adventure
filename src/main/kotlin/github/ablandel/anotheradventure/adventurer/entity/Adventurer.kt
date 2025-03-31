package github.ablandel.anotheradventure.adventurer.entity

import github.ablandel.anotheradventure.adventurer.dto.AdventurerDTO
import github.ablandel.anotheradventure.party.entity.Party
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity(name = "adventurer")
@Table(name = "adventurer")
data class Adventurer(
    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: Instant? = null,
    @UpdateTimestamp
    val updatedAt: Instant? = null,
    val name: String,
    @ManyToOne
    @JoinColumn(name = "party_id")
    val party: Party? = null,
)

fun Adventurer.toDTO(): AdventurerDTO =
    AdventurerDTO(
        id = id,
        name = name,
        partyId = party?.id,
    )

fun Adventurer.cloneWithParty(party: Party): Adventurer =
    Adventurer(
        id = id,
        name = name,
        party = party,
    )

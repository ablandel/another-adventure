package github.ablandel.anotheradventure.party.entity

import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import github.ablandel.anotheradventure.party.dto.PartyDTO
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity(name = "party")
@Table(name = "party")
data class Party(
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
    @OneToOne
    @JoinColumn(name = "founder_id")
    val founder: Adventurer,
    @OneToMany(mappedBy = "party")
    val adventurers: List<Adventurer>,
)

fun Party.toDTO(): PartyDTO =
    PartyDTO(
        id = id,
        name = name,
        founderId = founder.id!!,
        adventurerIds = adventurers.map { it.id!! },
    )

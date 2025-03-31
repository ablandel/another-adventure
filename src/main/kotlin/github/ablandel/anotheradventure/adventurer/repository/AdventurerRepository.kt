package github.ablandel.anotheradventure.adventurer.repository

import github.ablandel.anotheradventure.adventurer.entity.Adventurer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AdventurerRepository : JpaRepository<Adventurer, Long> {
    override fun findById(id: Long): Optional<Adventurer>

    @Query("SELECT a FROM adventurer a ORDER BY id LIMIT :limit OFFSET :offset")
    fun findWithLimitAndOffset(
        offset: Int,
        limit: Int,
    ): List<Adventurer>

    @Query("SELECT a FROM adventurer a WHERE a.id > :cursor ORDER BY id LIMIT :limit")
    fun findWithLimitAndCursor(
        cursor: Long,
        limit: Int,
    ): List<Adventurer>

    fun findByName(name: String): Optional<Adventurer>

    @Query("SELECT a FROM adventurer a WHERE a.id IN :ids")
    fun findInIds(ids: List<Long>): List<Adventurer>
}

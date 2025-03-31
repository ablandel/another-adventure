package github.ablandel.anotheradventure.party.repository

import github.ablandel.anotheradventure.party.entity.Party
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PartyRepository : JpaRepository<Party, Long> {
    override fun findById(id: Long): Optional<Party>

    @Query("SELECT p FROM party p ORDER BY id LIMIT :limit OFFSET :offset")
    fun findWithLimitAndOffset(
        offset: Int,
        limit: Int,
    ): List<Party>

    @Query("SELECT p FROM party p WHERE p.id > :cursor ORDER BY id LIMIT :limit")
    fun findWithLimitAndCursor(
        cursor: Long,
        limit: Int,
    ): List<Party>

    fun findByName(name: String): Optional<Party>

    @Query("SELECT COUNT(p) FROM party p WHERE p.founder.id = :founderId")
    fun countByFounderId(founderId: Long): Long
}

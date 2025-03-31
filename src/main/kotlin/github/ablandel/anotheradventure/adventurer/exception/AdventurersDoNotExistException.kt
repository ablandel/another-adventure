package github.ablandel.anotheradventure.adventurer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AdventurersDoNotExistException(
    ids: List<Long>,
) : ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Adventurers with IDs `$ids` does not exist",
    )

package github.ablandel.anotheradventure.party.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AdventurersAreAlreadyInAnotherPartyException(
    ids: List<Long>,
) : ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Adventurers with IDs `$ids` are already in another party",
    )

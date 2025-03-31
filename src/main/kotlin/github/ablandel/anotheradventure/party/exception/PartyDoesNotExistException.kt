package github.ablandel.anotheradventure.party.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class PartyDoesNotExistException(
    id: Long,
) : ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The party with ID `$id` does not exist",
    )

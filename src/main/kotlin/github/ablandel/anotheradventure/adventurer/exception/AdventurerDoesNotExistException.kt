package github.ablandel.anotheradventure.adventurer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AdventurerDoesNotExistException(
    id: Long,
) : ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The adventurer with ID `$id` does not exist",
    )

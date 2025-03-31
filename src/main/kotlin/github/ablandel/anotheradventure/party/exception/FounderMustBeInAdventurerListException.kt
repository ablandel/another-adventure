package github.ablandel.anotheradventure.party.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class FounderMustBeInAdventurerListException :
    ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The founder must be in the adventurer list",
    )

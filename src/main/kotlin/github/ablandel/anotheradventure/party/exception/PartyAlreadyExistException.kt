package github.ablandel.anotheradventure.party.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class PartyAlreadyExistException :
    ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The desired name is already used by another party",
    )

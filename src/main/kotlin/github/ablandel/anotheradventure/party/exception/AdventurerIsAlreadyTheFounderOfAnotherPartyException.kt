package github.ablandel.anotheradventure.party.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AdventurerIsAlreadyTheFounderOfAnotherPartyException :
    ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The desired adventurer founder is already the founder of another party",
    )

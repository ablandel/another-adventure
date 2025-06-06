package github.ablandel.anotheradventure.adventurer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AdventurerAlreadyExistException :
    ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The desired name is already used by another adventurer",
    )

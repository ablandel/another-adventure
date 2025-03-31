package github.ablandel.anotheradventure.party.resource

import github.ablandel.anotheradventure.adventurer.exception.AdventurerDoesNotExistException
import github.ablandel.anotheradventure.adventurer.exception.AdventurersDoNotExistException
import github.ablandel.anotheradventure.party.dto.PartyDTO
import github.ablandel.anotheradventure.party.exception.AdventurerIsAlreadyTheFounderOfAnotherParty
import github.ablandel.anotheradventure.party.exception.AdventurersAreAlreadyInAnotherParty
import github.ablandel.anotheradventure.party.exception.FounderMustBeInAdventurerList
import github.ablandel.anotheradventure.party.exception.PartyAlreadyExistException
import github.ablandel.anotheradventure.party.exception.PartyDoesNotExistException
import github.ablandel.anotheradventure.party.service.PartyService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(controllers = [PartyResource::class])
@AutoConfigureMockMvc()
class PartyResourceTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var partyService: PartyService

    @Nested
    internal inner class FindWithPagination {
        @Test
        fun `findWithPagination without parties and default pagination parameters`() {
            `when`(partyService.findWithPagination(any())).thenReturn(emptyList())
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/parties"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "[]",
                        ),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Default-Limit", "20"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Count", "0"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Total-Count", "0"),
                )
        }

        @Test
        fun `findWithPagination with parties (offset)`() {
            `when`(partyService.findWithPagination(any())).thenReturn(
                listOf(
                    PartyDTO(id = 1, name = "name1", founderId = 1, adventurerIds = listOf(1)),
                    PartyDTO(id = 2, name = "name2", founderId = 3, adventurerIds = listOf(3, 4)),
                    PartyDTO(id = 3, name = "name3", founderId = 5, adventurerIds = listOf(5)),
                ),
            )
            `when`(partyService.count()).thenReturn(3)
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/parties?limit=666&offset=77"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "[{\"id\":1,\"name\":\"name1\",\"founderId\":1,\"adventurerIds\":[1]},{\"id\":2,\"name\":\"name2\",\"founderId\":3,\"adventurerIds\":[3,4]},{\"id\":3,\"name\":\"name3\",\"founderId\":5,\"adventurerIds\":[5]}]",
                        ),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .doesNotExist("Pagination-Default-Limit"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Count", "3"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Total-Count", "3"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Offset", "77"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Cursor", "3"),
                )
        }

        @Test
        fun `findWithPagination with parties (cursor)`() {
            `when`(partyService.findWithPagination(any())).thenReturn(
                listOf(
                    PartyDTO(id = 1, name = "name1", founderId = 1, adventurerIds = listOf(1)),
                    PartyDTO(id = 2, name = "name2", founderId = 3, adventurerIds = listOf(3, 4)),
                    PartyDTO(id = 3, name = "name3", founderId = 5, adventurerIds = listOf(5)),
                ),
            )
            `when`(partyService.count()).thenReturn(3)
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/parties?limit=666&cursor=77"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "[{\"id\":1,\"name\":\"name1\",\"founderId\":1,\"adventurerIds\":[1]},{\"id\":2,\"name\":\"name2\",\"founderId\":3,\"adventurerIds\":[3,4]},{\"id\":3,\"name\":\"name3\",\"founderId\":5,\"adventurerIds\":[5]}]",
                        ),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .doesNotExist("Pagination-Default-Limit"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Count", "3"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Total-Count", "3"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .doesNotExist("Pagination-Offset"),
                ).andExpect(
                    MockMvcResultMatchers
                        .header()
                        .string("Pagination-Cursor", "3"),
                )
        }
    }

    @Test
    fun count() {
        `when`(partyService.count()).thenReturn(42)
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/v1/parties/count"),
            ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers
                    .content()
                    .string("{\"count\":42}"),
            )
    }

    @Nested
    internal inner class FindById {
        @Test
        @Throws(Exception::class)
        fun `findById when the adventurer does not exist`() {
            `when`(partyService.findById(42)).thenThrow(PartyDoesNotExistException(42))
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/parties/42"),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The party with ID `42` does not exist\",\"instance\":\"/v1/parties/42\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `findById when the fetch succeed`() {
            `when`(
                partyService.findById(42),
            ).thenReturn(PartyDTO(id = 42, name = "name", founderId = 666, adventurerIds = listOf(1, 2, 3)))
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/parties/42"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"id\":42,\"name\":\"name\",\"founderId\":666,\"adventurerIds\":[1,2,3]}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `findById when the fetch succeed (without ID and adventurers)`() {
            `when`(partyService.findById(42)).thenReturn(
                PartyDTO(
                    name = "name",
                    founderId = 666,
                    adventurerIds = emptyList(),
                ),
            )
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/parties/42"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"name\":\"name\",\"founderId\":666}",
                        ),
                )
        }
    }

    @Nested
    internal inner class Create {
        @Test
        @Throws(Exception::class)
        fun `create when the party already exists`() {
            `when`(partyService.create(any())).thenThrow(PartyAlreadyExistException())
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The desired name is already used by another party\",\"instance\":\"/v1/parties\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the founder does not exist`() {
            `when`(partyService.create(any())).thenThrow(AdventurerDoesNotExistException(42))
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The adventurer with ID `42` does not exist\",\"instance\":\"/v1/parties\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the founder is already the founder of another party`() {
            `when`(partyService.create(any())).thenThrow(AdventurerIsAlreadyTheFounderOfAnotherParty())
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The desired adventurer founder is already the founder of another party\",\"instance\":\"/v1/parties\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the founder is not the adventurer list`() {
            `when`(partyService.create(any())).thenThrow(FounderMustBeInAdventurerList())
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The founder must be in the adventurer list\",\"instance\":\"/v1/parties\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when some adventures in the list do not exist`() {
            `when`(partyService.create(any())).thenThrow(AdventurersDoNotExistException(listOf(2, 3)))
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Adventurers with IDs `[2, 3]` does not exist\",\"instance\":\"/v1/parties\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when some adventures in the list are already in other parties`() {
            `when`(partyService.create(any())).thenThrow(AdventurersAreAlreadyInAnotherParty(listOf(2, 3)))
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"Adventurers with IDs `[2, 3]` are already in another party\",\"instance\":\"/v1/parties\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the creation succeed`() {
            `when`(partyService.create(any())).thenReturn(
                PartyDTO(
                    id = 1,
                    name = "name",
                    founderId = 42,
                    adventurerIds = listOf(2, 3),
                ),
            )
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"id\":1,\"name\":\"name\",\"founderId\":42,\"adventurerIds\":[2,3]}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the ID is not null`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"id\":1,\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`id` must be null\",\"instance\":\"/v1/parties\"}",
                        ),
                )
            verify(partyService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the name is an empty string`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`name` size must be between 1 and 60\",\"instance\":\"/v1/parties\"}",
                        ),
                )
            verify(partyService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the name is more than sixty characters`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content(
                            "{\"name\":\"${
                                List(
                                    61,
                                ) { ('a'..'z').random() }.joinToString("")
                            }\",\"founderId\":\"42\",\"adventurerIds\":[2,3]}",
                        ).contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`name` size must be between 1 and 60\",\"instance\":\"/v1/parties\"}",
                        ),
                )
            verify(partyService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the founder ID is not valid`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"0\",\"adventurerIds\":[2,3]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`founderId` has an invalid ID value\",\"instance\":\"/v1/parties\"}",
                        ),
                )
            verify(partyService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the adventurer list is empty`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/parties")
                        .content("{\"name\":\"name\",\"founderId\":\"42\",\"adventurerIds\":[]}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`adventurerIds` must at least include the founder ID\",\"instance\":\"/v1/parties\"}",
                        ),
                )
            verify(partyService, never()).create(any())
        }
    }

    @Nested
    internal inner class DeleteById {
        @Test
        @Throws(Exception::class)
        fun `deleteById when the party does not exist`() {
            doThrow(PartyDoesNotExistException(42)).`when`(partyService).deleteById(42)
            mockMvc
                .perform(
                    MockMvcRequestBuilders.delete("/v1/parties/42"),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The party with ID `42` does not exist\",\"instance\":\"/v1/parties/42\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `deleteById when the deletion succeed`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders.delete("/v1/parties/42"),
                ).andExpect(MockMvcResultMatchers.status().isNoContent)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "",
                        ),
                )
        }
    }
}

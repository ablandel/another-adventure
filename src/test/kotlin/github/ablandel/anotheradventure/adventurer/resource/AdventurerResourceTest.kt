package github.ablandel.anotheradventure.adventurer.resource

import github.ablandel.anotheradventure.adventurer.dto.AdventurerDTO
import github.ablandel.anotheradventure.adventurer.exception.AdventurerAlreadyExistException
import github.ablandel.anotheradventure.adventurer.exception.AdventurerDoesNotExistException
import github.ablandel.anotheradventure.adventurer.service.AdventurerService
import github.ablandel.anotheradventure.party.exception.PartyDoesNotExistException
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

@WebMvcTest(controllers = [AdventurerResource::class])
@AutoConfigureMockMvc()
class AdventurerResourceTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var adventurerService: AdventurerService

    @Nested
    internal inner class FindWithPagination {
        @Test
        fun `findWithPagination without adventurers and default pagination parameters`() {
            `when`(adventurerService.findWithPagination(any())).thenReturn(emptyList())
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/adventurers"),
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
        fun `findWithPagination with adventurers (offset)`() {
            `when`(adventurerService.findWithPagination(any())).thenReturn(
                listOf(
                    AdventurerDTO(id = 1, name = "name1"),
                    AdventurerDTO(id = 2, name = "name2", partyId = 4),
                    AdventurerDTO(id = 3, name = "name3", partyId = 5),
                ),
            )
            `when`(adventurerService.count()).thenReturn(3)
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/adventurers?limit=666&offset=77"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "[{\"id\":1,\"name\":\"name1\"},{\"id\":2,\"name\":\"name2\",\"partyId\":4},{\"id\":3,\"name\":\"name3\",\"partyId\":5}]",
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
        fun `findWithPagination with adventurers (cursor)`() {
            `when`(adventurerService.findWithPagination(any())).thenReturn(
                listOf(
                    AdventurerDTO(id = 1, name = "name1"),
                    AdventurerDTO(id = 2, name = "name2", partyId = 4),
                    AdventurerDTO(id = 3, name = "name3", partyId = 5),
                ),
            )
            `when`(adventurerService.count()).thenReturn(3)
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/adventurers?limit=666&cursor=77"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "[{\"id\":1,\"name\":\"name1\"},{\"id\":2,\"name\":\"name2\",\"partyId\":4},{\"id\":3,\"name\":\"name3\",\"partyId\":5}]",
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
        `when`(adventurerService.count()).thenReturn(42)
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/v1/adventurers/count"),
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
            `when`(adventurerService.findById(42)).thenThrow(AdventurerDoesNotExistException(42))
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/adventurers/42"),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The adventurer with ID `42` does not exist\",\"instance\":\"/v1/adventurers/42\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `findById when the fetch succeed`() {
            `when`(adventurerService.findById(42)).thenReturn(AdventurerDTO(id = 42, name = "name", partyId = 666))
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/adventurers/42"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"id\":42,\"name\":\"name\",\"partyId\":666}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `findById when the fetch succeed (without ID and party)`() {
            `when`(adventurerService.findById(42)).thenReturn(AdventurerDTO(name = "name"))
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/v1/adventurers/42"),
                ).andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"name\":\"name\"}",
                        ),
                )
        }
    }

    @Nested
    internal inner class Create {
        @Test
        @Throws(Exception::class)
        fun `create when the adventurer already exists`() {
            `when`(adventurerService.create(any())).thenThrow(AdventurerAlreadyExistException())
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"name\":\"name\"}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The desired name is already used by another adventurer\",\"instance\":\"/v1/adventurers\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the party does not exist`() {
            `when`(adventurerService.create(any())).thenThrow(PartyDoesNotExistException(666))
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"name\":\"name\",\"partyId\":\"666\"}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The party with ID `666` does not exist\",\"instance\":\"/v1/adventurers\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the creation succeed`() {
            `when`(adventurerService.create(any())).thenReturn(AdventurerDTO(id = 1, name = "name", partyId = 2))
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"name\":\"name\",\"partyId\":\"2\"}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"id\":1,\"name\":\"name\",\"partyId\":2}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `create when the ID is not null`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"id\":1,\"name\":\"name\",\"partyId\":666}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`id` must be null\",\"instance\":\"/v1/adventurers\"}",
                        ),
                )
            verify(adventurerService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the name is an empty string`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"name\":\"\",\"partyId\":666}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`name` size must be between 1 and 40\",\"instance\":\"/v1/adventurers\"}",
                        ),
                )
            verify(adventurerService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the name is more than forty characters`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"name\":\"${List(41) { ('a'..'z').random() }.joinToString("")}\",\"partyId\":666}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`name` size must be between 1 and 40\",\"instance\":\"/v1/adventurers\"}",
                        ),
                )
            verify(adventurerService, never()).create(any())
        }

        @Test
        @Throws(Exception::class)
        fun `create when the party ID is not valid`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders
                        .post("/v1/adventurers")
                        .content("{\"name\":\"name\",\"partyId\":0}")
                        .contentType(
                            MediaType.APPLICATION_JSON,
                        ),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"`partyId` has an invalid ID value\",\"instance\":\"/v1/adventurers\"}",
                        ),
                )
            verify(adventurerService, never()).create(any())
        }
    }

    @Nested
    internal inner class DeleteById {
        @Test
        @Throws(Exception::class)
        fun `deleteById when the adventurer does not exist`() {
            doThrow(AdventurerDoesNotExistException(42)).`when`(adventurerService).deleteById(42)
            mockMvc
                .perform(
                    MockMvcRequestBuilders.delete("/v1/adventurers/42"),
                ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(
                    MockMvcResultMatchers
                        .content()
                        .string(
                            "{\"type\":\"about:blank\",\"title\":\"Bad Request\",\"status\":400,\"detail\":\"The adventurer with ID `42` does not exist\",\"instance\":\"/v1/adventurers/42\"}",
                        ),
                )
        }

        @Test
        @Throws(Exception::class)
        fun `deleteById when the deletion succeed`() {
            mockMvc
                .perform(
                    MockMvcRequestBuilders.delete("/v1/adventurers/42"),
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

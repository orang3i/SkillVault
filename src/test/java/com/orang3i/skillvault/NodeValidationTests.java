package com.orang3i.skillvault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class NodeValidationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void createNode_withValidData_shouldSucceed() throws Exception {
        String validJson = """
                {
                    "title": "Java Programming",
                    "description": "Learning Java fundamentals",
                    "category": "Programming",
                    "mastery": 75
                }
                """;

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.mastery").value(75));
    }

    @Test
    void createNode_withBlankTitle_shouldFail() throws Exception {
        String invalidJson = """
                {
                    "title": "   ",
                    "mastery": 50
                }
                """;

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.title").exists());
    }

    @Test
    void createNode_withNullTitle_shouldFail() throws Exception {
        String invalidJson = """
                {
                    "title": null,
                    "mastery": 50
                }
                """;

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createNode_withTitleTooLong_shouldFail() throws Exception {
        String longTitle = "a".repeat(201);
        String invalidJson = String.format("""
                {
                    "title": "%s",
                    "mastery": 50
                }
                """, longTitle);

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.title").exists());
    }

    @Test
    void createNode_withMasteryNegative_shouldFail() throws Exception {
        String invalidJson = """
                {
                    "title": "Test Node",
                    "mastery": -1
                }
                """;

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.mastery").exists());
    }

    @Test
    void createNode_withMasteryOver100_shouldFail() throws Exception {
        String invalidJson = """
                {
                    "title": "Test Node",
                    "mastery": 101
                }
                """;

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.mastery").exists());
    }

    @Test
    void createNode_withDescriptionTooLong_shouldFail() throws Exception {
        String longDesc = "a".repeat(5001);
        String invalidJson = String.format("""
                {
                    "title": "Test Node",
                    "description": "%s",
                    "mastery": 50
                }
                """, longDesc);

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.description").exists());
    }

    @Test
    void createNode_withCategoryTooLong_shouldFail() throws Exception {
        String longCategory = "a".repeat(101);
        String invalidJson = String.format("""
                {
                    "title": "Test Node",
                    "category": "%s",
                    "mastery": 50
                }
                """, longCategory);

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fields.category").exists());
    }

    @Test
    void updateNode_withValidData_shouldSucceed() throws Exception {
        String createJson = """
                {
                    "title": "Original Title",
                    "mastery": 30
                }
                """;

        String createResponse = mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String nodeId = createResponse.split("\"id\":\"")[1].split("\"")[0];

        String updateJson = """
                {
                    "title": "Updated Title",
                    "description": "Updated description",
                    "category": "Updated category",
                    "mastery": 80
                }
                """;

        mockMvc.perform(put("/api/nodes/" + nodeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.mastery").value(80));
    }

    @Test
    void updateNode_withInvalidData_shouldFail() throws Exception {
        String createJson = """
                {
                    "title": "Test Node",
                    "mastery": 30
                }
                """;

        String createResponse = mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String nodeId = createResponse.split("\"id\":\"")[1].split("\"")[0];

        String updateJson = """
                {
                    "title": "",
                    "mastery": 150
                }
                """;

        mockMvc.perform(put("/api/nodes/" + nodeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}

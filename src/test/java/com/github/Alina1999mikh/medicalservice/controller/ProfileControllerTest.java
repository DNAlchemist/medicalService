package com.github.Alina1999mikh.medicalservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProfileControllerTest {

    @Autowired
    TestRestTemplate template= new TestRestTemplate("user1", "user1Pass");

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Should create a profile")
    void createNote() {
        assertThat(
                template.exchange("/v1/profile", HttpMethod.POST, new HttpEntity<>("""
                        {
                          "userUuid": "b5871b0b-e0e4-4053-9fc8-2782a217ce0a",
                          "username": "deswier",
                          "date": "1999-06-02",
                          "gender": "F",
                          "fname": "Alina",
                          "sname": "Mikhaleva"
                        }
                        """, headers()), String.class)
        )
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);
    }

    @Operation(summary = "create", security = @SecurityRequirement(name="basicAuth"))
    @Test
    @DisplayName("Should return note by UUID")
    void findNoteByUuid() {
        // given
        assertThat(template.exchange("/v1/note", HttpMethod.POST, new HttpEntity<>("""
                {
                            "user_id": "1",
                            "uuid": "b5871b6b-e0e4-4053-9fc8-2782a217ce0a",
                            "lab": "invitro",
                            "test": "Fe",
                            "date": "2021-03-02",
                            "result": "6.42",
                            "referenceRange": "9-30.4",
                            "unit": "мкмоль/л",
                            "comment": "Тестовый тест"
                }
                """, headers()), String.class)
        )
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        assertThat(template.exchange("/v1/note", HttpMethod.POST, new HttpEntity<>("""
                {
                            "user_id": "1",
                            "uuid": "ed0bdada-dfad-42e3-aebd-f6e637dbd2a8",
                            "lab": "invitro",
                            "test": "HbA1C",
                            "date": "2021-03-02",
                            "result": "5.5",
                            "referenceRange": "0-6",
                            "unit": "%",
                            "comment": "Тестовый тест 2"
                }
                """, headers()), String.class)
        )
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        // when
        assertThat(
                template.exchange("/v1/note/{uuid}", HttpMethod.GET, new HttpEntity<>(headers()), String.class,"b5871b6b-e0e4-4053-9fc8-2782a217ce0a"))
                .extracting(ResponseEntity::getBody)
                .isEqualTo("""
        {"user_id":1,"uuid":"b5871b6b-e0e4-4053-9fc8-2782a217ce0a","lab":"invitro","test":"Fe","date":"2021-03-02","result":"6.42","referenceRange":"9-30.4","unit":"мкмоль/л","comment":"Тестовый тест"}""");
    }

    @Operation(summary = "create", security = @SecurityRequirement(name="basicAuth"))
    @Test
    @DisplayName("Should return all notes")
    void getAllNotes() {
        // given
        assertThat(template.exchange("/v1/note", HttpMethod.POST, new HttpEntity<>("""
                {
                            "user_id": "1",
                            "uuid": "b5871b6b-e0e4-4053-9fc8-2782a217ce0a",
                            "lab": "invitro",
                            "test": "HbA1C",
                            "date": "2021-03-02",
                            "result": "6.4",
                            "referenceRange": "0-6",
                            "unit": "мкмоль/л",
                            "comment": "Тестовый тест"
                }
                """, headers()), String.class)
        )
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        assertThat(template.exchange("/v1/note", HttpMethod.POST, new HttpEntity<>("""
                {
                            "user_id": "1",
                            "uuid": "ed0bdada-dfad-42e3-aebd-f6e637dbd2a8",
                            "lab": "invitro",
                            "test": "HbA1C",
                            "date": "2021-03-02",
                            "result": "5.5",
                            "referenceRange": "0-6",
                            "unit": "%",
                            "comment": "Тестовый тест 2"
                }
                """, headers()), String.class)
        )
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.CREATED);

        // when
        assertThat(
                template.exchange("/v1/note/", HttpMethod.GET, new HttpEntity<>(headers()), String.class))
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(
                template.exchange("/v1/note/", HttpMethod.GET, new HttpEntity<>(headers()), String.class))
                .extracting(ResponseEntity::getBody)
                .isEqualTo("""
      [{"user_id":1,"uuid":"b5871b6b-e0e4-4053-9fc8-2782a217ce0a","lab":"invitro","test":"HbA1C","date":"2021-03-02","result":"6.4","referenceRange":"0-6","unit":"мкмоль/л","comment":"Тестовый тест"},{"user_id":1,"uuid":"ed0bdada-dfad-42e3-aebd-f6e637dbd2a8","lab":"invitro","test":"HbA1C","date":"2021-03-02","result":"5.5","referenceRange":"0-6","unit":"%","comment":"Тестовый тест 2"}]""");
    }


    @Test
    @DisplayName("Should return NOT_FOUND  if note doesn't exist")
    @Operation(summary = "create", security = @SecurityRequirement(name="basicAuth"))
    void shouldReturnNotFoundIfNoteNotExists() {
        template.exchange("/v1/note", HttpMethod.POST, new HttpEntity<>("""
                {
                            "user_id": "1",
                            "uuid": "b5871b6b-e0e4-4053-9fc8-2782a217ce0a",
                            "lab": "invitro",
                            "test": "Fe",
                            "date": "02.03.2021",
                            "result": "6.42",
                            "referenceRange": "9-30.4",
                            "unit": "мкмоль/л"
                }
                """, headers()), String.class);

        assertThat(
                template.exchange("/v1/note/{uuid}", HttpMethod.GET, new HttpEntity<>(headers()), String.class,"a8871b6b-e0e4-4053-9fc8-2782a217ce0a"))
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Operation(summary = "create", security = @SecurityRequirement(name="basicAuth"))
    @DisplayName("Should delete a note")
    void deleteNoteByUuid() {
        // given
        template.exchange("/v1/note", HttpMethod.POST, new HttpEntity<>("""
                {
                            "user_id": "1",
                            "uuid": "b5871b6b-e0e4-4053-9fc8-2782a217ce0a",
                            "lab": "invitro",
                            "test": "Fe",
                            "date": "02.03.2021",
                            "result": "6.42",
                            "referenceRange": "9-30.4",
                            "unit": "мкмоль/л"
                }
                """, headers()), String.class);
        // when
        assertThat(
                template.exchange("/v1/note/delete/{uuid}", HttpMethod.DELETE, new HttpEntity<>(headers()), String.class, "b5871b6b-e0e4-4053-9fc8-2782a217ce0a"))
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        assertThat(
                template.exchange("/v1/note/{uuid}", HttpMethod.GET, new HttpEntity<>(headers()), String.class, "b5871b6b-e0e4-4053-9fc8-2782a217ce0a"))
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM PROFILES");
    }

    private HttpHeaders headers() {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic dXNlcjE6dXNlcjFQYXNz");
        return headers;
    }
}
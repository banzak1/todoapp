package com.banzak.todoapp.interfaces.rest;

import com.banzak.todoapp.application.TaskNotFoundException;
import com.banzak.todoapp.application.TaskService;
import com.banzak.todoapp.interfaces.rest.dto.CreateTaskRequest;
import com.banzak.todoapp.interfaces.rest.dto.TaskResponse;
import com.banzak.todoapp.interfaces.rest.dto.UpdateTaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController")
class TaskControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private final LocalDateTime now = LocalDateTime.now();

    private TaskResponse sampleResponse(Long id) {
        return new TaskResponse(id, "Test Task", "Description",
                "MEDIUM", "TODO", now, now);
    }

    @Nested
    @DisplayName("POST /api/v1/tasks")
    class Create {

        @Test
        @DisplayName("should return 201 when creating a valid task")
        void shouldReturn201_whenValidRequest() throws Exception {
            var request = new CreateTaskRequest("New Task", "Desc", "HIGH");
            var response = new TaskResponse(1L, "New Task", "Desc",
                    "HIGH", "TODO", now, now);
            when(taskService.create(any(CreateTaskRequest.class))).thenReturn(response);

            mockMvc.post().uri("/api/v1/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .assertThat()
                    .hasStatus(201)
                    .bodyJson()
                    .extractingPath("$.title")
                    .asString()
                    .isEqualTo("New Task");
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void shouldReturn400_whenBlankTitle() throws Exception {
            var request = new CreateTaskRequest("", "Desc", null);

            mockMvc.post().uri("/api/v1/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .assertThat()
                    .hasStatus(400);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/tasks")
    class FindAll {

        @Test
        @DisplayName("should return 200 with task list")
        void shouldReturn200_whenTasksExist() {
            when(taskService.findAll()).thenReturn(List.of(sampleResponse(1L), sampleResponse(2L)));

            mockMvc.get().uri("/api/v1/tasks")
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.length()")
                    .asNumber()
                    .isEqualTo(2);
        }

        @Test
        @DisplayName("should return 200 with empty list")
        void shouldReturn200_whenNoTasks() {
            when(taskService.findAll()).thenReturn(List.of());

            mockMvc.get().uri("/api/v1/tasks")
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.length()")
                    .asNumber()
                    .isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/tasks/page")
    class FindAllPaginated {

        @Test
        @DisplayName("should return 200 with paginated content")
        void shouldReturn200_whenPaginated() {
            var pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(List.of(sampleResponse(1L)), pageable, 1);
            when(taskService.findAll(any(PageRequest.class), eq(null), eq(null))).thenReturn(page);

            mockMvc.get().uri("/api/v1/tasks/page")
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.content.length()")
                    .asNumber()
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("should return 200 with filter params")
        void shouldReturn200_withFilters() {
            var pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(List.of(sampleResponse(1L)), pageable, 1);
            when(taskService.findAll(any(PageRequest.class), eq("TODO"), eq("HIGH")))
                    .thenReturn(page);

            mockMvc.get().uri("/api/v1/tasks/page?status=TODO&priority=HIGH")
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.content.length()")
                    .asNumber()
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("should return 200 with empty page")
        void shouldReturn200_whenNoResults() {
            var pageable = PageRequest.of(0, 20);
            var page = new PageImpl<TaskResponse>(List.of(), pageable, 0);
            when(taskService.findAll(any(PageRequest.class), eq(null), eq(null))).thenReturn(page);

            mockMvc.get().uri("/api/v1/tasks/page")
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.content.length()")
                    .asNumber()
                    .isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/tasks/{id}")
    class FindById {

        @Test
        @DisplayName("should return 200 when task exists")
        void shouldReturn200_whenExists() {
            when(taskService.findById(1L)).thenReturn(sampleResponse(1L));

            mockMvc.get().uri("/api/v1/tasks/1")
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.id")
                    .asNumber()
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("should return 404 when task does not exist")
        void shouldReturn404_whenNotFound() {
            when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

            mockMvc.get().uri("/api/v1/tasks/99")
                    .assertThat()
                    .hasStatus(404)
                    .bodyJson()
                    .extractingPath("$.title")
                    .asString()
                    .isEqualTo("Not Found");
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/tasks/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when updating existing task")
        void shouldReturn200_whenValidUpdate() throws Exception {
            var request = new UpdateTaskRequest("Updated", "Desc", "LOW", "DONE");
            var response = new TaskResponse(1L, "Updated", "Desc",
                    "LOW", "DONE", now, now);
            when(taskService.update(eq(1L), any(UpdateTaskRequest.class))).thenReturn(response);

            mockMvc.put().uri("/api/v1/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .assertThat()
                    .hasStatusOk()
                    .bodyJson()
                    .extractingPath("$.title")
                    .asString()
                    .isEqualTo("Updated");
        }

        @Test
        @DisplayName("should return 404 when updating non-existent task")
        void shouldReturn404_whenNotFound() throws Exception {
            var request = new UpdateTaskRequest("Updated", null, null, null);
            when(taskService.update(eq(99L), any(UpdateTaskRequest.class)))
                    .thenThrow(new TaskNotFoundException(99L));

            mockMvc.put().uri("/api/v1/tasks/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .assertThat()
                    .hasStatus(404);
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/tasks/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when deleting existing task")
        void shouldReturn204_whenExists() {
            mockMvc.delete().uri("/api/v1/tasks/1")
                    .assertThat()
                    .hasStatus(204);
        }

        @Test
        @DisplayName("should return 404 when deleting non-existent task")
        void shouldReturn404_whenNotFound() {
            doThrow(new TaskNotFoundException(99L))
                    .when(taskService).delete(99L);

            mockMvc.delete().uri("/api/v1/tasks/99")
                    .assertThat()
                    .hasStatus(404);
        }
    }
}

package tests;

import application.Main;
import application.dto.UserDto;
import application.kafka.UserEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import application.services.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserEventProducer userEventProducer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        UserDto dto = new UserDto(1L, "Василий", "test@example.com", 30);

        Mockito.when(userService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Василий"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void getUserById_ShouldReturn404WhenNotFound() throws Exception {
        Mockito.when(userService.getById(99L))
                .thenThrow(new RuntimeException("Пользователь с ID 99 не найден"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_ShouldReturn201() throws Exception {
        UserDto dto = new UserDto(1L, "Василий", "test@example.com", 30);

        Mockito.when(userService.create("Василий", "test@example.com", 30)).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                        .param("name", "Василий")
                        .param("email", "test@example.com")
                        .param("age", "30"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Василий"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void createUser_ShouldReturn500WhenServiceFails() throws Exception {
        Mockito.when(userService.create(anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException("Ошибка при создании пользователя"));

        mockMvc.perform(post("/api/users")
                        .param("name", "Василий")
                        .param("email", "test@example.com")
                        .param("age", "30"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateUser_ShouldReturn200() throws Exception {
        UserDto requestDto = new UserDto(1L,"Екатерина", "newtest@example.com", 25);

        UserDto responseDto = new UserDto(1L,"Екатерина", "newtest@example.com", 25);

        Mockito.when(userService.update(eq(1L), eq("Екатерина"), eq("newtest@example.com"), eq(25)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Екатерина"))
                .andExpect(jsonPath("$.email").value("newtest@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void updateUser_ShouldReturn404WhenNotFound() throws Exception {
        UserDto requestDto = new UserDto(1L,"Екатерина", "newtest@example.com", 25);

        Mockito.when(userService.update(anyLong(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Пользователь с ID 99 не найден"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        Mockito.doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturn404WhenNotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Пользователь с ID 99 не найден"))
                .when(userService).delete(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        UserDto dto1 = new UserDto(1L, "Василий", "test@example.com", 30);

        UserDto dto2 = new UserDto(2L,"Екатерина", "newtest@example.com", 25);

        Mockito.when(userService.getAll()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Василий"))
                .andExpect(jsonPath("$[1].name").value("Екатерина"))
                .andExpect(jsonPath("$.length()").value(2));
    }
}
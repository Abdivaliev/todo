package com.epam.todo.controller;

import com.epam.todo.entity.Todo;
import com.epam.todo.repo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    public void getAllTodos_ReturnsListOfTodos() throws Exception {
        List<Todo> todos = Arrays.asList(new Todo(1L, "Todo 1", "Description 1"),
                new Todo(2L, "Todo 2", "Description 2"));

        when(todoRepository.findAll()).thenReturn(todos);

        mockMvc.perform(get("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Todo 1")))
                .andExpect(jsonPath("$[1].title", is("Todo 2")));

        verify(todoRepository, times(1)).findAll();
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    public void getTodoById_ReturnsTodo() throws Exception {
        Todo todo = new Todo(1L, "Todo 1", "Description 1");
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        mockMvc.perform(get("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Todo 1")));

        verify(todoRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    public void createTodo_ReturnsCreatedTodo() throws Exception {
        Todo todoToCreate = new Todo(null, "New Todo", "New Description");
        Todo createdTodo = new Todo(1L, "New Todo", "New Description");

        when(todoRepository.save(any(Todo.class))).thenReturn(createdTodo);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Todo\",\"description\":\"New Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Todo")));

        verify(todoRepository, times(1)).save(any(Todo.class));
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    public void updateTodo_ReturnsUpdatedTodo() throws Exception {
        Todo existingTodo = new Todo(1L, "Old Todo", "Old Description");
        Todo updatedTodo = new Todo(1L, "Updated Todo", "Updated Description");

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Todo\",\"description\":\"Updated Description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Todo")));

        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    public void deleteTodo_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isOk());

        verify(todoRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    public void toStringMethod() {
        Todo todo = new Todo(1L, "Todo 1", "Description 1");
        String expectedToString = "Todo(id=1, title=Todo 1, description=Description 1)";
        assertEquals(expectedToString, todo.toString());
    }

    @Test
    public void setIdMethod() {
        Todo todo = new Todo();
        todo.setId(1L);
        assertEquals(1L, todo.getId().longValue());
    }


}

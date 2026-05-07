package co.udes.castellanos.tareas.controller;

import co.udes.castellanos.tareas.entity.Tarea;
import co.udes.castellanos.tareas.exception.TareaNotFoundException;
import co.udes.castellanos.tareas.service.TareaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración de la capa web usando @WebMvcTest.
 * Levanta solo el contexto MVC (sin base de datos real) e inyecta
 * TareaService como @MockBean para aislar el controlador.
 */
@WebMvcTest(TareaController.class)
@DisplayName("TareaController — pruebas de capa web con MockMvc")
class TareaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TareaService service;

    @Autowired
    ObjectMapper objectMapper;

    // ── GET /api/tareas ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/tareas: lista vacía → retorna 200 con array vacío")
    void listarTodas_sinTareas_retorna200ConArrayVacio() throws Exception {
        when(service.listarTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/tareas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/tareas/{id} ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/tareas/{id}: tarea existe → retorna 200 con datos correctos")
    void buscarPorId_tareaExiste_retorna200ConTarea() throws Exception {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTitulo("Test");
        tarea.setCompletada(false);

        when(service.buscarPorId(1L)).thenReturn(tarea);

        mockMvc.perform(get("/api/tareas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titulo").value("Test"))
                .andExpect(jsonPath("$.completada").value(false));
    }

    @Test
    @DisplayName("GET /api/tareas/{id}: tarea no existe → retorna 404")
    void buscarPorId_tareaNoExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L))
                .thenThrow(new TareaNotFoundException("no encontrada"));

        mockMvc.perform(get("/api/tareas/99"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/tareas ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/tareas: datos válidos → retorna 201 con tarea creada")
    void crear_datosValidos_retorna201ConTareaCreada() throws Exception {
        Tarea entrada = new Tarea();
        entrada.setTitulo("Nueva tarea");

        Tarea guardada = new Tarea();
        guardada.setId(1L);
        guardada.setTitulo("Nueva tarea");

        when(service.crear(any(Tarea.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Nueva tarea"));
    }

    @Test
    @DisplayName("POST /api/tareas: título inválido → retorna 400")
    void crear_tituloInvalido_retorna400() throws Exception {
        Tarea entrada = new Tarea();
        entrada.setTitulo("  ");

        when(service.crear(any(Tarea.class)))
                .thenThrow(new IllegalArgumentException("El título no puede estar vacío"));

        mockMvc.perform(post("/api/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/tareas/{id}/completar ────────────────────────────────────

    @Test
    @DisplayName("PATCH /api/tareas/{id}/completar: tarea existe → retorna 200 con completada=true")
    void completar_tareaExiste_retorna200ConCompletadaTrue() throws Exception {
        Tarea completada = new Tarea();
        completada.setId(1L);
        completada.setTitulo("Tarea");
        completada.setCompletada(true);

        when(service.completar(1L)).thenReturn(completada);

        mockMvc.perform(patch("/api/tareas/1/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completada").value(true));
    }

    @Test
    @DisplayName("PATCH /api/tareas/{id}/completar: tarea no existe → retorna 404")
    void completar_tareaNoExiste_retorna404() throws Exception {
        when(service.completar(99L))
                .thenThrow(new TareaNotFoundException(99L));

        mockMvc.perform(patch("/api/tareas/99/completar"))
                .andExpect(status().isNotFound());
    }
}

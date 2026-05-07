package co.udes.castellanos.tareas.controller;

import co.udes.castellanos.tareas.entity.Tarea;
import co.udes.castellanos.tareas.exception.TareaNotFoundException;
import co.udes.castellanos.tareas.service.TareaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de tareas.
 * Expone los endpoints del recurso /api/tareas.
 */
@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    /**
     * GET /api/tareas — Retorna todas las tareas.
     */
    @GetMapping
    public ResponseEntity<List<Tarea>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    /**
     * GET /api/tareas/{id} — Retorna una tarea por id.
     * Retorna 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * POST /api/tareas — Crea una nueva tarea.
     * Retorna 201 Created con la tarea persistida.
     */
    @PostMapping
    public ResponseEntity<Tarea> crear(@RequestBody Tarea tarea) {
        Tarea creada = service.crear(tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * PATCH /api/tareas/{id}/completar — Marca una tarea como completada.
     * Retorna 200 con la tarea actualizada.
     */
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Tarea> completar(@PathVariable Long id) {
        return ResponseEntity.ok(service.completar(id));
    }

    /**
     * Manejador de excepciones local para TareaNotFoundException.
     * Retorna HTTP 404 con el mensaje de error.
     */
    @ExceptionHandler(TareaNotFoundException.class)
    public ResponseEntity<String> handleNotFound(TareaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de excepciones local para argumentos inválidos.
     * Retorna HTTP 400 con el mensaje de error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

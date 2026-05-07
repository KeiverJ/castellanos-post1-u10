package co.udes.castellanos.tareas.service;

import co.udes.castellanos.tareas.entity.Tarea;
import co.udes.castellanos.tareas.exception.TareaNotFoundException;
import co.udes.castellanos.tareas.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de tareas.
 * Aplica validaciones y delega la persistencia al repositorio.
 */
@Service
@Transactional
public class TareaService {

    private final TareaRepository repo;

    public TareaService(TareaRepository repo) {
        this.repo = repo;
    }

    /**
     * Crea y persiste una nueva tarea.
     * Valida que el título no sea nulo ni esté en blanco.
     *
     * @param tarea objeto con los datos de la nueva tarea
     * @return la tarea persistida con id asignado
     * @throws IllegalArgumentException si el título es nulo o vacío
     */
    public Tarea crear(Tarea tarea) {
        if (tarea.getTitulo() == null || tarea.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        return repo.save(tarea);
    }

    /**
     * Busca una tarea por su identificador único.
     *
     * @param id identificador de la tarea
     * @return la tarea encontrada
     * @throws TareaNotFoundException si no existe una tarea con ese id
     */
    @Transactional(readOnly = true)
    public Tarea buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new TareaNotFoundException(id));
    }

    /**
     * Marca una tarea existente como completada.
     *
     * @param id identificador de la tarea a completar
     * @return la tarea actualizada
     * @throws TareaNotFoundException si no existe una tarea con ese id
     */
    public Tarea completar(Long id) {
        Tarea tarea = buscarPorId(id);
        tarea.setCompletada(true);
        return repo.save(tarea);
    }

    /**
     * Retorna todas las tareas registradas.
     *
     * @return lista completa de tareas
     */
    @Transactional(readOnly = true)
    public List<Tarea> listarTodas() {
        return repo.findAll();
    }

    /**
     * Filtra tareas por estado de completitud.
     *
     * @param completada estado a filtrar
     * @return lista de tareas con el estado indicado
     */
    @Transactional(readOnly = true)
    public List<Tarea> listarPorEstado(boolean completada) {
        return repo.findByCompletada(completada);
    }
}

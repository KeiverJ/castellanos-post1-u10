package co.udes.castellanos.tareas.repository;

import co.udes.castellanos.tareas.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Tarea.
 * Extiende JpaRepository para heredar operaciones CRUD estándar.
 */
@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    /**
     * Busca tareas según su estado de completitud.
     *
     * @param completada true para tareas completadas, false para pendientes
     * @return lista de tareas que coinciden con el estado indicado
     */
    List<Tarea> findByCompletada(boolean completada);
}

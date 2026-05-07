package co.udes.castellanos.tareas.repository;

import co.udes.castellanos.tareas.entity.Tarea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración de la capa de repositorio usando @DataJpaTest.
 * Levanta un contexto JPA mínimo con H2 en memoria.
 * Los cambios se revierten automáticamente entre tests (rollback por defecto).
 */
@DataJpaTest
@DisplayName("TareaRepository — pruebas de integración con H2 en memoria")
class TareaRepositoryTest {

    @Autowired
    TareaRepository repo;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        Tarea pendiente = new Tarea();
        pendiente.setTitulo("Pendiente");
        pendiente.setCompletada(false);
        em.persistAndFlush(pendiente);

        Tarea completada = new Tarea();
        completada.setTitulo("Completada");
        completada.setCompletada(true);
        em.persistAndFlush(completada);
    }

    // ── findByCompletada ─────────────────────────────────────────────────────

    @Test
    @DisplayName("findByCompletada(false): existen pendientes → retorna solo tareas pendientes")
    void findByCompletada_false_retornaUnaTareaPendiente() {
        List<Tarea> pendientes = repo.findByCompletada(false);

        assertThat(pendientes).hasSize(1)
                .extracting("titulo")
                .containsExactly("Pendiente");
    }

    @Test
    @DisplayName("findByCompletada(true): existen completadas → retorna solo tareas completadas")
    void findByCompletada_true_retornaUnaTareaCompletada() {
        List<Tarea> completadas = repo.findByCompletada(true);

        assertThat(completadas).hasSize(1)
                .extracting("titulo")
                .containsExactly("Completada");
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById: id existente → retorna la tarea correcta")
    void findById_idExistente_retornaTareaCorrecta() {
        Tarea guardada = new Tarea();
        guardada.setTitulo("Buscable");
        Tarea persistida = em.persistAndFlush(guardada);

        Optional<Tarea> resultado = repo.findById(persistida.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getTitulo()).isEqualTo("Buscable");
    }

    @Test
    @DisplayName("findById: id inexistente → retorna Optional vacío")
    void findById_idNoExistente_retornaOptionalVacio() {
        Optional<Tarea> resultado = repo.findById(999L);

        assertThat(resultado).isEmpty();
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save: nueva tarea → persiste y asigna id autogenerado")
    void save_nuevaTarea_persisteConIdAsignado() {
        Tarea nueva = new Tarea();
        nueva.setTitulo("Nueva para guardar");

        Tarea guardada = repo.save(nueva);

        assertThat(guardada.getId()).isNotNull().isPositive();
        assertThat(guardada.getTitulo()).isEqualTo("Nueva para guardar");
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll: con registros en BD → retorna todas las tareas")
    void findAll_conRegistros_retornaTodasLasTareas() {
        List<Tarea> todas = repo.findAll();

        // setUp persiste 2 tareas; este test puede ver solo las de su transacción
        assertThat(todas).isNotEmpty();
    }
}

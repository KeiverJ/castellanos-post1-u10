package co.udes.castellanos.tareas.service;

import co.udes.castellanos.tareas.entity.Tarea;
import co.udes.castellanos.tareas.exception.TareaNotFoundException;
import co.udes.castellanos.tareas.repository.TareaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de TareaService usando Mockito.
 * Aísla la capa de servicio mediante mocks del repositorio.
 * Convención de nombres: método_condición_resultadoEsperado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TareaService — pruebas unitarias con Mockito")
class TareaServiceTest {

    @Mock
    TareaRepository repo;

    @InjectMocks
    TareaService service;

    // ── Pruebas de crear() ───────────────────────────────────────────────────

    @Test
    @DisplayName("crear: título válido → guarda y retorna la tarea")
    void crear_conTituloValido_guardaYRetorna() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("Estudiar JUnit");

        when(repo.save(any(Tarea.class))).thenReturn(tarea);

        Tarea resultado = service.crear(tarea);

        assertThat(resultado.getTitulo()).isEqualTo("Estudiar JUnit");
        verify(repo).save(tarea);
    }

    @Test
    @DisplayName("crear: título en blanco → lanza IllegalArgumentException sin invocar repo")
    void crear_conTituloVacio_lanzaIllegalArgumentException() {
        Tarea tarea = new Tarea();
        tarea.setTitulo("   ");

        assertThrows(IllegalArgumentException.class, () -> service.crear(tarea));

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("crear: título nulo → lanza IllegalArgumentException sin invocar repo")
    void crear_conTituloNulo_lanzaIllegalArgumentException() {
        Tarea tarea = new Tarea();
        tarea.setTitulo(null);

        assertThrows(IllegalArgumentException.class, () -> service.crear(tarea));

        verify(repo, never()).save(any());
    }

    // ── Pruebas de buscarPorId() ─────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: id existente → retorna la tarea correcta")
    void buscarPorId_idExistente_retornaTarea() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTitulo("Tarea existente");

        when(repo.findById(1L)).thenReturn(Optional.of(tarea));

        Tarea resultado = service.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Tarea existente");
        verify(repo).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId: id inexistente → lanza TareaNotFoundException")
    void buscarPorId_idNoExistente_lanzaTareaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TareaNotFoundException.class, () -> service.buscarPorId(99L));

        verify(repo).findById(99L);
    }

    // ── Pruebas de completar() ───────────────────────────────────────────────

    @Test
    @DisplayName("completar: tarea existente → marca completada y persiste")
    void completar_tareaExistente_marcaCompletadaYGuarda() {
        Tarea tarea = new Tarea();
        tarea.setId(1L);
        tarea.setTitulo("Tarea a completar");
        tarea.setCompletada(false);

        when(repo.findById(1L)).thenReturn(Optional.of(tarea));
        when(repo.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        Tarea resultado = service.completar(1L);

        assertThat(resultado.isCompletada()).isTrue();
        verify(repo).save(tarea);
    }

    @Test
    @DisplayName("completar: id inexistente → lanza TareaNotFoundException sin guardar")
    void completar_idNoExistente_lanzaTareaNotFoundException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TareaNotFoundException.class, () -> service.completar(99L));

        verify(repo, never()).save(any());
    }

    // ── Pruebas de listarTodas() y listarPorEstado() ─────────────────────────

    @Test
    @DisplayName("listarTodas: repositorio con registros → retorna lista completa")
    void listarTodas_conRegistros_retornaListaCompleta() {
        Tarea t1 = new Tarea("Tarea 1", null);
        Tarea t2 = new Tarea("Tarea 2", null);

        when(repo.findAll()).thenReturn(List.of(t1, t2));

        List<Tarea> resultado = service.listarTodas();

        assertThat(resultado).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    @DisplayName("listarPorEstado: filtro pendientes → retorna solo tareas no completadas")
    void listarPorEstado_pendientes_retornaTareasPendientes() {
        Tarea pendiente = new Tarea("Pendiente", null);
        pendiente.setCompletada(false);

        when(repo.findByCompletada(false)).thenReturn(List.of(pendiente));

        List<Tarea> resultado = service.listarPorEstado(false);

        assertThat(resultado).hasSize(1)
                .extracting("titulo")
                .containsExactly("Pendiente");
        verify(repo).findByCompletada(false);
    }
}

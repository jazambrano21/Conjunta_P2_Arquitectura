package ec.edu.espe.andesfin.services;

import ec.edu.espe.andesfin.dto.UsuarioDTO;
import ec.edu.espe.andesfin.entity.Usuario;
import ec.edu.espe.andesfin.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Service Pattern: logica de negocio de usuarios. CRUD completo; el controller
 * solo expone los GET requeridos.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO findById(UUID id) {
        return usuarioRepository.findById(id).map(this::toDTO).orElseThrow(() -> notFound(id));
    }

    public UsuarioDTO create(UsuarioDTO dto) {
        if (dto.getEmail() != null && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con el email " + dto.getEmail());
        }
        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .capitalDisponible(dto.getCapitalDisponible())
                .build();
        return toDTO(usuarioRepository.save(usuario));
    }

    public UsuarioDTO update(UUID id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> notFound(id));
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setCapitalDisponible(dto.getCapitalDisponible());
        return toDTO(usuarioRepository.save(usuario));
    }

    public void delete(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw notFound(id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO toDTO(Usuario u) {
        return UsuarioDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .capitalDisponible(u.getCapitalDisponible())
                .build();
    }

    private ResponseStatusException notFound(UUID id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + id);
    }
}

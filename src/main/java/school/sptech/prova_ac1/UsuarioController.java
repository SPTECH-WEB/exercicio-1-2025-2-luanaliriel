package school.sptech.prova_ac1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        try {

            if (usuarioRepository.existsByemail(usuario.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            if (usuarioRepository.existsBycpf(usuario.getCpf())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();

            if (usuarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(usuarios);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);

            if (usuario.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(usuario.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            if (usuarioRepository.existsById(id)) {
                usuarioRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(
            @RequestParam LocalDate nascimento) {

        try {
            List<Usuario> usuarios = usuarioRepository.findBydataNascimentoAfter(nascimento);

            if (usuarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.status(HttpStatus.OK).body(usuarios);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {

        try {
            if (!usuarioRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Optional<Usuario> usuarioComEmail = usuarioRepository.findAll().stream()
                    .filter(u -> u.getEmail().equals(usuario.getEmail()) && !u.getId().equals(id))
                    .findFirst();

            if (usuarioComEmail.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            Optional<Usuario> usuarioComCpf = usuarioRepository.findAll().stream()
                    .filter(u -> u.getCpf().equals(usuario.getCpf()) && !u.getId().equals(id))
                    .findFirst();

            if (usuarioComCpf.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            usuario.setId(id);
            Usuario usuarioAtualizado = usuarioRepository.save(usuario);
            return ResponseEntity.status(HttpStatus.OK).body(usuarioAtualizado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

package com.taelmeireles.minhasfinancas.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import com.taelmeireles.minhasfinancas.model.Usuario;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioRepositoryTest {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testExistsByEmail_DeveVerificarAExistenciaDeUmEmail() {
        Usuario usuario = Usuario.builder()
            .nome("usuario")
            .email("usuario@gmail.com")
            .build();

        this.entityManager.persist(usuario);

        boolean isEmailExist = this.usuarioRepository.existsByEmail(usuario.getEmail());

        Assertions.assertTrue(isEmailExist);
    }

    @Test
    void testExistsByEmail_DeveRetornarFalso_QuandoNaoHouverUsuarioCadastradoComEmail() {

        boolean result = this.usuarioRepository.existsByEmail("usuario@gmail.com");
        
        Assertions.assertFalse(result);

    }

}

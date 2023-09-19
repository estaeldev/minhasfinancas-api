package com.taelmeireles.minhasfinancas.repository;

import java.time.LocalDate;

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
        Usuario usuario = new Usuario();
        usuario.setId(null);
        usuario.setDataCadastro(LocalDate.now());
        usuario.setEmail("teste1@gmail.com");
        usuario.setSenha("senha");
        this.entityManager.persist(usuario);
        
        boolean isEmailExist = this.usuarioRepository.existsByEmail(usuario.getEmail());

        Assertions.assertTrue(isEmailExist);
    }

    @Test
    void testExistsByEmail_DeveRetornarFalso_QuandoNaoHouverUsuarioCadastradoComEmail() {
        this.entityManager.clear();
        
        boolean result = this.usuarioRepository.existsByEmail("naoexiste@gmail.com");
        
        Assertions.assertFalse(result);

    }

}

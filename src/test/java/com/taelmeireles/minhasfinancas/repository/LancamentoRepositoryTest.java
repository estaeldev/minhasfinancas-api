package com.taelmeireles.minhasfinancas.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.util.LancamentoUtil;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void testSave_DeveSalvarUmLancamento_QuandoLancamentoEstiverOk() {
        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento = this.repository.save(lancamento);

        Assertions.assertNotNull(lancamento.getId());
    }

    @Test
    void testDelete_DeveExcluirLancamento_QuandoIdForValido() {
        this.entityManager.clear();

        Lancamento lancamento = LancamentoUtil.getLancamento();
        this.entityManager.persist(lancamento);

        lancamento = this.entityManager.find(Lancamento.class, lancamento.getId());

        this.repository.delete(lancamento);

        Lancamento lancamentoExcluido = this.entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertNull(lancamentoExcluido);

    }

    @Test
    void testSave_DeveAtualizarLancamento_QuandoIdForValido() {
        this.entityManager.clear();

        Lancamento lancamento = LancamentoUtil.getLancamento();
        lancamento = this.entityManager.persist(lancamento);

        lancamento.setAno(2022);
        lancamento.setValor(BigDecimal.valueOf(20));
        lancamento.setDescricao("Teste");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        this.repository.save(lancamento);

        Lancamento lancamentoAtualizado = this.entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertNotNull(lancamentoAtualizado.getId());
        Assertions.assertEquals(lancamento.getAno(), lancamentoAtualizado.getAno());
        Assertions.assertEquals(lancamento.getValor(), lancamentoAtualizado.getValor());
        Assertions.assertEquals(lancamento.getDescricao(), lancamentoAtualizado.getDescricao());
        Assertions.assertEquals(lancamento.getStatus(), lancamentoAtualizado.getStatus());
        
    }

    @Test
    void testFindByid_DeveBuscarLancamentoPorId_QuandoIdForValido() {
        this.entityManager.clear();

        Lancamento lancamento = LancamentoUtil.getLancamento();
        this.entityManager.persist(lancamento);

        Optional<Lancamento> lancamentoOpt = this.repository.findById(lancamento.getId());

        Assertions.assertNotNull(lancamentoOpt.get());
        Assertions.assertNotNull(lancamentoOpt.get().getId());

    }

    @Test
    void testObterSaldoPorTipoLancamentoEUsuario() {
        this.entityManager.clear();
        
        Usuario usuario = new Usuario();
        usuario.setDataCadastro(LocalDate.now());
        usuario.setEmail("teste@gmail.com");
        usuario.setNome("Teste");
        usuario.setSenha("senha");
        this.entityManager.persist(usuario);
        
        Lancamento lancamentoDespesa = new Lancamento();
        lancamentoDespesa.setId(null);
        lancamentoDespesa.setValor(BigDecimal.valueOf(500));
        lancamentoDespesa.setAno(2023);
        lancamentoDespesa.setDescricao("Despesa Qualquer");
        lancamentoDespesa.setMes(1);
        lancamentoDespesa.setStatus(StatusLancamento.EFETIVADO);
        lancamentoDespesa.setTipo(TipoLancamento.DESPESA);
        lancamentoDespesa.setUsuario(usuario);

        Lancamento lancamentoReceita = new Lancamento();
        lancamentoReceita.setId(null);
        lancamentoReceita.setValor(BigDecimal.valueOf(1000));
        lancamentoReceita.setAno(2023);
        lancamentoReceita.setDescricao("Receita Qualquer");
        lancamentoReceita.setMes(2);
        lancamentoReceita.setStatus(StatusLancamento.EFETIVADO);
        lancamentoReceita.setTipo(TipoLancamento.RECEITA);
        lancamentoReceita.setUsuario(usuario);

        Lancamento lancamentoReceita2 = new Lancamento();
        lancamentoReceita2.setId(null);
        lancamentoReceita2.setValor(BigDecimal.valueOf(1000));
        lancamentoReceita2.setAno(2023);
        lancamentoReceita2.setDescricao("Receita Qualquer 2");
        lancamentoReceita2.setMes(2);
        lancamentoReceita2.setStatus(StatusLancamento.EFETIVADO);
        lancamentoReceita2.setTipo(TipoLancamento.RECEITA);
        lancamentoReceita2.setUsuario(usuario);
        
        this.entityManager.persist(lancamentoDespesa);
        this.entityManager.persist(lancamentoReceita);
        this.entityManager.persist(lancamentoReceita2);

        BigDecimal saldoDespesa = this.repository.obterSaldoPorTipoLancamentoEUsuario(usuario.getId(), TipoLancamento.DESPESA);
        BigDecimal saldoReceita = this.repository.obterSaldoPorTipoLancamentoEUsuario(usuario.getId(), TipoLancamento.RECEITA);
        BigDecimal saldoFinal = saldoReceita.subtract(saldoDespesa);

        Assertions.assertEquals(500, saldoDespesa.intValue());
        Assertions.assertEquals(2000, saldoReceita.intValue());
        Assertions.assertEquals(1500, saldoFinal.intValue());
        
    }

}

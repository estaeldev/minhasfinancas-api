package com.taelmeireles.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taelmeireles.minhasfinancas.dto.LancamentoDto;
import com.taelmeireles.minhasfinancas.enums.StatusLancamento;
import com.taelmeireles.minhasfinancas.enums.TipoLancamento;
import com.taelmeireles.minhasfinancas.exception.RegraNegocioException;
import com.taelmeireles.minhasfinancas.mapper.LancamentoMapper;
import com.taelmeireles.minhasfinancas.model.Lancamento;
import com.taelmeireles.minhasfinancas.model.Usuario;
import com.taelmeireles.minhasfinancas.repository.LancamentoRepository;
import com.taelmeireles.minhasfinancas.service.LancamentoService;
import com.taelmeireles.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LancamentoServiceImpl implements LancamentoService {

    private final LancamentoRepository repository;
    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public LancamentoDto salvar(LancamentoDto dto) {

        if(Objects.isNull(dto.getId()) && Objects.nonNull(dto.getUsuarioId())) {

            Usuario usuarioEncontrado = this.usuarioService.findById(dto.getUsuarioId());

            if(Objects.nonNull(usuarioEncontrado)) {
                Lancamento lancamento = LancamentoMapper.fromDtoToEntity(dto, usuarioEncontrado);
                lancamento.setStatus(StatusLancamento.PENDENTE);
                lancamento = this.repository.save(lancamento);
                return LancamentoMapper.fromEntityToDto(lancamento);
            }
            
            throw new RegraNegocioException("Usuario não encontrado pelo 'ID' informado.");

        }

        throw new RegraNegocioException("Lançamento deve possuir 'ID' ou 'ID_USUARIO' deve ser informado.");

    }   

    @Override
    @Transactional
    public LancamentoDto atualizar(LancamentoDto dto) {
        Objects.requireNonNull(dto.getId());
        Lancamento lancamento = LancamentoMapper.fromDtoToEntity(dto, this.usuarioService.findById(dto.getUsuarioId()));
        return LancamentoMapper.fromEntityToDto(this.repository.save(lancamento));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LancamentoDto> buscar(LancamentoDto dto) {

        Usuario usuario = this.usuarioService.findById(dto.getUsuarioId());

        Lancamento lancamentoFiltro = LancamentoMapper.fromDtoToEntity(dto, usuario);

        Example<Lancamento> example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
            .withIgnoreCase()
            .withStringMatcher(StringMatcher.CONTAINING));

        List<Lancamento> lancamentoList = this.repository.findAll(example);
        return lancamentoList.stream().map(LancamentoMapper::fromEntityToDto).toList();

    }

    @Override
    @Transactional
    public void atualizarStatus(UUID lancamentoId, String status) {
        StatusLancamento statusSelecionado = StatusLancamento.valueOf(status);
        if(Objects.isNull(statusSelecionado)) {
            throw new RegraNegocioException("Não foi possível atualizar o status do lançamento.");
        }
        
        LancamentoDto dto = buscarPorId(lancamentoId);
        dto.setStatus(statusSelecionado);
        atualizar(dto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LancamentoDto buscarPorId(UUID lancamentoId) {
        Optional<Lancamento> lancamentoOpt = this.repository.findById(lancamentoId);
        return lancamentoOpt.map(LancamentoMapper::fromEntityToDto)
            .orElseThrow(() -> new RegraNegocioException("Lançamento não encontrado pelo 'ID' informado."));
    }

    @Override
    @Transactional
    public void deletar(UUID lancamentoId) {
        LancamentoDto lancamentoDto = buscarPorId(lancamentoId);
        Objects.requireNonNull(lancamentoDto);
        this.repository.delete(LancamentoMapper.fromDtoToEntity(lancamentoDto, null));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(UUID usuarioId) {
        BigDecimal receitas = this.repository.obterSaldoPorTipoLancamentoEUsuaririo(usuarioId, TipoLancamento.RECEITA);
        BigDecimal despesas = this.repository.obterSaldoPorTipoLancamentoEUsuaririo(usuarioId, TipoLancamento.DESPESA);

        if(Objects.isNull(receitas)) {
            receitas = BigDecimal.ZERO;
        }

        if(Objects.isNull(despesas)) {
            despesas = BigDecimal.ZERO;
        }
        
        return receitas.subtract(despesas);

    }
    
}

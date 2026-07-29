package com.joaovpg.economize.conta.application;

import com.joaovpg.economize.conta.ContaFinanceira;
import com.joaovpg.economize.conta.ContaFinanceiraRepository;
import com.joaovpg.economize.conta.SituacaoConta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ListarContas {
    private final ContaFinanceiraRepository contaRepository;

    public ListarContas(ContaFinanceiraRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Transactional
    public List<ContaResultado> executar(UUID usuarioId) {
        return resultados(contaRepository.listarDoUsuario(usuarioId));
    }

    public List<ContaResultado> executar(UUID usuarioId, SituacaoConta situacao) {
        return resultados(contaRepository.listarDoUsuario(usuarioId, situacao));
    }

    private List<ContaResultado> resultados(List<ContaFinanceira> contas) {
        return contas.stream()
                .map(ContaValidation::resultado)
                .toList();
    }
}

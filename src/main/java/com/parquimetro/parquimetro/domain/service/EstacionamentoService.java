package com.parquimetro.parquimetro.domain.service;

import com.parquimetro.parquimetro.domain.entity.Estacionamento;
import com.parquimetro.parquimetro.infra.repository.EstacionamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EstacionamentoService {

    private static final double VALOR_POR_HORA = 5.0;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Transactional
    public Estacionamento registrarEntrada(String placaVeiculo) {
        verificaSeVeiculoJaEntrou(placaVeiculo);

        Estacionamento estacionamento = new Estacionamento();
        estacionamento.setPlacaVeiculo(placaVeiculo);
        estacionamento.setHoraEntrada(LocalDateTime.now());
        return estacionamentoRepository.save(estacionamento);
    }

    private void verificaSeVeiculoJaEntrou(String placaVeiculo){
        if (estacionamentoRepository.findByHoraSaidaIsNull().stream()
                .anyMatch(e -> e.getPlacaVeiculo().equals(placaVeiculo))) {
            throw new IllegalStateException("ERR001: Veiculo com placa " + placaVeiculo + " ja esta no estacionamento");
        }
    }

    @Transactional
    public Optional<Estacionamento> registrarSaida(UUID id) {
        verificaSeVeiculoJaSaiu(id);
        Optional<Estacionamento> estacionamentoOpt = estacionamentoRepository.findById(id);

        return estacionamentoOpt.map(estacionamento -> {
            estacionamento.setHoraSaida(LocalDateTime.now());
            estacionamento.setValorAPagar(calcularValor(estacionamento.getHoraEntrada(), estacionamento.getHoraSaida()));
            estacionamentoRepository.save(estacionamento);
            return estacionamento;
        });
    }

    private void verificaSeVeiculoJaSaiu(UUID id){
        Optional<Estacionamento> carro = estacionamentoRepository.findById(id);

        if (carro.isPresent() && carro.get().getHoraSaida() != null) {
            throw new IllegalStateException("ERR002: Veiculo com id " + id + " ja saiu do estacionamento");
        }
    }

    private double calcularValor(LocalDateTime horaEntrada, LocalDateTime horaSaida) {
        long horas = Duration.between(horaEntrada, horaSaida).toHours();
        return horas == 0 ? VALOR_POR_HORA : horas * VALOR_POR_HORA;
    }
}

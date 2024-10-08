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
        verificarSeVeiculoJaEntrou(placaVeiculo);

        Estacionamento estacionamento = new Estacionamento();
        estacionamento.setPlacaVeiculo(placaVeiculo);
        estacionamento.setHoraEntrada(LocalDateTime.now());
        return estacionamentoRepository.save(estacionamento);
    }

    private void verificarSeVeiculoJaEntrou(String placaVeiculo){
        if (estacionamentoRepository.findByHoraSaidaIsNull().stream()
                .anyMatch(e -> e.getPlacaVeiculo().equals(placaVeiculo))) {
            throw new IllegalStateException("ERR001: Veiculo com placa " + placaVeiculo + " ja esta no estacionamento");
        }
    }

    @Transactional
    public Estacionamento registrarSaida(UUID id) {
        Estacionamento estacionamento = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERR001: Veículo com id " + id + " não encontrado"));

        verificarSeVeiculoJaSaiu(id);

        estacionamento.setHoraSaida(LocalDateTime.now());
        estacionamento.setValorAPagar(calcularValor(estacionamento.getHoraEntrada(), estacionamento.getHoraSaida()));

        return estacionamentoRepository.save(estacionamento);
    }

    private void verificarSeVeiculoJaSaiu(UUID id) {
        estacionamentoRepository.findById(id)
                .filter(veiculo -> veiculo.getHoraSaida() != null)
                .ifPresent(veiculo -> {
                    throw new IllegalStateException("ERR002: Veículo com id " + id + " já saiu do estacionamento");
                });
    }


    private double calcularValor(LocalDateTime horaEntrada, LocalDateTime horaSaida) {
        long horas = Duration.between(horaEntrada, horaSaida).toHours();
        return horas == 0 ? VALOR_POR_HORA : horas * VALOR_POR_HORA;
    }
}

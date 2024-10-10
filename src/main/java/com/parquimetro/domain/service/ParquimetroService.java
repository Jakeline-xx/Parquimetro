package com.parquimetro.parquimetro.domain.service;

import com.parquimetro.parquimetro.domain.entity.Parquimetro;
import com.parquimetro.parquimetro.infra.repository.ParquimetroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ParquimetroService {

    private static final double VALOR_POR_HORA = 5.0;

    private final ParquimetroRepository parquimetroRepository;

    @Autowired
    public ParquimetroService(ParquimetroRepository parquimetroRepository) {
        this.parquimetroRepository = parquimetroRepository;
    }

    @Transactional
    public Parquimetro registrarEntrada(String placaVeiculo) {
        verificarSeVeiculoJaEntrou(placaVeiculo);

        Parquimetro parquimetro = new Parquimetro();
        parquimetro.setPlacaVeiculo(placaVeiculo);
        parquimetro.setHoraEntrada(LocalDateTime.now());
        return parquimetroRepository.save(parquimetro);
    }

    private void verificarSeVeiculoJaEntrou(String placaVeiculo){
        if (parquimetroRepository.findByHoraSaidaIsNull().stream()
                .anyMatch(e -> e.getPlacaVeiculo().equals(placaVeiculo))) {
            throw new IllegalStateException("ERR001: Veiculo com placa " + placaVeiculo + " ja esta no parquimetro");
        }
    }

    @Transactional
    public Parquimetro registrarSaida(UUID id) {
        Parquimetro parquimetro = parquimetroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ERR002: Veículo com id " + id + " não encontrado"));

        verificarSeVeiculoJaSaiu(id);

        parquimetro.setHoraSaida(LocalDateTime.now());
        parquimetro.setValorAPagar(calcularValor(parquimetro.getHoraEntrada(), parquimetro.getHoraSaida()));

        return parquimetroRepository.save(parquimetro);
    }

    private void verificarSeVeiculoJaSaiu(UUID id) {
        parquimetroRepository.findById(id)
                .filter(veiculo -> veiculo.getHoraSaida() != null)
                .ifPresent(veiculo -> {
                    throw new IllegalStateException("ERR003: Veículo com id " + id + " já saiu do parquimetro");
                });
    }


    private double calcularValor(LocalDateTime horaEntrada, LocalDateTime horaSaida) {
        long horas = Duration.between(horaEntrada, horaSaida).toHours();
        return horas == 0 ? VALOR_POR_HORA : horas * VALOR_POR_HORA;
    }
}

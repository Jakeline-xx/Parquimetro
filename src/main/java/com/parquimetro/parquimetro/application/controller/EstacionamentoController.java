package com.parquimetro.parquimetro.application.controller;

import com.parquimetro.parquimetro.domain.entity.Estacionamento;
import com.parquimetro.parquimetro.domain.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/parquimetro")
public class EstacionamentoController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @PostMapping("/entrada")
    public ResponseEntity<Estacionamento> registrarEntrada(@RequestParam String placaVeiculo) {
        try {
            Estacionamento estacionamento = estacionamentoService.registrarEntrada(placaVeiculo);
            return ResponseEntity.ok(estacionamento);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/saida/{id}")
    public ResponseEntity<Estacionamento> registrarSaida(@PathVariable UUID id) {
        Optional<Estacionamento> estacionamentoOpt = estacionamentoService.registrarSaida(id);

        return estacionamentoOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<String> gerarTicket(@PathVariable UUID id) {
        Optional<Estacionamento> estacionamentoOpt = estacionamentoService.registrarSaida(id);

        if (estacionamentoOpt.isPresent()) {
            Estacionamento est = estacionamentoOpt.get();
            String ticket = criarTicket(est);
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String criarTicket(Estacionamento estacionamento) {
        return String.format("""
            ********** TICKET DE ESTACIONAMENTO **********
            Placa do Veículo: %s
            Hora de Entrada: %s
            Hora de Saída: %s
            Valor Total: R$ %.2f
            ********************************************
            """,
                estacionamento.getPlacaVeiculo(),
                estacionamento.getHoraEntrada(),
                estacionamento.getHoraSaida(),
                estacionamento.getValorPago());
    }
}


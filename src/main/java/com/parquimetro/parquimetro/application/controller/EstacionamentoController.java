package com.parquimetro.parquimetro.application.controller;

import com.parquimetro.parquimetro.domain.entity.Estacionamento;
import com.parquimetro.parquimetro.domain.service.EstacionamentoService;
import com.parquimetro.parquimetro.infra.repository.EstacionamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/parquimetro")
public class EstacionamentoController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(@RequestParam String placaVeiculo) {
        try {
            Estacionamento estacionamento = estacionamentoService.registrarEntrada(placaVeiculo);
            return ResponseEntity.ok(estacionamento);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/saida/{id}")
    public ResponseEntity<?> registrarSaida(@PathVariable UUID id) {
        try {
            Estacionamento estacionamento = estacionamentoService.registrarSaida(id);
            return ResponseEntity.ok(estacionamento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Retorna 404 com a mensagem
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 com a mensagem
        }
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<String> gerarTicket(@PathVariable UUID id) {
        Optional<Estacionamento> estacionamentoOpt = estacionamentoRepository.findById(id);

        if (estacionamentoOpt.isPresent()) {
            Estacionamento est = estacionamentoOpt.get();

            if (est.getHoraSaida() != null) {
                String ticket = criarTicket(est);
                return ResponseEntity.ok(ticket);
            } else {
                return ResponseEntity.badRequest().body("A saída ainda não foi registrada para o veículo com id " + id);
            }
        } else {
            return ResponseEntity.status(404).body("Veículo com id " + id + " não encontrado.");
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
                estacionamento.getValorAPagar());
    }
}


package com.parquimetro.parquimetro.application.controller;

import com.parquimetro.parquimetro.domain.entity.Parquimetro;
import com.parquimetro.parquimetro.domain.service.ParquimetroService;
import com.parquimetro.parquimetro.infra.repository.ParquimetroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/parquimetro")
public class ParquimetroController {

    private final ParquimetroService parquimetroService;
    private final ParquimetroRepository parquimetroRepository;

    @Autowired
    public ParquimetroController(ParquimetroRepository parquimetroRepository, ParquimetroService parquimetroService) {
        this.parquimetroRepository = parquimetroRepository;
        this.parquimetroService = parquimetroService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(@RequestParam String placaVeiculo) {
        try {
            Parquimetro parquimetro = parquimetroService.registrarEntrada(placaVeiculo);
            return ResponseEntity.ok(parquimetro);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/saida/{id}")
    public ResponseEntity<?> registrarSaida(@PathVariable UUID id) {
        try {
            Parquimetro parquimetro = parquimetroService.registrarSaida(id);
            return ResponseEntity.ok(parquimetro);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Retorna 404 com a mensagem
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna 400 com a mensagem
        }
    }

    @GetMapping("/ticket/{id}")
    public ResponseEntity<String> gerarTicket(@PathVariable UUID id) {
        Optional<Parquimetro> parquimetroOpt = parquimetroRepository.findById(id);

        if (parquimetroOpt.isPresent()) {
            Parquimetro est = parquimetroOpt.get();

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

    private String criarTicket(Parquimetro parquimetro) {
        return String.format("""
            ---------- COMPROVANTE DE ESTACIONAMENTO ----------
            Placa do Veículo: %s
            Hora de Entrada: %s
            Hora de Saída: %s
            Valor Total: R$ %.2f
            --------------------------------------------
            """,
                parquimetro.getPlacaVeiculo(),
                parquimetro.getHoraEntrada(),
                parquimetro.getHoraSaida(),
                parquimetro.getValorAPagar());
    }
}


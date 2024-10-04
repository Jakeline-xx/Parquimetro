package com.parquimetro.parquimetro.infra.repository;

import com.parquimetro.parquimetro.domain.entity.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {

    // Busca por veículos que ainda estão no estacionamento
    List<Estacionamento> findByHoraSaidaIsNull();

    Optional<Estacionamento> findById(UUID id);
}


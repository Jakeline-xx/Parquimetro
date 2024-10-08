package com.parquimetro.parquimetro.infra.repository;

import com.parquimetro.parquimetro.domain.entity.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {

    List<Estacionamento> findByHoraSaidaIsNull();

    Optional<Estacionamento> findById(UUID id);

}


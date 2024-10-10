package com.parquimetro.parquimetro.infra.repository;

import com.parquimetro.parquimetro.domain.entity.Parquimetro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParquimetroRepository extends JpaRepository<Parquimetro, Long> {

    List<Parquimetro> findByHoraSaidaIsNull();

    Optional<Parquimetro> findById(UUID id);

}


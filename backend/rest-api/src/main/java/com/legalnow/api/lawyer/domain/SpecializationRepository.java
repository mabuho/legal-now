package com.legalnow.api.lawyer.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Short> {

    Optional<Specialization> findByCode(String code);
}

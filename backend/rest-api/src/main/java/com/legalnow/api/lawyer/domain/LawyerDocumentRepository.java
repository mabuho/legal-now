package com.legalnow.api.lawyer.domain;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawyerDocumentRepository extends JpaRepository<LawyerDocument, UUID> {

    List<LawyerDocument> findByLawyerId(UUID lawyerId);
}

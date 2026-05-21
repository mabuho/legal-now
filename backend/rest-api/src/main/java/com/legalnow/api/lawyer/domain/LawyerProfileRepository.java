package com.legalnow.api.lawyer.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, UUID> {

    @Query("""
        SELECT DISTINCT lp FROM LawyerProfile lp
        LEFT JOIN FETCH lp.user
        LEFT JOIN FETCH lp.specializations
        WHERE lp.userId = :userId
        """)
    Optional<LawyerProfile> findByIdWithDetails(@Param("userId") UUID userId);

    @Query(
        value = """
            SELECT DISTINCT lp FROM LawyerProfile lp
            LEFT JOIN FETCH lp.user
            LEFT JOIN FETCH lp.specializations
            WHERE lp.verifiedAt IS NOT NULL
            """,
        countQuery = "SELECT COUNT(lp) FROM LawyerProfile lp WHERE lp.verifiedAt IS NOT NULL"
    )
    Page<LawyerProfile> findAllVerified(Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT lp FROM LawyerProfile lp
            LEFT JOIN FETCH lp.user
            LEFT JOIN FETCH lp.specializations s
            WHERE lp.verifiedAt IS NOT NULL AND s.code = :code
            """,
        countQuery = """
            SELECT COUNT(DISTINCT lp) FROM LawyerProfile lp
            JOIN lp.specializations s
            WHERE lp.verifiedAt IS NOT NULL AND s.code = :code
            """
    )
    Page<LawyerProfile> findAllVerifiedBySpecializationCode(@Param("code") String code, Pageable pageable);
}

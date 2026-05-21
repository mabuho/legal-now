package com.legalnow.api.sep;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.lawyer.domain.LawyerProfile;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;

@Service
public class SepValidationService {

    private final SepCedulaClient sepCedulaClient;
    private final LawyerProfileRepository lawyerProfileRepository;

    public SepValidationService(
        SepCedulaClient sepCedulaClient,
        LawyerProfileRepository lawyerProfileRepository
    ) {
        this.sepCedulaClient = sepCedulaClient;
        this.lawyerProfileRepository = lawyerProfileRepository;
    }

    @Transactional(readOnly = true)
    public SepValidationResult validateLawyer(UUID lawyerId) {
        LawyerProfile profile = lawyerProfileRepository.findById(lawyerId)
            .orElseThrow(() -> new NotFoundException("Lawyer profile not found: " + lawyerId));

        String barId = profile.getBarId();

        if (barId == null || barId.isBlank()) {
            return new SepValidationResult(false, Collections.emptyList());
        }

        List<SepProfesionistaDto> matches = sepCedulaClient.queryByCedula(barId);
        return new SepValidationResult(!matches.isEmpty(), matches);
    }
}

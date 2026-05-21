package com.legalnow.api.lawyer;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.legalnow.api.lawyer.dto.SpecializationResponse;

@RestController
@RequestMapping("/api/v1/specializations")
public class SpecializationController {

    private final LawyerService lawyerService;

    public SpecializationController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }

    @GetMapping
    public List<SpecializationResponse> list() {
        return lawyerService.listSpecializations();
    }
}

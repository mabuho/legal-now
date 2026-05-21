package com.legalnow.api.lawyer;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.legalnow.api.common.SecurityUtils;
import com.legalnow.api.lawyer.dto.DocumentResponse;

@RestController
@RequestMapping("/api/v1/lawyers/me/documents")
public class LawyerDocumentController {

    private final LawyerDocumentService documentService;

    public LawyerDocumentController(LawyerDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("doc_type") String docType
    ) {
        UUID lawyerId = SecurityUtils.currentUserId();
        DocumentResponse response = documentService.upload(lawyerId, file, docType);
        return ResponseEntity.created(URI.create("/api/v1/lawyers/me/documents/" + response.id())).body(response);
    }

    @GetMapping
    public List<DocumentResponse> list() {
        UUID lawyerId = SecurityUtils.currentUserId();
        return documentService.listByLawyer(lawyerId);
    }
}

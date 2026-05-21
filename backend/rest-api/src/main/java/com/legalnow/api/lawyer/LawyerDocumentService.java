package com.legalnow.api.lawyer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.legalnow.api.common.exception.BadRequestException;
import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.lawyer.domain.LawyerDocument;
import com.legalnow.api.lawyer.domain.LawyerDocumentRepository;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.lawyer.dto.DocumentResponse;

@Service
public class LawyerDocumentService {

    private static final Set<String> ALLOWED_DOC_TYPES = Set.of("cedula", "titulo", "otro");

    private final LawyerDocumentRepository documentRepository;
    private final LawyerProfileRepository lawyerProfileRepository;

    @Value("${app.uploads.dir:/uploads}")
    private String uploadsDir;

    public LawyerDocumentService(
        LawyerDocumentRepository documentRepository,
        LawyerProfileRepository lawyerProfileRepository
    ) {
        this.documentRepository = documentRepository;
        this.lawyerProfileRepository = lawyerProfileRepository;
    }

    @Transactional
    public DocumentResponse upload(UUID lawyerId, MultipartFile file, String docType) {
        if (!lawyerProfileRepository.existsById(lawyerId)) {
            throw new NotFoundException("Lawyer profile not found");
        }
        if (!ALLOWED_DOC_TYPES.contains(docType)) {
            throw new BadRequestException("Invalid doc_type. Allowed: cedula, titulo, otro");
        }
        if (file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        UUID docId = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String storedName = docId + "_" + sanitizeFilename(originalFilename);
        Path targetDir = Paths.get(uploadsDir, "lawyers", lawyerId.toString());

        try {
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }

        String filePath = "lawyers/" + lawyerId + "/" + storedName;

        LawyerDocument doc = new LawyerDocument();
        doc.setId(docId);
        doc.setLawyerId(lawyerId);
        doc.setDocType(docType);
        doc.setFilePath(filePath);
        doc.setFileName(originalFilename);
        doc.setContentType(file.getContentType());
        doc.setSizeBytes(file.getSize());

        LawyerDocument saved = documentRepository.save(doc);
        return DocumentResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> listByLawyer(UUID lawyerId) {
        return documentRepository.findByLawyerId(lawyerId).stream()
            .map(DocumentResponse::from)
            .toList();
    }

    private String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

package com.legalnow.api.lawyer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.legalnow.api.common.exception.BadRequestException;
import com.legalnow.api.common.exception.NotFoundException;
import com.legalnow.api.lawyer.domain.LawyerDocument;
import com.legalnow.api.lawyer.domain.LawyerDocumentRepository;
import com.legalnow.api.lawyer.domain.LawyerProfileRepository;
import com.legalnow.api.lawyer.dto.DocumentResponse;

@ExtendWith(MockitoExtension.class)
class LawyerDocumentServiceTest {

    @Mock
    private LawyerDocumentRepository documentRepository;

    @Mock
    private LawyerProfileRepository lawyerProfileRepository;

    private LawyerDocumentService documentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        documentService = new LawyerDocumentService(documentRepository, lawyerProfileRepository);
        ReflectionTestUtils.setField(documentService, "uploadsDir", tempDir.toString());
    }

    @Test
    void upload_lawyerNotFound_throwsNotFound() {
        UUID lawyerId = UUID.randomUUID();
        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(false);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());

        assertThrows(NotFoundException.class, () -> documentService.upload(lawyerId, file, "cedula"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    void upload_invalidDocType_throwsBadRequest() {
        UUID lawyerId = UUID.randomUUID();
        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(true);

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());

        assertThrows(BadRequestException.class, () -> documentService.upload(lawyerId, file, "invalid_type"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    void upload_emptyFile_throwsBadRequest() {
        UUID lawyerId = UUID.randomUUID();
        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(true);

        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);

        assertThrows(BadRequestException.class, () -> documentService.upload(lawyerId, emptyFile, "cedula"));
        verify(documentRepository, never()).save(any());
    }

    @Test
    void upload_happyPath_savesDocAndReturnsResponse() throws IOException {
        UUID lawyerId = UUID.randomUUID();
        when(lawyerProfileRepository.existsById(lawyerId)).thenReturn(true);

        MockMultipartFile file = new MockMultipartFile(
            "file", "cedula.pdf", "application/pdf", "pdf-content".getBytes()
        );

        LawyerDocument saved = new LawyerDocument();
        saved.setId(UUID.randomUUID());
        saved.setLawyerId(lawyerId);
        saved.setDocType("cedula");
        saved.setFilePath("lawyers/" + lawyerId + "/cedula.pdf");
        saved.setFileName("cedula.pdf");
        saved.setContentType("application/pdf");
        saved.setSizeBytes(11L);

        when(documentRepository.save(any(LawyerDocument.class))).thenReturn(saved);

        DocumentResponse response = documentService.upload(lawyerId, file, "cedula");

        assertNotNull(response);
        assertEquals(lawyerId, response.lawyerId());
        assertEquals("cedula", response.docType());
        verify(documentRepository).save(any(LawyerDocument.class));

        // Verify file was written to temp dir
        Path lawyerDir = tempDir.resolve("lawyers").resolve(lawyerId.toString());
        assertEquals(1, Files.list(lawyerDir).count());
    }
}

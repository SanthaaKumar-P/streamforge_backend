package com.streamforge.controller;

import com.streamforge.dto.request.FileUploadRequest;
import com.streamforge.dto.response.ShowFileResponse;
import com.streamforge.service.ShowFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class ShowFileController {

    private final ShowFileService showFileService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public ResponseEntity<ShowFileResponse> uploadFile(
            @Valid @RequestBody FileUploadRequest request) {

        return ResponseEntity.ok(
                showFileService.uploadFile(request)
        );
    }

    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ShowFileResponse>> getFilesByShow(
            @PathVariable Long showId) {

        return ResponseEntity.ok(
                showFileService.getFilesByShow(showId)
        );
    }

    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAnyRole('ADMIN','CREATOR')")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long fileId) {

        showFileService.deleteFile(fileId);

        return ResponseEntity.ok("File deleted successfully");
    }

}
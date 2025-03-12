package org.gatorapps.garesearch.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.gatorapps.garesearch.dto.ErrorResponse;
import org.gatorapps.garesearch.middleware.ValidateUserAuthInterceptor;
import org.gatorapps.garesearch.model.account.User;
import org.gatorapps.garesearch.model.garesearch.File;
import org.gatorapps.garesearch.repository.garesearch.FileRepository;
import org.gatorapps.garesearch.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/appApi/garesearch/file")
public class FileController {

    private final S3Service s3Service;
    private final FileRepository fileRepository;

    public FileController(S3Service s3Service, FileRepository fileRepository) {
        this.s3Service = s3Service;
        this.fileRepository = fileRepository;
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> getApplicantResumeMetadata(@Valid HttpServletRequest request, @PathVariable String fileId) {
        if (fileId == null) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Missing fileId");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<File> fileOptional = fileRepository.findById(fileId);
        if (fileOptional.isEmpty()) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Invalid fileId");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        File file = fileOptional.get();

        // Authorization check
        // Retrieve userAuth from request attributes
        User authedUser = ((ValidateUserAuthInterceptor.UserAuth) request.getAttribute("userAuth")).getAuthedUser();
        if (!(
                // User is file uploader
                file.getOpid().equals(authedUser.getOpid()) |
                // TODO: User received application from uploader with given fileId as attachment
                true)) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "You are not authorized to access this file");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        // Download the file from S3
        try {
            return s3Service.downloadFile(file);
        } catch (Exception e) {
            ErrorResponse<Void> response = new ErrorResponse<>("-", "Unable to download file");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

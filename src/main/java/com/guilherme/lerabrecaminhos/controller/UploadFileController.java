package com.guilherme.lerabrecaminhos.controller;


import com.guilherme.lerabrecaminhos.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequestMapping("/file-upload")
@Api(tags = "File Upload")
@RestController
@CrossOrigin(origins = "http://localhost:3002")
@AllArgsConstructor
public class UploadFileController {
    private final FileUploadService service;

    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation("Anexa arquivo e devolve o id")
    @PostMapping
    public Integer storeFile(@RequestPart(required = false) MultipartFile file) {
        return service.storeFile(file).getId();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation("Faz download do arquivo")
    @GetMapping
    public ResponseEntity<Resource> downloadFile(@RequestParam Integer fileId) {
        return service.downloadFile(fileId);
    }

}

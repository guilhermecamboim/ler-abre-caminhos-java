package com.guilherme.lerabrecaminhos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "files")
@Data
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    private String fileName;

    @JsonIgnore
    private String fileType;

    private String fileUrl;


    public UploadFile(Object o, String fileName, String contentType) {
        this.fileName = fileName;
        this.fileType = contentType;
    }

}
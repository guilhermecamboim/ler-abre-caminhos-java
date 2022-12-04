package com.guilherme.lerabrecaminhos.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Posts")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Campo título obrigatório")
    @NotNull
    private String title;
    @Lob
    private String firstParagraph;
    @Lob
    private String secondParagraph;
    @Lob
    private String thirdParagraph;
    private String impactPhrase;
    private String authorLink;
    private String authorSocialMedia;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    @OneToOne
    private UploadFile file;


    @PrePersist
    private void onCreate() {
        createdAt = new Date();
    }

}

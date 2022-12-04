package com.guilherme.lerabrecaminhos.controller.dto;

import lombok.Data;

import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreatePostDTO {
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

    @OneToOne
    private Integer file;
}

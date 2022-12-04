package com.guilherme.lerabrecaminhos.controller;

import com.guilherme.lerabrecaminhos.controller.dto.CreatePostDTO;
import com.guilherme.lerabrecaminhos.controller.impl.UploadFileRepository;
import com.guilherme.lerabrecaminhos.entities.Post;
import com.guilherme.lerabrecaminhos.controller.impl.PostRepository;
import com.guilherme.lerabrecaminhos.entities.UploadFile;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Api(tags = "Post")
@CrossOrigin(origins = "http://localhost:3002")
@AllArgsConstructor
public class PostController {
    PostRepository repository;

    UploadFileRepository fileRepository;

    @GetMapping("/post")
    public List<Post> getAllPost(){
        return repository.findAll();
    }

    @GetMapping("/post/{id}")
    public Optional<Post> getPostById(@PathVariable Integer id){
        return repository.findById(id);
    }

    @PostMapping("/post")
    public Post savePost(@RequestBody CreatePostDTO postDTO){
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .firstParagraph(postDTO.getFirstParagraph())
                .secondParagraph(postDTO.getSecondParagraph())
                .thirdParagraph(postDTO.getThirdParagraph())
                .impactPhrase(postDTO.getImpactPhrase())
                .authorLink(postDTO.getAuthorLink())
                .authorSocialMedia(postDTO.getAuthorSocialMedia())
                .file(getFileById(postDTO.getFile()))
                .build();
        return repository.save(post);
    }

    private UploadFile getFileById(Integer file) {
        Optional<UploadFile> uploadFile = fileRepository.findById(file);
        return uploadFile.isEmpty() ? null : uploadFile.get();
    }

    @DeleteMapping("/post/{id}")
    public void deletePost(@PathVariable Integer id) {
        repository.deleteById(id);
    }


}

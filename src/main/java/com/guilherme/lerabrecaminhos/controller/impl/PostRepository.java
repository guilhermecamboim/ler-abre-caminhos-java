package com.guilherme.lerabrecaminhos.controller.impl;

import com.guilherme.lerabrecaminhos.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

}

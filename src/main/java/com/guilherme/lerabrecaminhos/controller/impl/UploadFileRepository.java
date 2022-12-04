package com.guilherme.lerabrecaminhos.controller.impl;

import com.guilherme.lerabrecaminhos.entities.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Integer> {
}

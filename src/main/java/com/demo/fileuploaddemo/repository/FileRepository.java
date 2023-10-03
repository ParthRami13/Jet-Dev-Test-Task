package com.demo.fileuploaddemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.fileuploaddemo.entity.ExcelFileMetadata;

public interface FileRepository extends JpaRepository<ExcelFileMetadata, Long> {

}

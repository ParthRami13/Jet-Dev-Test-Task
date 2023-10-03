package com.demo.fileuploaddemo.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.fileuploaddemo.entity.ExcelFileData;

public interface ExcelFileDataRepository extends JpaRepository<ExcelFileData, Long> {
    public Set<ExcelFileData> getExcelFileDataByFileId(Long fileId);
}

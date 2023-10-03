package com.demo.fileuploaddemo.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@ToString
public class ExcelFileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessOn;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "last_reviewed_by", referencedColumnName = "id")
    private User lastReviewedBy;

    private String status;

    private boolean isDeleted;
    
    @JsonInclude(Include.NON_NULL) 
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "file")
    private Set<ExcelFileData> fileRecords;
}

package com.demo.fileuploaddemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@ToString
public class ExcelFileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "JSONB")
    private String header;
    
    @Column(columnDefinition = "JSONB")
    private String data;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="file_id",referencedColumnName = "id")
    @JsonIgnore
    private ExcelFileMetadata file;
    
}

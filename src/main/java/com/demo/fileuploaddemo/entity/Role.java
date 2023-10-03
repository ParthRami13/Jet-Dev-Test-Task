package com.demo.fileuploaddemo.entity;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "role")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60)
    private String name;
}

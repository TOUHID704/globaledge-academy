package com.globaledge.academy.lms.course.entity;

import com.globaledge.academy.lms.course.enums.ContentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "module_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moduleContentId;

    private String title;

    private Integer contentNumber;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "media_id")
    private Long mediaId;


    @Transient
    private String mediaUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private CourseModule courseModule;
}
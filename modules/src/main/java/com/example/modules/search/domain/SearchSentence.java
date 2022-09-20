package com.example.modules.search.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@DynamicUpdate
@NoArgsConstructor
@Table(name = "search_sentence")
@Entity
public class SearchSentence implements Serializable {

    @Id
    @Column(name = "sentence", nullable = false, updatable = false)
    private String sentence;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "registration_date", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date registrationDate;

    @Column(name = "modification_date", nullable = false)
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date modificationDate;
}

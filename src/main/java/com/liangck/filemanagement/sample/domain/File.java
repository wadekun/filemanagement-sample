package com.liangck.filemanagement.sample.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author liangck
 * @version 1.0
 * @since 2018/1/16 17:57
 */
@Data
@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "fdfs_key")
    private String fdfsKey;

    @Column(name = "upload_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;

    @Column(name = "origin_name")
    private String originName;
}

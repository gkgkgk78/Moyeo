package com.moyeo.main.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(length = 2000)
    private String content;

    @Builder.Default
    @ColumnDefault("0")
    private Boolean isChecked = false;

    private LocalDateTime createTime;


}

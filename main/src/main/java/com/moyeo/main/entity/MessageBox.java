package com.moyeo.main.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(length = 30)
    private String category;
    @Column(length = 2000)
    private String content;

    @Builder.Default
    @ColumnDefault("0")
    private Boolean isChecked = false;



}

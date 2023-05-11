package com.example.batch.RestaurantRecommendDto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MessageBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User userId;

    @Column(length = 2000)
    private String content;

    @Builder.Default
    @ColumnDefault("0")
    private Boolean isChecked = false;

    private LocalDateTime createTime;

}

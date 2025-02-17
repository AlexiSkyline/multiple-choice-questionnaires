package org.skyline.mcq.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Result result;
    private String userAnswers;
    private Boolean isCorrect;
    private Integer points;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}

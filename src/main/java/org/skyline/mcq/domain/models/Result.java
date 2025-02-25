package org.skyline.mcq.domain.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    private Account account;

    @ManyToOne
    private Survey survey;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp startTime;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp endTime;
    private Integer duration;
    private Integer totalPoints;
    private Integer correctAnswers;
    private Integer incorrectAnswers;

    @OneToMany(mappedBy = "result")
    private Set<Answer> answers;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}

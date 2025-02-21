package org.skyline.mcq.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Survey {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    private String title;
    private String description;
    private String image;
    private Integer maxPoints;
    private Integer questionCount;

    @ManyToOne
    private Category category;
    private boolean isActive = true;
    private Integer timeLimit;

    @ManyToOne
    private Account account;
    private Integer attempts;
    @Column(name = "is_public")
    private boolean hasRestrictedAccess = false;
    private boolean status = false;

    @Column(nullable = true, length = 60)
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}

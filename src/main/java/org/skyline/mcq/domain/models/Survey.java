package org.skyline.mcq.domain.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
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
    @JsonBackReference
    private Category category;

    @Builder.Default
    private Boolean active = true;

    private Integer timeLimit;

    @ManyToOne
    @JsonBackReference
    private Account account;
    private Integer attempts;

    @Builder.Default
    @Column(name = "is_public")
    private Boolean hasRestrictedAccess = false;

    @Builder.Default
    private Boolean status = false;

    @Column(nullable = true, length = 60)
    private String password;

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "survey")
    private Set<Question> questions = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}

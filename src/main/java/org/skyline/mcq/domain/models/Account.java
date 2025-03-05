package org.skyline.mcq.domain.models;

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
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    private String firstName;
    private String lastName;

    @Column(unique = true, length = 20)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(length = 60)
    private String password;
    private String profileImage;
    private String description;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "account")
    private Set<Category> categories = new HashSet<>();

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "account")
    private Set<Survey> surveys = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}

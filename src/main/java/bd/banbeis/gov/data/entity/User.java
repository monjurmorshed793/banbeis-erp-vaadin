package bd.banbeis.gov.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.ScriptAssert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "application_user", indexes = @Index(columnList = "username, fullName, email"))
public class User extends AbstractAuditing {

    @Column(unique = true, nullable = false, length = 25)
    @NotNull(message = "Valid unique username is required")
    private String username;

    @Column(nullable = false, length = 50)
    @NotNull(message = "Full name is required")
    private String fullName;

    @Column(nullable = false, length = 50, unique = true)
    @Email(message = "An valid email is required")
    @NotNull(message = "Email is required")
    private String email;

    @Transient
    @NotNull
    private String password;
    @Transient
    @NotNull
    private String confirmPassword;

    @JsonIgnore
    private String hashedPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    private String profilePictureUrl;
}

package bd.banbeis.gov.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "application_user", indexes = @Index(columnList = "username, fullName, email"))
public class User extends AbstractAuditing {

    @Column(unique = true, nullable = false, length = 25)
    private String username;

    @Column(nullable = false, length = 50)
    private String fullName;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @JsonIgnore
    private String hashedPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
    @Lob
    private String profilePictureUrl;
}

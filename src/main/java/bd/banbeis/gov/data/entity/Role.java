package bd.banbeis.gov.data.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="role")
public class Role extends AbstractEntity{
    @Column(unique = true, nullable = false, length = 15)
    private String role;
}

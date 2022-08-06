package bd.banbeis.gov.data.service;


import bd.banbeis.gov.data.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
}

package bd.banbeis.gov.views.usermanagement;

import bd.banbeis.gov.data.service.UserRepository;
import bd.banbeis.gov.views.MainLayout;
import com.vaadin.flow.router.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;

@PageTitle("Update User")
@Route(value="update-user/:username?", layout = MainLayout.class)
@PermitAll
public class UpdateUser extends AddUser implements BeforeEnterObserver {
    private final UserRepository updateUserRepository;
    public UpdateUser(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRepository updateUserRepository) {
        super(userRepository, passwordEncoder);
        this.updateUserRepository = updateUserRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String username = beforeEnterEvent.getRouteParameters().get("username").orElseThrow();
        user = updateUserRepository.findByUsername(username);
        nameField.setValue(user.getFullName());
        userNameField.setValue(user.getUsername());
        userNameField.setReadOnly(true);
    }

}

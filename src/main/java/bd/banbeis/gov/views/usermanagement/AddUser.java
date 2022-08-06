package bd.banbeis.gov.views.usermanagement;

import bd.banbeis.gov.data.entity.User;
import bd.banbeis.gov.data.service.UserRepository;
import bd.banbeis.gov.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;

@PageTitle("Add User")
@Route(value="add-user", layout = MainLayout.class)
@PermitAll
public class AddUser extends Div {
    public User user = new User();
    public VerticalLayout verticalLayout = new VerticalLayout();

    public TextField nameField = new TextField("Full Name");
    public TextField userNameField = new TextField("User Name");
    public PasswordField passwordField = new PasswordField("Password");
    public PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AddUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        addClassNames("grid-container", "grid");
        configureFormLayout();
        addSavebutton();

        add(verticalLayout);
    }

    private void configureFormLayout(){
        FormLayout userFormLayout = new FormLayout();


        userFormLayout.add(nameField, userNameField, passwordField, confirmPasswordField);
        userFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));
        userFormLayout.setColspan(passwordField, 2);
        userFormLayout.setColspan(confirmPasswordField,2 );

        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        verticalLayout.add(userFormLayout);
    }

    private void addSavebutton(){
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener((clickEvent->{
            user.setName(nameField.getValue());
            user.setUsername(userNameField.getValue());
            user.setHashedPassword(passwordEncoder.encode(passwordField.getValue()));
            userRepository.save(user);
            Notification saveSuccessNotification = Notification.show("Save successful");
            saveSuccessNotification.setPosition(Notification.Position.TOP_CENTER);
            Notification.show("Save Successful");

            getUI().ifPresent(ui-> ui.navigate("user-list"));
        }));
        verticalLayout.add(saveButton);
    }

}

package bd.banbeis.gov.views.usermanagement;

import bd.banbeis.gov.data.entity.Role;
import bd.banbeis.gov.data.entity.User;
import bd.banbeis.gov.data.service.RoleRepository;
import bd.banbeis.gov.data.service.UserRepository;
import bd.banbeis.gov.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Add User")
@Route(value="add-user", layout = MainLayout.class)
@PermitAll
public class AddUser extends Div {
    public User user = new User();
    public VerticalLayout verticalLayout = new VerticalLayout();
    FormLayout userFormLayout = new FormLayout();

    public TextField nameField = new TextField("Full Name");
    public TextField userNameField = new TextField("User Name");
    MultiSelectComboBox<Role> roleField = new MultiSelectComboBox<>("Roles");

    public PasswordField passwordField = new PasswordField("Password");
    public PasswordField confirmPasswordField = new PasswordField("Confirm Password");
    public Span passwordValidationText;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public AddUser(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

        addClassNames("grid-container", "grid");
        configureFormLayout();
        addSavebutton();

        add(verticalLayout);
    }

    private void configureFormLayout(){
        List<Role> roles = roleRepository.findAll();
        roleField.setItems(roles);
        roleField.setItemLabelGenerator(Role::getRole);

        userFormLayout.add(nameField, userNameField, roleField, passwordField, confirmPasswordField);
        userFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));
        userFormLayout.setColspan(passwordField, 2);
        userFormLayout.setColspan(confirmPasswordField,2 );
        verticalLayout.add(userFormLayout);

        passwordsValidityCheck();
    }

    private void passwordsValidityCheck(){
        Div passwordValidation = new Div();
        passwordValidationText = new Span();
        passwordValidation.add(new Text(""), passwordValidationText);
        passwordField.setHelperComponent(passwordValidation);
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        passwordField.addValueChangeListener(e->{
            processPasswordValidationCheck();
        });

        confirmPasswordField.setHelperComponent(passwordValidation);
        confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        confirmPasswordField.addValueChangeListener(e->{
            processPasswordValidationCheck();
        });
    }


    private void addSavebutton(){
        Button saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener((clickEvent->{
            user.setFullName(nameField.getValue());
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

    private void processPasswordValidationCheck(){
        String mainPassword = passwordField.getValue();
        String confirmationPassword = confirmPasswordField.getValue();
        if(confirmationPassword!=null && !mainPassword.equals(confirmationPassword)){
            passwordValidationText.setText("Confirmation password should match with the provided password.");
        }else{
            passwordValidationText.setText("");
        }
    }

}

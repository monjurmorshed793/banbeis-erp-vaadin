package bd.banbeis.gov.views.usermanagement;

import bd.banbeis.gov.data.entity.Role;
import bd.banbeis.gov.data.entity.User;
import bd.banbeis.gov.data.service.RoleRepository;
import bd.banbeis.gov.data.service.UserRepository;
import bd.banbeis.gov.views.MainLayout;
import com.vaadin.flow.component.ComponentEvent;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
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

    public TextField fullNameField = new TextField("Full Name");
    public TextField usernameField = new TextField("User Name");
    MultiSelectComboBox<Role> rolesField = new MultiSelectComboBox<>("Roles");

    public PasswordField passwordField = new PasswordField("Password");
    public PasswordField confirmPasswordField = new PasswordField("Confirm Password");
    Binder<User> userBinder = new BeanValidationBinder<>(User.class);
    public Span passwordValidationText;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public AddUser(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
//        userBinder.bindInstanceFields(this);
        addClassNames("grid-container", "grid");
        configureFormLayout();
        addSavebutton();
//        userBinder.bindInstanceFields(this.user);
        add(verticalLayout);
    }

    protected void configureBinder(){
        userBinder.forField(fullNameField).bind(User::getFullName, User::setFullName);
        userBinder.forField(usernameField).bind(User::getUsername, User::setUsername);
        userBinder.forField(rolesField).bind(User::getRoles, User::setRoles);
        userBinder.forField(passwordField).bind(User::getPassword, User::setPassword);
        userBinder.forField(confirmPasswordField).bind(User::getConfirmPassword, User::setConfirmPassword);
        userBinder.readBean(user);
    }

    private void configureFormLayout(){
        List<Role> roles = roleRepository.findAll();
        this.rolesField.setItems(roles);
        this.rolesField.setItemLabelGenerator(Role::getRole);

        userFormLayout.add(fullNameField, usernameField, this.rolesField, passwordField, confirmPasswordField);
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
            userBinder.validate();
            validateAndSave();
        }));
        verticalLayout.add(saveButton);
    }

    private void validateAndSave(){
        try{
            userBinder.writeBean(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            Notification.show("Save Successful");
            getUI().ifPresent(ui-> ui.navigate("user-list"));
        }catch (ValidationException e){
            e.printStackTrace();
        }
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

    public static abstract class AddUserFormEvent extends ComponentEvent<AddUser>{
        private User user;
        protected AddUserFormEvent(AddUser source, User user) {
            super(source, false);

            this.user   = user;
        }
    }

    public static class SaveEvent extends AddUserFormEvent{
        protected SaveEvent(AddUser source, User user) {
            super(source, user);
        }
    }

}

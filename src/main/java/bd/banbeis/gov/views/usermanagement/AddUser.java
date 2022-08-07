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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Add User")
@Route(value="add-user", layout = MainLayout.class)
@PermitAll
@Transactional
public class AddUser extends Div {
    public User user = new User();
    public VerticalLayout verticalLayout = new VerticalLayout();
    FormLayout userFormLayout = new FormLayout();

    public TextField fullName = new TextField("Full Name");
    public TextField userName = new TextField("User Name");
    MultiSelectComboBox<Role> roles = new MultiSelectComboBox<>("Roles");

    public EmailField email = new EmailField("Email");

    public PasswordField password = new PasswordField("Password");
    public PasswordField confirmPassword = new PasswordField("Confirm Password");
    Binder<User> userBinder = new BeanValidationBinder<>(User.class);
    public Span passwordValidationText;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


    public AddUser(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        user = new User();
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        userBinder = new BeanValidationBinder<>(User.class);
        userBinder.bindInstanceFields(this);
        addClassNames("grid-container", "grid");
        configureFormLayout();
        addSavebutton();
        add(verticalLayout);
    }

    protected void configureBinder(){
        userBinder.readBean(user);

        userBinder.forField(fullName)
                .bind(User::getFullName, User::setFullName);
        userBinder.forField(userName).bind(User::getUsername, User::setUsername);
        userBinder.forField(email).bind(User::getEmail, User::setEmail);
        userBinder.forField(roles).bind(User::getRoles, User::setRoles).validate(true);
        userBinder.forField(password).bind(User::getPassword, User::setPassword);
        userBinder.forField(confirmPassword).bind(User::getConfirmPassword, User::setConfirmPassword);
    }

    private void configureFormLayout(){
        List<Role> roles = roleRepository.findAll();
        this.roles.setItems(roles);
        this.roles.setItemLabelGenerator(Role::getRole);

        userFormLayout.add(fullName, userName, this.roles, this.email, password, confirmPassword);
        userFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));
        userFormLayout.setColspan(password, 2);
        userFormLayout.setColspan(confirmPassword,2 );
        verticalLayout.add(userFormLayout);

        passwordsValidityCheck();
    }

    private void passwordsValidityCheck(){
        Div passwordValidation = new Div();
        passwordValidationText = new Span();
        passwordValidation.add(new Text(""), passwordValidationText);
        password.setHelperComponent(passwordValidation);
        password.setValueChangeMode(ValueChangeMode.EAGER);
        password.addValueChangeListener(e->{
            processPasswordValidationCheck();
        });

        confirmPassword.setHelperComponent(passwordValidation);
        confirmPassword.setValueChangeMode(ValueChangeMode.EAGER);
        confirmPassword.addValueChangeListener(e->{
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
            userBinder.writeBean(this.user);
            user.setHashedPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            Notification.show("Save Successful");
            getUI().ifPresent(ui-> ui.navigate("user-list"));
        }catch (ValidationException e){
            e.printStackTrace();
        }
    }

    private void processPasswordValidationCheck(){
        String mainPassword = password.getValue();
        String confirmationPassword = confirmPassword.getValue();
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

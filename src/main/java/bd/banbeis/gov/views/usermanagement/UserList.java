package bd.banbeis.gov.views.usermanagement;

import javax.annotation.security.PermitAll;
import javax.swing.*;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import bd.banbeis.gov.data.entity.User;
import bd.banbeis.gov.data.service.UserRepository;
import bd.banbeis.gov.views.MainLayout;

import java.util.List;
import java.util.stream.Collectors;


@PageTitle("User List")
@Route(value="user-list", layout = MainLayout.class)
@PermitAll
public class UserList extends VerticalLayout {
    private final UserRepository userRepository;


    public UserList(UserRepository userRepository){
        this.userRepository = userRepository;
        addNewUserButton();
        setParentVerticalLayout();
        addUserGrid();
    }

    private void addUserGrid(){
        int pageSize = userRepository.count ()>0? (int) (userRepository.count()/20): 1; // want to show 20 items each page
        Grid<User> userGrid = new Grid<>(20);

        // configuring DataProvider, it will help in loading large data.
        DataProvider<User, Void> dataProvider =
                DataProvider.fromCallbacks(query->{
                   int offset = query.getOffset();
                   int limit = query.getLimit();

                   Page<User> users = userRepository
                           .findAll(PageRequest.of(offset, limit));
                   return users.getContent().stream();
                }, query -> Integer.parseInt(userRepository.count()+""));

        userGrid.setDataProvider(dataProvider);
        userGrid.addColumn(User::getId).setHeader("ID");
        userGrid.addColumn(User::getUsername).setHeader("User Name");
        userGrid.addColumn(User::getFullName).setHeader("Full Name");
        userGrid.addColumn(createRoleRenderer()).setHeader("Roles");
        userGrid.addColumn(User::getProfilePictureUrl).setHeader("Profile Picture");
        userGrid.addComponentColumn(user->{
           Button editButton = new Button("Edit");
           editButton.addClickListener(e->{
               getUI().ifPresent(ui-> ui.navigate(UpdateUser.class, new RouteParameters("username", user.getUsername())));
           });

           return editButton;
        });
        add(userGrid);
    }

    private static Renderer<User> createRoleRenderer(){
        return LitRenderer.<User>of("<vaadin-label>${item.roles}</vaadin-label>")
                .withProperty("roles", UserList::getRolesStr);
    }

    private static String getRolesStr(User user){
        List<String> roles = user.getRoles()
                .stream()
                .map(r-> r.getRole())
                .collect(Collectors.toList());
        String rolesStr = String.join(",", roles);
        return rolesStr;
    }


    private void setParentVerticalLayout(){
        setPadding(true);
        setSpacing(true);
    }

    private void addNewUserButton(){
        VerticalLayout addButtonLayout = new VerticalLayout();
        addButtonLayout.setAlignItems(Alignment.END);
        Button newUserButton = new Button("Add New User", new Icon(VaadinIcon.PLUS));
        newUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newUserButton.addClickListener(eventListener->{
            getUI().ifPresent(ui-> ui.navigate("add-user-view"));
        });
        addButtonLayout.add(newUserButton);
        add(addButtonLayout);
    }
}

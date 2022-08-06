package bd.banbeis.gov.views.usermanagement;

import bd.banbeis.gov.data.entity.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import bd.banbeis.gov.views.MainLayout;
import bd.banbeis.gov.data.service.UserRepository;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.PermitAll;
import javax.swing.*;


@PageTitle("User List")
@Route(value="user-list", layout = MainLayout.class)
@PermitAll
public class UserList extends VerticalLayout {
    private final UserRepository userRepository;


    public UserList(UserRepository userRepository){
        this.userRepository = userRepository;
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


    private void setParentVerticalLayout(){
        setPadding(true);
        setSpacing(true);
    }
}

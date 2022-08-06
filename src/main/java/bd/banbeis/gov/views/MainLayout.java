package bd.banbeis.gov.views;

import bd.banbeis.gov.components.appnav.AppNav;
import bd.banbeis.gov.components.appnav.AppNavItem;
import bd.banbeis.gov.data.entity.User;
import bd.banbeis.gov.security.AuthenticatedUser;
import bd.banbeis.gov.views.about.AboutView;
import bd.banbeis.gov.views.addressform.AddressFormView;
import bd.banbeis.gov.views.cardlist.CardListView;
import bd.banbeis.gov.views.chat.ChatView;
import bd.banbeis.gov.views.checkoutform.CheckoutFormView;
import bd.banbeis.gov.views.collaborativemasterdetail.CollaborativeMasterDetailView;
import bd.banbeis.gov.views.creditcardform.CreditCardFormView;
import bd.banbeis.gov.views.helloworld.HelloWorldView;
import bd.banbeis.gov.views.imagelist.ImageListView;
import bd.banbeis.gov.views.masterdetail.MasterDetailView;
import bd.banbeis.gov.views.personform.PersonFormView;
import bd.banbeis.gov.views.usermanagement.AddUser;
import bd.banbeis.gov.views.usermanagement.UserList;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("banbeis-erp");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createUserManagementNavigation(), createDefaultNaviation(), createFooter());
        section.addClassNames("drawer-section");
        section.setSizeUndefined();
        return section;
    }

    private void setDefaultAppNavBehavior(AppNav appNav){
        appNav.setCollapsible(true);
        appNav.setClassName("app-nav");
        appNav.setCollapsible(true);
    }

    private AppNav createUserManagementNavigation(){
        AppNav nav = new AppNav("User Management");
        setDefaultAppNavBehavior(nav);

        if(accessChecker.hasAccess(UserList.class)){
            nav.addItem(new AppNavItem("User List", UserList.class, "la la-user"));
        }

        if(accessChecker.hasAccess(AddUser.class)){
            nav.addItem(new AppNavItem("Add user", AddUser.class, "la la-user"));
        }
        return nav;
    }

    private AppNav createDefaultNaviation() {
        AppNav nav = new AppNav("Default Navigation");
        setDefaultAppNavBehavior(nav);

        if (accessChecker.hasAccess(HelloWorldView.class)) {
            nav.addItem(new AppNavItem("Hello World", HelloWorldView.class, "la la-globe"));

        }
        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new AppNavItem("About", AboutView.class, "la la-file"));

        }
        if (accessChecker.hasAccess(CardListView.class)) {
            nav.addItem(new AppNavItem("Card List", CardListView.class, "la la-list"));

        }
        if (accessChecker.hasAccess(MasterDetailView.class)) {
            nav.addItem(new AppNavItem("Master-Detail", MasterDetailView.class, "la la-columns"));

        }
        if (accessChecker.hasAccess(CollaborativeMasterDetailView.class)) {
            nav.addItem(new AppNavItem("Collaborative Master-Detail", CollaborativeMasterDetailView.class,
                    "la la-columns"));

        }
        if (accessChecker.hasAccess(PersonFormView.class)) {
            nav.addItem(new AppNavItem("Person Form", PersonFormView.class, "la la-user"));

        }
        if (accessChecker.hasAccess(AddressFormView.class)) {
            nav.addItem(new AppNavItem("Address Form", AddressFormView.class, "la la-map-marker"));

        }
        if (accessChecker.hasAccess(CreditCardFormView.class)) {
            nav.addItem(new AppNavItem("Credit Card Form", CreditCardFormView.class, "la la-credit-card"));

        }
        if (accessChecker.hasAccess(ChatView.class)) {
            nav.addItem(new AppNavItem("Chat", ChatView.class, "la la-comments"));

        }
        if (accessChecker.hasAccess(ImageListView.class)) {
            nav.addItem(new AppNavItem("Image List", ImageListView.class, "la la-th-list"));

        }
        if (accessChecker.hasAccess(CheckoutFormView.class)) {
            nav.addItem(new AppNavItem("Checkout Form", CheckoutFormView.class, "la la-credit-card"));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("app-nav-footer");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getFullName(), user.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            ContextMenu userMenu = new ContextMenu(avatar);
            userMenu.setOpenOnClick(true);
            userMenu.addItem("Logout", e -> {
                authenticatedUser.logout();
            });

            Span name = new Span(user.getFullName());
            name.addClassNames("font-medium", "text-s", "text-secondary");

            layout.add(avatar, name);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

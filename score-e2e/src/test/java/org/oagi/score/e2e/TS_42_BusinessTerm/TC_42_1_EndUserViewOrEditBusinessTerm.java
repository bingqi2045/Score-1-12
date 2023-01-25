package org.oagi.score.e2e.TS_42_BusinessTerm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.oagi.score.e2e.BaseTest;
import org.oagi.score.e2e.menu.BIEMenu;
import org.oagi.score.e2e.obj.AppUserObject;
import org.oagi.score.e2e.obj.BusinessTermObject;
import org.oagi.score.e2e.obj.ContextCategoryObject;
import org.oagi.score.e2e.page.HomePage;
import org.oagi.score.e2e.page.business_term.CreateBusinessTermPage;
import org.oagi.score.e2e.page.business_term.ViewEditBusinessTermPage;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.oagi.score.e2e.impl.PageHelper.getText;

@Execution(ExecutionMode.CONCURRENT)
public class TC_42_1_EndUserViewOrEditBusinessTerm extends BaseTest {
    private List<AppUserObject> randomAccounts = new ArrayList<>();

    private void thisAccountWillBeDeletedAfterTests(AppUserObject appUser) {
        this.randomAccounts.add(appUser);
    }

    @BeforeEach
    public void init() {
        super.init();
    }

    @Test
    @DisplayName("TC_42_1_1")
    public void enduser_should_open_page_titled_business_term_under_bie_menu() {
        AppUserObject endUser = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
        thisAccountWillBeDeletedAfterTests(endUser);
        HomePage homePage = loginPage().signIn(endUser.getLoginId(), endUser.getPassword());
        BIEMenu bieMenu = homePage.getBIEMenu();
        String viewEditBusinessTermPageTitle = getText(bieMenu.openViewEditBusinessTermSubMenu().getTitle());
        assertEquals("Business Term", viewEditBusinessTermPageTitle);

    }

    @Test
    @DisplayName("TC_42_1_2")
    public void enduser_can_create_business_term_with_only_required_fields() {

        AppUserObject endUser = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
        thisAccountWillBeDeletedAfterTests(endUser);
        HomePage homePage = loginPage().signIn(endUser.getLoginId(), endUser.getPassword());
        BIEMenu bieMenu = homePage.getBIEMenu();
        ViewEditBusinessTermPage viewEditBusinessTermPage = bieMenu.openViewEditBusinessTermSubMenu();
        CreateBusinessTermPage createBusinessTermPage = viewEditBusinessTermPage.openCreateBusinessTermPage();

        BusinessTermObject businessTerm = new BusinessTermObject();
        businessTerm.setBusinessTerm("bt_" + randomAlphanumeric(5, 10));
        businessTerm.setExternalReferenceUri("http://www." + randomAscii(3,8) + ".com");
        viewEditBusinessTermPage = createBusinessTermPage.createBusinessTerm(businessTerm);











    }

    @Test
    @DisplayName("TC_42_1_3")
    public void enduser_cannot_create_business_term_if_any_required_field_missing() {
    }

    @Test
    @DisplayName("TC_42_1_4")
    public void enduser_can_search_for_business_term_based_only_on_its_term() {
    }

    @Test
    @DisplayName("TC_42_1_5")
    public void enduser_can_search_for_business_term_based_on_external_reference_uri() {
    }

    @Test
    @DisplayName("TC_42_1_6")
    public void enduser_can_click_business_term_to_update_its_details_in_edit_business_term_page() {
    }

    @Test
    @DisplayName("TC_42_1_7")
    public void enduser_cannot_change_definition_field_in_edit_business_term_page() {
    }
    @Test
    @DisplayName("TC_42_1_8")
    public void enduser_cannot_save_business_term_if_an_already_existing_term_and_uri_in_edit_business_term_page() {
    }

    @Test
    @DisplayName("TC_42_1_9")
    public void enduser_cannot_discard_business_term_in_edit_business_term_page_if_it_is_used_in_assignments() {
    }

    @Test
    @DisplayName("TC_42_1_10")
    public void enduser_can_discard_business_term_in_edit_business_term_page_if_not_used_in_any_assignments() {
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();

        // Delete random accounts
        this.randomAccounts.forEach(randomAccount -> {
            getAPIFactory().getAppUserAPI().deleteAppUserByLoginId(randomAccount.getLoginId());
        });
    }

}
package org.oagi.score.e2e.TS_42_BusinessTerm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.oagi.score.e2e.BaseTest;
import org.oagi.score.e2e.menu.BIEMenu;
import org.oagi.score.e2e.obj.*;
import org.oagi.score.e2e.page.HomePage;
import org.oagi.score.e2e.page.bie.EditBIEPage;
import org.oagi.score.e2e.page.business_term.AssignBusinessTermBTPage;
import org.oagi.score.e2e.page.business_term.BusinessTermAssignmentPage;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.oagi.score.e2e.impl.PageHelper.click;
import static org.oagi.score.e2e.impl.PageHelper.getText;

@Execution(ExecutionMode.CONCURRENT)
public class TC_42_2_BusinessTermAssignment extends BaseTest {

    private final List<AppUserObject> randomAccounts = new ArrayList<>();

    private void thisAccountWillBeDeletedAfterTests(AppUserObject appUser) {
        this.randomAccounts.add(appUser);
    }

    @BeforeEach
    public void init() {
        super.init();
    }

    @Test
    @DisplayName("TC_42_2_1")
    public void enduser_should_open_page_titled_business_term_assignment_on_edit_bie_page() {
        AppUserObject developer = getAPIFactory().getAppUserAPI().createRandomDeveloperAccount(false);
        thisAccountWillBeDeletedAfterTests(developer);
        AppUserObject endUser = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
        thisAccountWillBeDeletedAfterTests(endUser);
        BusinessTermObject randomBusinessTerm = getAPIFactory().getBusinessTermAPI().createRandomBusinessTerm(endUser);
        //use pre-existing BBIE node
        BusinessContextObject randomBusinessContext = getAPIFactory().getBusinessContextAPI().createRandomBusinessContext(developer);
        ReleaseObject release = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.3");
        ASCCPObject asccp = getAPIFactory().getCoreComponentAPI().getASCCPByDENAndReleaseNum("Source Activity. Source Activity", release.getReleaseNumber());
        TopLevelASBIEPObject topLevelASBIEP = getAPIFactory().getBusinessInformationEntityAPI().generateRandomTopLevelASBIEP(Collections.singletonList(randomBusinessContext), asccp, developer, "WIP");

        HomePage homePage = loginPage().signIn(developer.getLoginId(), developer.getPassword());
        EditBIEPage editBIEPage = homePage.getBIEMenu().openViewEditBIESubMenu().openEditBIEPage(topLevelASBIEP);

        String path = "/" + asccp.getPropertyTerm() + "/Note";
        WebElement bbieNode = editBIEPage.getNodeByPath(path);
        EditBIEPage.BBIEPanel bbiePanel = editBIEPage.getBBIEPanel(bbieNode);

        bbiePanel.toggleUsed();
        editBIEPage.hitUpdateButton();
        //Assign business term to pre-existing, used BBIE node
        assertTrue(bbiePanel.getAssignBusinessTermButton(true).isEnabled());
        BusinessTermAssignmentPage businessTermAssignmentPage = bbiePanel.clickShowBusinessTermsButton();
        assertEquals("Business Term Assignment", businessTermAssignmentPage.getTitle());
    }

    @Test
    @DisplayName("TC_42_2_2")
    public void enduser_can_view_all_business_terms_with_assignments_on_business_term_assignment_page() {
        AppUserObject developer = getAPIFactory().getAppUserAPI().createRandomDeveloperAccount(false);
        thisAccountWillBeDeletedAfterTests(developer);
        AppUserObject endUser = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
        thisAccountWillBeDeletedAfterTests(endUser);
        //use pre-existing BBIE node
        BusinessContextObject randomBusinessContext = getAPIFactory().getBusinessContextAPI().createRandomBusinessContext(developer);
        ReleaseObject release = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.3");
        //BBIE
        ASCCPObject asccp = getAPIFactory().getCoreComponentAPI().getASCCPByDENAndReleaseNum("Source Activity. Source Activity", release.getReleaseNumber());
        TopLevelASBIEPObject topLevelASBIEP = getAPIFactory().getBusinessInformationEntityAPI().generateRandomTopLevelASBIEP(Collections.singletonList(randomBusinessContext), asccp, developer, "WIP");

        HomePage homePage = loginPage().signIn(developer.getLoginId(), developer.getPassword());
        EditBIEPage editBIEPage = homePage.getBIEMenu().openViewEditBIESubMenu().openEditBIEPage(topLevelASBIEP);
        String path = "/" + asccp.getPropertyTerm() + "/Note";
        WebElement bbieNode = editBIEPage.getNodeByPath(path);
        EditBIEPage.BBIEPanel bbiePanel = editBIEPage.getBBIEPanel(bbieNode);

        bbiePanel.toggleUsed();
        editBIEPage.hitUpdateButton();
        //Assign business term to pre-existing, used BBIE node
        assertTrue(bbiePanel.getAssignBusinessTermButton(true).isEnabled());
        AssignBusinessTermBTPage assignBusinessTermBTPage = bbiePanel.clickAssignBusinessTermButton();
        //assign up to 3 random business terms to selected BIE for testing purpose
        ArrayList<BusinessTermObject> businessTerms = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BusinessTermObject randomBusinessTerm = getAPIFactory().getBusinessTermAPI().createRandomBusinessTerm(endUser);
            businessTerms.add(randomBusinessTerm);
            assignBusinessTermBTPage.setBusinessTerm(randomBusinessTerm.getBusinessTerm());
            assignBusinessTermBTPage.hitSearchButton();
            click(assignBusinessTermBTPage.getSelectCheckboxAtIndex(1));
            click(assignBusinessTermBTPage.getCreateButton());
            WebElement bbieNodeForLoop = homePage.getBIEMenu().openViewEditBIESubMenu().openEditBIEPage(topLevelASBIEP).getNodeByPath(path);
            assignBusinessTermBTPage = editBIEPage.getBBIEPanel(bbieNodeForLoop).clickAssignBusinessTermButton();
        }
        //ASBIE node
        ASCCPObject asccp2 = getAPIFactory().getCoreComponentAPI()
                .getASCCPByDENAndReleaseNum("Get Item Certificate Of Analysis. Get Item Certificate Of Analysis", release.getReleaseNumber());
        TopLevelASBIEPObject topLevelASBIEP2 = getAPIFactory().getBusinessInformationEntityAPI()
                .generateRandomTopLevelASBIEP(Arrays.asList(randomBusinessContext), asccp, developer, "WIP");

        EditBIEPage editBIEPage2 = homePage.getBIEMenu().openViewEditBIESubMenu().openEditBIEPage(topLevelASBIEP2);

        String path2 = "/" + asccp.getPropertyTerm() + "/Data Area/Item Certificate Of Analysis";
        WebElement asbieNode = editBIEPage2.getNodeByPath(path2);
        EditBIEPage.ASBIEPanel asbiePanel = editBIEPage2.getASBIEPanel(asbieNode);

        asbiePanel.toggleUsed();
        editBIEPage.hitUpdateButton();
        //Assign business term to pre-existing, used ASBIE node
        assertTrue(asbiePanel.getAssignBusinessTermButton(true).isEnabled());
        AssignBusinessTermBTPage assignBusinessTermBTPageASBIE = asbiePanel.clickAssignBusinessTermButton();
        //assign up to 3 random business terms to selected BIE for testing purpose
        for (int i = 0; i < 3; i++) {
            BusinessTermObject randomBusinessTerm = getAPIFactory().getBusinessTermAPI().createRandomBusinessTerm(endUser);
            businessTerms.add(randomBusinessTerm);
            assignBusinessTermBTPageASBIE.setBusinessTerm(randomBusinessTerm.getBusinessTerm());
            assignBusinessTermBTPageASBIE.hitSearchButton();
            click(assignBusinessTermBTPageASBIE.getSelectCheckboxAtIndex(1));
            click(assignBusinessTermBTPageASBIE.getCreateButton());
            WebElement asbieNodeForLoop = homePage.getBIEMenu().openViewEditBIESubMenu().openEditBIEPage(topLevelASBIEP2).getNodeByPath(path2);
            assignBusinessTermBTPageASBIE = editBIEPage.getASBIEPanel(asbieNodeForLoop).clickAssignBusinessTermButton();
        }
        //verify all random business terms assigned to the selected BIE are displayed in Business Term Assignment page
        WebElement bbieNodeForCheck = homePage.getBIEMenu().openViewEditBIESubMenu().openEditBIEPage(topLevelASBIEP).getNodeByPath(path);
        BusinessTermAssignmentPage btAssignmentPageForSelectedBIE = editBIEPage.getBBIEPanel(bbieNodeForCheck).clickShowBusinessTermsButton();
        assertTrue(btAssignmentPageForSelectedBIE.getTurnOffButton().isEnabled());
        click(btAssignmentPageForSelectedBIE.getTurnOffButton()); // list all BIEs in the business term assignment page
        for (int i = 0; i < businessTerms.size(); i++) {
            btAssignmentPageForSelectedBIE.setBusinessTerm(businessTerms.get(i).getBusinessTerm());
            click(btAssignmentPageForSelectedBIE.getSearchButton());
            assertTrue(btAssignmentPageForSelectedBIE.getSelectCheckboxAtIndex(1).isDisplayed());
        }
    }

    @Test
    @DisplayName("TC_42_2_3")
    public void enduser_can_view_asbies_bbies_on_business_term_assignment_page() {
    }

    @Test
    @DisplayName("TC_42_2_4")
    public void enduser_can_search_business_term_assignments_by_bietype_and_den_or_business_term_or_uri_or_typecode_on_business_term_assigment_page() {
    }

    @Test
    @DisplayName("TC_42_2_5")
    public void enduser_can_filter_only_preferred_business_terms_on_business_term_assignment_page() {
    }

    @Test
    @DisplayName("TC_42_2_6")
    public void enduser_can_select_bie_to_view_all_business_term_assignments_assigned_for_that_bie_on_business_term_assignment_page() {
    }

    @Test
    @DisplayName("TC_42_2_7")
    public void enduser_can_select_bie_to_view_all_business_term_not_assigned_but_available_for_that_bie_on_assign_business_term_page() {
    }

    @Test
    @DisplayName("TC_42_2_8")
    public void enduser_can_filter_business_terms_already_assigned_to_the_same_core_component_on_assign_business_term_page() {
    }

    @Test
    @DisplayName("TC_42_2_9")
    public void enduser_can_assign_duplicate_business_term_and_type_code_based_on_mixed_conditions_on_assign_business_term_page() {
    }

    @Test
    @DisplayName("TC_42_2_10")
    public void enduser_can_only_set_one_preferred_business_term_assignment_for_each_bie_on_assign_business_term_page() {
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
package org.oagi.score.e2e.TS_17_ReleaseBranchCodeListManagementForEndUser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.oagi.score.e2e.BaseTest;
import org.oagi.score.e2e.obj.*;
import org.oagi.score.e2e.page.HomePage;
import org.oagi.score.e2e.page.code_list.AddCommentDialog;
import org.oagi.score.e2e.page.code_list.EditCodeListPage;
import org.oagi.score.e2e.page.code_list.EditCodeListValueDialog;
import org.oagi.score.e2e.page.code_list.ViewEditCodeListPage;
import org.openqa.selenium.NoSuchElementException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.oagi.score.e2e.AssertionHelper.assertDisabled;
import static org.oagi.score.e2e.AssertionHelper.assertNotChecked;
import static org.oagi.score.e2e.impl.PageHelper.getSnackBarMessage;
import static org.oagi.score.e2e.impl.PageHelper.getText;

@Execution(ExecutionMode.CONCURRENT)
public class TC_17_2_CreatingABrandNewEndUserCodeList extends BaseTest {
    private final List<AppUserObject> randomAccounts = new ArrayList<>();

    @BeforeEach
    public void init() {
        super.init();

    }

    private void thisAccountWillBeDeletedAfterTests(AppUserObject appUser) {
        this.randomAccounts.add(appUser);
    }

    @Test
    @DisplayName("TC_17_2_TA_1")
    public void test_TA_1() {
        AppUserObject endUserA;
        ReleaseObject branch;
        {
            endUserA = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
            thisAccountWillBeDeletedAfterTests(endUserA);
            branch = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.5");
        }
        HomePage homePage = loginPage().signIn(endUserA.getLoginId(), endUserA.getPassword());
        ViewEditCodeListPage viewEditCodeListPage = homePage.getCoreComponentMenu().openViewEditCodeListSubMenu();
        viewEditCodeListPage.setBranch(branch.getReleaseNumber());
        EditCodeListPage editCodeListPage = viewEditCodeListPage.openNewCodeList(endUserA, branch.getReleaseNumber());
        assertEquals("Code List", getText(editCodeListPage.getCodeListNameField()));
        assertTrue(getAPIFactory().getCodeListAPI().isListIdUnique(getText(editCodeListPage.getListIDField())));
        assertEquals("Mutually defined (ZZZ)", getText(editCodeListPage.getAgencyIDListValueField()));
        assertEquals("1", getText(editCodeListPage.getVersionField()));
        assertEquals(null, getText(editCodeListPage.getDefinitionField()));
        assertEquals(null, getText(editCodeListPage.getDefinitionSourceField()));
        assertEquals(null, getText(editCodeListPage.getRemarkField()));
        assertDisabled(editCodeListPage.getDeprecatedSelectField());
        assertNotChecked(editCodeListPage.getDeprecatedSelectField());
        assertEquals("Namespace", getText(editCodeListPage.getNamespaceSelectField()));
        AddCommentDialog addCodeListCommentDialog = editCodeListPage.hitAddCommentButton();
        assertEquals(null, getText(addCodeListCommentDialog.getCommentField()));
        addCodeListCommentDialog.hitCloseButton();
        assertEquals("1", getText(editCodeListPage.getRevisionField()));

        CodeListObject codeList = getAPIFactory().getCodeListAPI().getNewlyCreatedCodeList(endUserA, branch.getReleaseNumber());
        ReleaseObject anotherBranch = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.6");
        homePage.getCoreComponentMenu().openViewEditCodeListSubMenu();
        assertThrows(NoSuchElementException.class, () -> {viewEditCodeListPage.searchCodeListByNameAndBranch(codeList.getName(), anotherBranch.getReleaseNumber());});
    }

    @Test
    @DisplayName("TC_17_2_TA_2")
    public void test_TA_2() {
        AppUserObject endUserA;
        ReleaseObject branch;
        NamespaceObject namespaceEU;
        {
            endUserA = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
            thisAccountWillBeDeletedAfterTests(endUserA);

            namespaceEU = getAPIFactory().getNamespaceAPI().createRandomEndUserNamespace(endUserA);
            branch = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.5");
        }
        HomePage homePage = loginPage().signIn(endUserA.getLoginId(), endUserA.getPassword());
        ViewEditCodeListPage viewEditCodeListPage = homePage.getCoreComponentMenu().openViewEditCodeListSubMenu();
        viewEditCodeListPage.setBranch(branch.getReleaseNumber());
        EditCodeListPage editCodeListPage = viewEditCodeListPage.openNewCodeList(endUserA, branch.getReleaseNumber());
        CodeListObject codeList = getAPIFactory().getCodeListAPI().getNewlyCreatedCodeList(endUserA, branch.getReleaseNumber());
        assertEquals(null, codeList.getBasedCodeListManifestId());
        editCodeListPage.setDefinition("test definition");
        editCodeListPage.setDefinitionSource("test definition source");
        editCodeListPage.setName("test code list");
        editCodeListPage.setNamespace(namespaceEU);
        EditCodeListValueDialog editCodeListValueDialog = editCodeListPage.addCodeListValue();
        editCodeListValueDialog.setCode("code value");
        editCodeListValueDialog.setMeaning("code meaning");
        editCodeListValueDialog.hitAddButton();
        editCodeListValueDialog = editCodeListPage.addCodeListValue();
        editCodeListValueDialog.setCode("code value 2");
        editCodeListValueDialog.setMeaning("code meaning 2");
        editCodeListValueDialog.hitAddButton();
        editCodeListValueDialog = editCodeListPage.addCodeListValue();
        editCodeListValueDialog.setCode("code value");
        editCodeListValueDialog.setMeaning("code meaning");
        String enteredValue = getText(editCodeListValueDialog.getCodeField());
        editCodeListValueDialog.hitAddButton();
        String message = enteredValue + " already exist";
        assert message.equals(getSnackBarMessage(getDriver()));
        editCodeListPage.hitUpdateButton();
    }

    @Test
    @DisplayName("TC_17_2_TA_3")
    public void test_TA_3() {
        AppUserObject endUserA;
        ReleaseObject branch;
        NamespaceObject namespaceEU;
        {
            endUserA = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
            thisAccountWillBeDeletedAfterTests(endUserA);

            namespaceEU = getAPIFactory().getNamespaceAPI().createRandomEndUserNamespace(endUserA);
            branch = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.5");
        }
        HomePage homePage = loginPage().signIn(endUserA.getLoginId(), endUserA.getPassword());
        ViewEditCodeListPage viewEditCodeListPage = homePage.getCoreComponentMenu().openViewEditCodeListSubMenu();
        viewEditCodeListPage.setBranch(branch.getReleaseNumber());
        EditCodeListPage editCodeListPage = viewEditCodeListPage.openNewCodeList(endUserA, branch.getReleaseNumber());
        CodeListObject codeList = getAPIFactory().getCodeListAPI().getNewlyCreatedCodeList(endUserA, branch.getReleaseNumber());
        assertEquals(null, codeList.getBasedCodeListManifestId());
        editCodeListPage.setDefinition("test definition");
        editCodeListPage.setDefinitionSource("test definition source");
        editCodeListPage.setName("test code list");
        editCodeListPage.setNamespace(namespaceEU);
        EditCodeListValueDialog editCodeListValueDialog = editCodeListPage.addCodeListValue();
        editCodeListValueDialog.setCode("code value");
        editCodeListValueDialog.setMeaning("code meaning");
        editCodeListValueDialog.hitAddButton();
        editCodeListValueDialog = editCodeListPage.addCodeListValue();
        editCodeListValueDialog.setCode("code value 2");
        editCodeListValueDialog.setMeaning("code meaning 2");
        editCodeListValueDialog.hitAddButton();
        editCodeListPage.selectCodeListValue("code value 2");
        editCodeListPage.removeCodeListValue();
        editCodeListPage.hitUpdateButton();
    }
    @Test
    @DisplayName("TC_17_2_TA_4")
    public void test_TA_4() {
        AppUserObject endUserA;
        ReleaseObject branch;
        NamespaceObject namespaceEU;
        {
            endUserA = getAPIFactory().getAppUserAPI().createRandomEndUserAccount(false);
            thisAccountWillBeDeletedAfterTests(endUserA);

            namespaceEU = getAPIFactory().getNamespaceAPI().createRandomEndUserNamespace(endUserA);
            branch = getAPIFactory().getReleaseAPI().getReleaseByReleaseNumber("10.8.5");
        }
        HomePage homePage = loginPage().signIn(endUserA.getLoginId(), endUserA.getPassword());
        ViewEditCodeListPage viewEditCodeListPage = homePage.getCoreComponentMenu().openViewEditCodeListSubMenu();
        viewEditCodeListPage.setBranch(branch.getReleaseNumber());
        EditCodeListPage editCodeListPage = viewEditCodeListPage.openNewCodeList(endUserA, branch.getReleaseNumber());
        CodeListObject codeList = getAPIFactory().getCodeListAPI().getNewlyCreatedCodeList(endUserA, branch.getReleaseNumber());
        editCodeListPage.setVersion("test version");
        editCodeListPage.setNamespace(namespaceEU);
        editCodeListPage.hitUpdateButton();
        assertEquals("true", editCodeListPage.getVersionField().getAttribute("aria-required"));
        String agencyIDList = getText(editCodeListPage.getAgencyIDListField());
        assertTrue(getAPIFactory().getCodeListAPI().checkCodeListUniqueness(codeList, agencyIDList));
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
        // Delete random accounts
        this.randomAccounts.forEach(newUser -> {
            getAPIFactory().getAppUserAPI().deleteAppUserByLoginId(newUser.getLoginId());
        });
    }
}

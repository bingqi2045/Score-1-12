package org.oagi.score.e2e.impl.page.business_term;

import org.oagi.score.e2e.impl.page.BasePageImpl;
import org.oagi.score.e2e.page.business_term.AssignBusinessTermPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.Duration.ofMillis;
import static org.oagi.score.e2e.impl.PageHelper.*;

public class AssignBusinessTermPageImpl extends BasePageImpl implements AssignBusinessTermPage {

    private static final By BRANCH_SELECT_FIELD_LOCATOR =
            By.xpath("//*[contains(text(), \"Branch\")]//ancestor::div[1]/mat-select[1]");

    private static final By STATE_SELECT_FIELD_LOCATOR =
            By.xpath("//*[contains(text(), \"State\")]//ancestor::div[1]/mat-select[1]");

    private static final By OWNER_SELECT_FIELD_LOCATOR =
            By.xpath("//*[contains(text(), \"Owner\")]//ancestor::div[1]/mat-select[1]");

    private static final By UPDATER_SELECT_FIELD_LOCATOR =
            By.xpath("//*[contains(text(), \"Updater\")]//ancestor::div[1]/mat-select[1]");

    private static final By DROPDOWN_SEARCH_FIELD_LOCATOR =
            By.xpath("//input[@aria-label=\"dropdown search\"]");

    private static final By UPDATED_START_DATE_FIELD_LOCATOR =
            By.xpath("//input[contains(@data-placeholder, \"Updated start date\")]");

    private static final By UPDATED_END_DATE_FIELD_LOCATOR =
            By.xpath("//input[contains(@data-placeholder, \"Updated end date\")]");

    private static final By TYPE_SELECT_FIELD_LOCATOR =
            By.xpath("//*[contains(text(), \"Type\")]//ancestor::div[1]/mat-select[1]");

    private static final By DEN_FIELD_LOCATOR =
            By.xpath("//span[contains(text(), \"DEN\")]//ancestor::div[1]/input");

    private static final By BUSINESS_CONTEXT_FIELD_LOCATOR =
            By.xpath("//span[contains(text(), \"Business Context\")]//ancestor::div[1]/input");

    private static final By TOP_LEVEL_BIE_FIELD_LOCATOR =
            By.xpath("//span[contains(text(), \"Top Level BIE\")]//ancestor::div[1]/input");

    private static final By SEARCH_BUTTON_LOCATOR =
            By.xpath("//span[contains(text(), \"Search\")]//ancestor::button[1]");

    private final BusinessTermAssignmentPageImpl parent;

    public AssignBusinessTermPageImpl(BusinessTermAssignmentPageImpl parent) {
        super(parent);
        this.parent = parent;
    }
    @Override
    protected String getPageUrl() {return getConfig().getBaseUrl().resolve("/business_term_management/assign_business_term/create").toString();
    }

    @Override
    public void openPage() {
        String url = getPageUrl();
        getDriver().get(url);
        assert "Assign Business Term".equals(getText(getTitle()));

    }

    @Override
    public WebElement getTitle() {
        return visibilityOfElementLocated(getDriver(), By.className("mat-card-title"));
    }

    @Override
    public WebElement getBranchSelectField() {
        return visibilityOfElementLocated(getDriver(), BRANCH_SELECT_FIELD_LOCATOR);
    }

    @Override
    public void setBranch(String branch) {
        retry(() -> {
            click(getBranchSelectField());
            WebElement optionField = visibilityOfElementLocated(getDriver(),
                    By.xpath("//span[contains(text(), \"" + branch + "\")]//ancestor::mat-option[1]/span"));
            click(optionField);
            waitFor(ofMillis(500L));
        });
    }

    @Override
    public WebElement getStateSelectField() {
        return visibilityOfElementLocated(getDriver(), STATE_SELECT_FIELD_LOCATOR);
    }

    @Override
    public void setState(String state) {
        click(getUpdaterSelectField());
        sendKeys(visibilityOfElementLocated(getDriver(), DROPDOWN_SEARCH_FIELD_LOCATOR), state);
        WebElement searchedSelectField = visibilityOfElementLocated(getDriver(),
                By.xpath("//mat-option//span[contains(text(), \"" + state + "\")]"));
        click(searchedSelectField);
        escape(getDriver());
    }

    @Override
    public WebElement getOwnerSelectField() {
        return visibilityOfElementLocated(getDriver(), OWNER_SELECT_FIELD_LOCATOR);
    }

    @Override
    public void setOwner(String owner) {
        click(getUpdaterSelectField());
        sendKeys(visibilityOfElementLocated(getDriver(), DROPDOWN_SEARCH_FIELD_LOCATOR), owner);
        WebElement searchedSelectField = visibilityOfElementLocated(getDriver(),
                By.xpath("//mat-option//span[contains(text(), \"" + owner + "\")]"));
        click(searchedSelectField);
        escape(getDriver());
    }

    @Override
    public WebElement getUpdaterSelectField() {
        return visibilityOfElementLocated(getDriver(), UPDATER_SELECT_FIELD_LOCATOR);
    }

    @Override
    public void setUpdater(String updater) {
        click(getUpdaterSelectField());
        sendKeys(visibilityOfElementLocated(getDriver(), DROPDOWN_SEARCH_FIELD_LOCATOR), updater);
        WebElement searchedSelectField = visibilityOfElementLocated(getDriver(),
                By.xpath("//mat-option//span[contains(text(), \"" + updater + "\")]"));
        click(searchedSelectField);
        escape(getDriver());

    }

    @Override
    public WebElement getBIEDenField() {
        return visibilityOfElementLocated(getDriver(), DEN_FIELD_LOCATOR);
    }

    @Override
    public void setBIEDenField(String bieDen) {
        sendKeys(getBIEDenField(), bieDen);
    }

    @Override
    public WebElement getTopLevelBIEField() {
        return visibilityOfElementLocated(getDriver(), TOP_LEVEL_BIE_FIELD_LOCATOR);
    }

    @Override
    public void setTopLevelBIE(String topLevelBIE) {
        sendKeys(getTopLevelBIEField(), topLevelBIE);
    }

    @Override
    public WebElement getTypeField() {
        return visibilityOfElementLocated(getDriver(), TYPE_SELECT_FIELD_LOCATOR);
    }

    @Override
    public void setType(String bieType) {
        click(getUpdaterSelectField());
        sendKeys(visibilityOfElementLocated(getDriver(), DROPDOWN_SEARCH_FIELD_LOCATOR), bieType);
        WebElement searchedSelectField = visibilityOfElementLocated(getDriver(),
                By.xpath("//mat-option//span[contains(text(), \"" + bieType + "\")]"));
        click(searchedSelectField);
        escape(getDriver());
    }

    @Override
    public WebElement getBusinessContextField() {
        return visibilityOfElementLocated(getDriver(), BUSINESS_CONTEXT_FIELD_LOCATOR);
    }

    @Override
    public void setBusinessContext(String businessContext) {
        sendKeys(getBusinessContextField(), businessContext);
    }

    @Override
    public String getBusinessContextFieldText() {
        return getText(getBusinessContextField());
    }

    @Override
    public WebElement getUpdatedStartDateField() {
        return visibilityOfElementLocated(getDriver(), UPDATED_START_DATE_FIELD_LOCATOR);
    }

    @Override
    public void setUpdatedStartDate(LocalDateTime updatedStartDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        sendKeys(getUpdatedStartDateField(), formatter.format(updatedStartDate));
    }

    @Override
    public WebElement getUpdatedEndDateField() {
        return visibilityOfElementLocated(getDriver(), UPDATED_END_DATE_FIELD_LOCATOR);
    }

    @Override
    public void setUpdatedEndDate(LocalDateTime updatedEndDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        sendKeys(getUpdatedEndDateField(), formatter.format(updatedEndDate));
    }

    @Override
    public WebElement getSearchButton() {
        return elementToBeClickable(getDriver(), SEARCH_BUTTON_LOCATOR);
    }
}

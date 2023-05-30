package org.oagi.score.e2e.impl.page.module;

import org.oagi.score.e2e.impl.page.BasePageImpl;
import org.oagi.score.e2e.page.module.CreateModuleSetPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.time.Duration.ofMillis;
import static org.oagi.score.e2e.impl.PageHelper.*;

public class CreateModuleSetPageImpl extends BasePageImpl implements CreateModuleSetPage {

    private static final By NAME_FIELD_LOCATOR =
            By.xpath("//mat-label[contains(text(), \"Name\")]//ancestor::div[1]/input");
    private static final By DESCRIPTION_FIELD_LOCATOR =
            By.xpath("//mat-label[contains(text(), \"Description\")]//ancestor::div[1]/textarea");
    private static final By CREATE_BUTTON_LOCATOR =
            By.xpath("//span[contains(text(), \"Create\")]//ancestor::button[1]");
    private static final By CREATE_MODULE_SET_RELEASE_CHECKBOX_LOCATOR =
            By.xpath("//span[contains(text(),\"Create Module Set Release\")]//ancestor::mat-checkbox[1]");

    private ViewEditModuleSetPageImpl parent;

    public CreateModuleSetPageImpl(ViewEditModuleSetPageImpl parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    protected String getPageUrl() {
        return getConfig().getBaseUrl().resolve("/module_management/module_set/create").toString();
    }

    @Override
    public void openPage() {
        String url = getPageUrl();
        getDriver().get(url);
        assert "Create Module Set".equals(getText(getTitle()));
    }

    @Override
    public WebElement getTitle() {
        return visibilityOfElementLocated(getDriver(), By.className("mat-card-title"));
    }

    @Override
    public WebElement getNameField() {
        return visibilityOfElementLocated(getDriver(), NAME_FIELD_LOCATOR);
    }

    @Override
    public void setName(String name) {
        sendKeys(getNameField(), name);
    }

    @Override
    public WebElement getDescriptionField() {
        return visibilityOfElementLocated(getDriver(), DESCRIPTION_FIELD_LOCATOR);
    }

    @Override
    public void setDescription(String description) {
        sendKeys(getDescriptionField(), description);
    }

    @Override
    public WebElement getCreateModuleSetReleaseCheckbox() {
        return visibilityOfElementLocated(getDriver(), CREATE_MODULE_SET_RELEASE_CHECKBOX_LOCATOR);
    }

    @Override
    public void hitCreateButton() {
        retry(() -> {
            click(getCreateButton());
            waitFor(ofMillis(1000L));
        });
        invisibilityOfLoadingContainerElement(getDriver());
    }

    @Override
    public WebElement getCreateButton() {
        return visibilityOfElementLocated(getDriver(), CREATE_BUTTON_LOCATOR);
    }
}

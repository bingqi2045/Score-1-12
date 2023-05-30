package org.oagi.score.e2e.impl.page.module;

import org.oagi.score.e2e.impl.page.BasePageImpl;
import org.oagi.score.e2e.page.module.EditModuleSetPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class EditModuleSetPageImpl extends BasePageImpl implements EditModuleSetPage {

    private static final By NAME_FIELD_LOCATOR =
            By.xpath("//mat-label[contains(text(), \"Name\")]//ancestor::div[1]/input");
    private static final By DESCRIPTION_FIELD_LOCATOR =
            By.xpath("//mat-label[contains(text(), \"Description\")]//ancestor::div[1]/textarea");
    private static final By CREATE_BUTTON_LOCATOR =
            By.xpath("//span[contains(text(), \"Create\")]//ancestor::button[1]");
    private static final By CREATE_MODULE_SET_RELEASE_CHECKBOX_LOCATOR =
            By.xpath("//span[contains(text(),\"Create Module Set Release\")]//ancestor::mat-checkbox[1]");

    private final ViewEditModuleSetPageImpl parent;

    public EditModuleSetPageImpl(ViewEditModuleSetPageImpl parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    protected String getPageUrl() {
        return getConfig().getBaseUrl().resolve("/module_management/module_set/" ).toString();
    }

    @Override
    public void openPage() {

    }

    @Override
    public WebElement getTitle() {
        return null;
    }

    @Override
    public WebElement getNameField() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public WebElement getDescriptionField() {
        return null;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public WebElement getStandardCheckboxField() {
        return null;
    }

    @Override
    public void hitUpdateButton() {

    }

    @Override
    public WebElement getUpdateButton() {
        return null;
    }

    @Override
    public void hitAddButton() {

    }

    @Override
    public WebElement getAddButton() {
        return null;
    }
}

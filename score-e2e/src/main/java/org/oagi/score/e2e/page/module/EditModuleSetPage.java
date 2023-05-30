package org.oagi.score.e2e.page.module;

import org.oagi.score.e2e.page.Page;
import org.openqa.selenium.WebElement;

public interface EditModuleSetPage extends Page {
    WebElement getNameField();

    void setName(String name);

    WebElement getDescriptionField();

    void setDescription(String description);

    WebElement getStandardCheckboxField();

    void hitUpdateButton();

    WebElement getUpdateButton();

    void hitAddButton();

    WebElement getAddButton();
}

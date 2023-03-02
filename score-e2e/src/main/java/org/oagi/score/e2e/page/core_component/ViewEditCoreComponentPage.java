package org.oagi.score.e2e.page.core_component;

import org.oagi.score.e2e.page.Page;
import org.openqa.selenium.WebElement;

/**
 * An interface of 'View/Edit Core Component' page.
 */
public interface ViewEditCoreComponentPage extends Page {

    /**
     * Return the UI element of the 'Branch' select field.
     *
     * @return the UI element of the 'Branch' select field
     */
    WebElement getBranchSelectField();

    /**
     * Set the 'Branch' select field.
     *
     * @param branch Branch text
     */
    void setBranch(String branch);

    /**
     * Return the UI element of the 'DEN' field.
     *
     * @return the UI element of the 'DEN' field
     */
    WebElement getDENField();

    /**
     * Return the 'DEN' field label text.
     *
     * @return the 'DEN' field label text
     */
    String getDENFieldLabel();

    /**
     * Set the 'DEN' field with given text.
     *
     * @param den DEN text
     */
    void setDEN(String den);

    /**
     * Return the UI element of the 'Module' field.
     *
     * @return the UI element of the 'Module' field
     */
    WebElement getModuleField();

    /**
     * Return the 'Module' field label text.
     *
     * @return the 'Module' field label text
     */
    String getModuleFieldLabel();

    /**
     * Set the 'Module' field with given text.
     *
     * @param module Module text
     */
    void setModule(String module);

    /**
     * Return the UI element of the 'Search' button.
     *
     * @return the UI element of the 'Search' button
     */
    WebElement getSearchButton();

    /**
     * Open the page of the ACC filtered by `den` and `branch`.
     *
     * @param den DEN text
     * @param branch Branch text
     * @return the ACC page object
     */
    ACCViewEditPage openACCViewEditPageByDenAndBranch(String den, String branch);

    /**
     * Open the page of the ASCCP filtered by `den` and `branch`.
     *
     * @param den DEN text
     * @param branch Branch text
     * @return the ASCCP page object
     */
    ASCCPViewEditPage openASCCPViewEditPageByDenAndBranch(String den, String branch);

    /**
     * Open the page of the BCCP filtered by `den` and `branch`.
     *
     * @param den DEN text
     * @param branch Branch text
     * @return the BCCP page object
     */
    BCCPViewEditPage openBCCPViewEditPageByDenAndBranch(String den, String branch);

    /**
     * Open the page of the DT filtered by `den` and `branch`.
     *
     * @param den DEN text
     * @param branch Branch text
     * @return the DT page object
     */
    DTViewEditPage openDTViewEditPageByDenAndBranch(String den, String branch);

    /**
     * Return the UI element of the table record at the given index, which starts from 1.
     *
     * @param idx The index of the table record.
     * @return the UI element of the table record at the given index
     */
    WebElement getTableRecordAtIndex(int idx);

    /**
     * Return the UI element of the table record containing the given value.
     *
     * @param value value
     * @return the UI element of the table record
     */
    WebElement getTableRecordByValue(String value);

    /**
     * Return the UI element of the column of the given table record with the column name.
     *
     * @param tableRecord the table record
     * @param columnName  the column name
     * @return the UI element of the column
     */
    WebElement getColumnByName(WebElement tableRecord, String columnName);

    /**
     * Return the number of only Core Components by state
     *
     * @param state the Core Component state: WIP, QA or Production
     * @return the quantity of Only Core Components by state
     */
    int getNumberOfOnlyCCsPerStateAreListed(String state);

    /**
     * Return a unique table record based on the Core Component name and the owner
     * @param name  the Core Component name
     * @param owner
     * @return a single table record based on the Core Component name and the owner
     */
    WebElement getTableRecordByCCNameAndOwner(String name, String owner);

    /**
     * Set the size of items to the 'Items per page' select field.
     *
     * @param items the size of items; 10, 25, 50
     */
    void setItemsPerPage(int items);

}

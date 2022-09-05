package model;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MyModelTest {
    // delete or rename this class!

    @Test
    public void SpendingEntryTest() {
        double amount = 10.00;

        // some random time from internet
        // https://currentmillis.com/
        Date date = new Date();
        date.setTime(1634007872723L);

        String category = "TestCategory";
        String subCategory = "TestSubCategory";

        SpendingEntry spendingEntryTestObject = new SpendingEntry(amount, date, category, subCategory);

        // test json output
        JSONObject spendingEntryJsonObject = spendingEntryTestObject.toJson();
        // must ensure we have more than 1 item in the spending entry object
        assertFalse(spendingEntryJsonObject.isEmpty());

        // make sure unique id is not zero
        assertNotEquals(spendingEntryTestObject.getUniqueId(), 0);

        assertEquals(amount, spendingEntryTestObject.getAmount());
        assertEquals(date, spendingEntryTestObject.getDate());
        assertEquals(category, spendingEntryTestObject.getCategory());
        assertEquals(subCategory, spendingEntryTestObject.getSubCategory());

        try {
            // make sure the toString method did not crash or throw exceptions
            spendingEntryTestObject.toString();

            // if print successfully, then we can assume the print method is good
            // and data is also good which tested previously
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false); // print failed, threw exception
        }
    }

    @Test
    public void ExpenseAccountTest() {
        String expenseAccountName = "TestAccount";
        ExpenseAccount expenseAccountTestObject = new ExpenseAccount(expenseAccountName);

        // ensure we properly created the expenseAccount with matching account name
        assertEquals(expenseAccountName, expenseAccountTestObject.getAccountName());

        // check if expense account size is zero
        assertEquals(expenseAccountTestObject.getNumEntries(), 0);

        // expenseAccount should always return null if we request any item index less than 1
        assertEquals(expenseAccountTestObject.getEntryByPosition(0), null);
        assertEquals(expenseAccountTestObject.removeEntryByPosition(0), null);

        // since we have nothing in the expenseAccount, therefore if we get the item #1
        // it should return null
        assertEquals(expenseAccountTestObject.getEntryByPosition(1), null);
        assertEquals(expenseAccountTestObject.removeEntryByPosition(1), null);

        // create a test SpendingEntry and add to expenseAccount
        SpendingEntry testEntry = new SpendingEntry(10.0, new Date(),
                "Cat", "SubCat");

        // test if we added the entry successfully
        assertTrue(expenseAccountTestObject.addExpenseEntry(testEntry));

        // search for category = "Cat", result should be one
        assertEquals(1, expenseAccountTestObject.searchByCategory("Cat").size());

        // search for category = "Cat1", result should be zero
        assertEquals(0, expenseAccountTestObject.searchByCategory("Cat1").size());

        // search for sub-category = "SubCat", result should be one
        assertEquals(1, expenseAccountTestObject.searchBySubCategory("SubCat").size());

        // search for sub-category = "SubCat1", result should be zero
        assertEquals(0, expenseAccountTestObject.searchBySubCategory("SubCat1").size());

        // now we have 1 entry in the list, therefore when we get position 1 should not be null
        assertNotEquals(expenseAccountTestObject.getEntryByPosition(1), null);

        // test json generate
        JSONObject jsonObject = expenseAccountTestObject.toJson();
        // must ensure we have more than 1 item in the spending entry object
        assertFalse(jsonObject.isEmpty());

        // now remove the 1st entry and should not be null
        assertNotEquals(expenseAccountTestObject.removeEntryByPosition(1), null);

    }
}

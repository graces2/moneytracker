package persistence;

import model.ExpenseAccount;
import model.SpendingEntry;
import org.junit.jupiter.api.Test;
import ui.TransactionService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JsonReadWriteTest {
    //NOTE TO CPSC 210 STUDENTS: the strategy in designing tests for the JsonWriter is to
    //write data to a file and then use the reader to read it back in and check that we
    //read in a copy of what was written out.

    @Test
    void testWriterInvalidFile() {
        try {
            ExpenseAccount expenseAccount = new ExpenseAccount("Test");
            JsonWriter writer = new JsonWriter();
            // use the illegal file name from the class JsonWriterTest example
            writer.write(expenseAccount, "./data/my\0illegal:fileName.json");
            fail("IOException was expected");
        } catch (FileNotFoundException e) {
            // pass
        }
    }

    // generate a SpendingEntry for testing
    SpendingEntry generateNewEntry(double amount, String date, String category, String subCategory) {
        Date formattedDate;
        try {
            formattedDate = TransactionService.DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            return null;
        }
        return new SpendingEntry(amount, formattedDate, category, subCategory);
    }

    @Test
    void testJsonReadWrite() {
        String expenseAccountName = "testAccountObject";
        String dataFileName = "./data/testJsonOutput.json";
        ExpenseAccount expenseAccount = new ExpenseAccount(expenseAccountName);
        expenseAccount.addExpenseEntry(generateNewEntry(1.0, "2021/1/1", "food", "lunch"));
        expenseAccount.addExpenseEntry(generateNewEntry(2.0, "2021/2/2", "drink", "dinner"));
        JsonWriter writer = new JsonWriter();
        try {
            writer.write(expenseAccount, dataFileName);
        } catch (FileNotFoundException e) {
            fail("failed to write to test file");
            return;
        }
        JsonReader reader = new JsonReader();
        ExpenseAccount accountFromFile;

        // read from a random file that does not exist
        try {
            accountFromFile = reader.read("./data/makeSureThisFileDoesNotExist.json");
            fail("file does not exist");
        } catch (IOException e) {
            // passes
        }

        // now read from the actual file
        try {
            accountFromFile = reader.read(dataFileName);
            // passed, since we are reading from the json file
        } catch (IOException e) {
            fail("file should exist");
            accountFromFile = null;
        }

        assertNotNull(accountFromFile);
        assertEquals(accountFromFile.getAccountName(), expenseAccountName);
        assertEquals(accountFromFile.getNumEntries(), 2);

        // get the item we put int
        SpendingEntry entry = accountFromFile.getEntryByPosition(1);
        assertNotNull(entry);
        assertEquals(1.0D, entry.getAmount());
    }
}

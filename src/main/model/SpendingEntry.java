package model;

import org.json.JSONObject;
import persistence.Writable;
import ui.TransactionService;

import java.util.Date;

public class SpendingEntry implements Writable {
    private static int globalUniqueIdCounter = 1;

    private final int uniqueId;
    private final double amount;
    private final Date date;
    private final String category;
    private final String subCategory;

    // REQUIRES: the expense information: amount, date, category, and sub-category
    // MODIFIES: this
    // EFFECTS: initializes expense values
    public SpendingEntry(double expenseAmount, Date expenseDate, String expenseCategory, String expenseSubCategory) {
        uniqueId = globalUniqueIdCounter;
        globalUniqueIdCounter++;
        amount = expenseAmount;
        date = expenseDate;
        category = expenseCategory;
        subCategory = expenseSubCategory;
        EventLog.getInstance().logEvent(new Event("Created new SpendingEntry: " + this));
    }

    // EFFECTS: returns the unique transaction ID
    public int getUniqueId() {
        return uniqueId;
    }

    // EFFECTS: returns the transaction amount
    public double getAmount() {
        return amount;
    }

    // EFFECTS: returns the date of the transaction
    public Date getDate() {
        return date;
    }

    // EFFECTS: returns the expense category
    public String getCategory() {
        return category;
    }

    // EFFECTS: returns the expense sub-category info
    public String getSubCategory() {
        return subCategory;
    }

    // EFFECTS: returns the string representation of this class
    @Override
    public String toString() {
        // the override and formatting of the SpendingEntry object is taken
        // from CPSC210 bank project Account.toString() method

        return String.format("%s\t$%.2f\t%s\t%s",
                TransactionService.DATE_FORMAT.format(date), amount, category, subCategory);
    }

    // EFFECTS: return a JSONObject representing a SpendingEntry
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        // store the date object as a string, similar to how user inputs the date yyyy/MM/dd
        jsonObject.put("Date", TransactionService.DATE_FORMAT.format(date));
        // store amount as double
        jsonObject.put("Amount", amount);
        jsonObject.put("Category", category);
        jsonObject.put("Subcategory", subCategory);

        return jsonObject;
    }
}

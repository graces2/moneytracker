package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;

public class ExpenseAccount implements Writable {
    private final String accountName;
    private final ArrayList<SpendingEntry> entries;
    private final EventLog logger;


    // REQUIRES: account name
    // MODIFIES: this
    // EFFECTS: initializes a new ExpenseAccount object
    public ExpenseAccount(String expenseAccountName) {
        logger = EventLog.getInstance();
        accountName = expenseAccountName;
        entries = new ArrayList<SpendingEntry>();
    }

    // REQUIRES: a new expense entry
    // MODIFIES: the expense list
    // EFFECTS: appends the new expense entry to the end of the list
    // returns true when successful
    public boolean addExpenseEntry(SpendingEntry newEntry) {
        entries.add(newEntry);
        logger.logEvent(new Event("ExpenseAccount added " + newEntry.toString()));
        return true;
    }

    // EFFECTS: returns the name of this expense account
    public String getAccountName() {
        return accountName;
    }

    // EFFECTS: prints the number of expense entries
    public int getNumEntries() {
        return entries.size();
    }

    // REQUIRES: category String to search
    // EFFECTS: returns a list with search results
    public ArrayList<SpendingEntry> searchByCategory(String searchString) {
        ArrayList<SpendingEntry> results = new ArrayList<>();
        int index = 0;
        while (index < entries.size()) {
            SpendingEntry entry = entries.get(index);
            if (entry.getCategory().equals(searchString)) {
                results.add(entry);
                index++;
            } else { // do nothing, continue
                index++;
            }
        }
        logger.logEvent(new Event("ExpenseAccount search Category for " + searchString + " yield "
                + results.size() + " results"));
        return results;
    }

    // REQUIRES: sub-category String to search
    // EFFECTS: returns a list with search results
    public ArrayList<SpendingEntry> searchBySubCategory(String searchString) {
        ArrayList<SpendingEntry> results = new ArrayList<>();
        int index = 0;
        while (index < entries.size()) {
            SpendingEntry entry = entries.get(index);
            if (entry.getSubCategory().equals(searchString)) {
                results.add(entry);
                index++;
            } else { // do nothing, continue
                index++;
            }
        }
        logger.logEvent(new Event("ExpenseAccount search Sub-Category for " + searchString + " yield "
                + results.size() + " results"));
        return results;
    }

    // REQUIRES: entry position (1-based)
    // EFFECTS: returns the SpendingEntry specified by the position
    // if the specified position is less than 1 and greater than the
    // number of entries then returns null
    public SpendingEntry getEntryByPosition(int position) {
        if (position <= 0) { // position must start with 1
            logger.logEvent(new Event("Invalid SpendingEntry get position <0"));
            return null;
        } else if (position > entries.size()) {
            logger.logEvent(new Event("Invalid SpendingEntry get position >Size"));
            return null;
        } else {
            // since it is 1-based, so the actual index is one less than the
            // specified position
            SpendingEntry returnValue = entries.get(position - 1);
            logger.logEvent(new Event("Get SpendingEntry at position " + position + " returned "
                    + returnValue.toString()));
            return returnValue;
        }
    }

    // REQUIRES: entry position (1-based)
    // MODIFIES: the expense list
    // EFFECTS: removes the SpendingEntry specified by the position
    // if the specified position is invalid returns null. If success returns the
    // entry removed
    public SpendingEntry removeEntryByPosition(int position) {
        if (position <= 0) { // position must start with 1
            logger.logEvent(new Event("Invalid SpendingEntry remove position <0"));
            return null;
        } else if (position > entries.size()) {
            logger.logEvent(new Event("Invalid SpendingEntry remove position >Size"));
            return null;
        } else {
            // since it is 1-based, so the actual index is one less than the
            // specified position
            SpendingEntry returnValue = entries.remove(position - 1);
            logger.logEvent(new Event("Remove SpendingEntry at position " + position + " returned "
                    + returnValue.toString()));
            return returnValue;
        }
    }

    // EFFECTS: returns the jsonObject representation of the ExpenseAccount class
    @Override
    public JSONObject toJson() {
        // add each spending entry to jsonArray
        JSONArray listOfSpendingEntries = new JSONArray();
        for (SpendingEntry entry : entries) {
            // add each spending entry json object
            listOfSpendingEntries.put(entry.toJson());
        }
        // create the return jsonObject
        JSONObject jsonObject = new JSONObject();
        // add the name of this account
        jsonObject.put("Name", accountName);
        // put all spending entries under the key "Entries"
        jsonObject.put("Entries", listOfSpendingEntries);
        logger.logEvent(new Event("SpendingEntry toJson of " + entries.size() + " items"));
        return jsonObject;
    }
}

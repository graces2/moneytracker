package persistence;

import model.ExpenseAccount;
import model.SpendingEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.TransactionService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

// The original code for JsonReader was obtained from CPSC210
// persistence example https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
// Sections of the code was modified, so they are suitable for my project

// Represents a reader that reads ExpenseAccount and SpendingEntries
// from JSON data stored in
public class JsonReader {

    // EFFECTS: constructs reader
    public JsonReader() {
    }

    // REQUIRES: file location to read ExpenseAccount data
    // EFFECTS: reads ExpenseAccount from file and returns it;
    public ExpenseAccount read(String source) throws IOException {
        String jsonData = readFile(source);
        // convert the file content to JSONObject
        JSONObject jsonObject = new JSONObject(jsonData);
        // parse the data
        return parseExpenseAccount(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    // this readFile() method is directly copied from
    // https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
    // JsonReader.readFile(String source) method
    // Using the same code so it will read the file content into a String
    private String readFile(String source) throws IOException {

        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses ExpenseAccount from JSON object and returns it
    private ExpenseAccount parseExpenseAccount(JSONObject jsonObject) {
        String name = jsonObject.getString("Name");
        // create a new expense account
        ExpenseAccount expenseAccount = new ExpenseAccount(name);
        addExpenses(expenseAccount, jsonObject);
        return expenseAccount;
    }

    // MODIFIES: expenseAccount
    // EFFECTS: parses all expenses from JSON object and adds them to expenseAccount
    private void addExpenses(ExpenseAccount expenseAccount, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("Entries");
        // the array parsing code is from the class data persistence example
        for (Object json : jsonArray) {
            // cast the items in jsonArray from Object type to JSONObject type
            JSONObject jsonEntry = (JSONObject) json;
            addExpenseEntry(expenseAccount, jsonEntry);
        }
    }

    // MODIFIES: expenseAccount
    // EFFECTS: parses a single SpendingEntry from JSON object and adds it to expenseAccount
    private void addExpenseEntry(ExpenseAccount expenseAccount, JSONObject jsonObject) {
        // read each spending entry value
        String dateString = jsonObject.getString("Date");
        Date date = TransactionService.getDate(dateString);
        Double amount = jsonObject.getDouble("Amount");
        String category = jsonObject.getString("Category");
        String subcategory = jsonObject.getString("Subcategory");
        SpendingEntry entry = new SpendingEntry(amount, date, category, subcategory);
        expenseAccount.addExpenseEntry(entry);
    }

}

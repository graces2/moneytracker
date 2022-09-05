package ui;

import model.ExpenseAccount;
import model.SpendingEntry;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class TransactionService {
    // input date formatter
    // the date format example taken from
    // http://tutorials.jenkov.com/java-date-time/parsing-formatting-dates.html
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    // the file to store the persistent data
    public static final String OUTPUT_FILE = "./data/ExpenseData.json";
    //public static final String OUTPUT_FILE = "./data/ExpenseData.txt";

    // partial code in this TransactionService class are modeled after CPSC210 Transaction class
    // bank.ui.TellerApp class example

    Scanner input;   // get inputs from user
    ExpenseAccount account; // the account that holds all the transactions

    // MODIFIES: this
    // EFFECTS: runs the money transaction service
    public TransactionService() {
        input = new Scanner(System.in);
        account = new ExpenseAccount("My Expense Account");
        runTransactionInput();
    }

    // MODIFIES: expense account
    // EFFECTS: processes user input and adds transactions to expense account
    private void runTransactionInput() {
        // this runTransactionInput method is take and modeled from CPSC210
        // "Transaction" class example

        boolean keepGoing = true;
        String command = null;

        while (keepGoing) {
            displayMenu();
            command = input.next(); // get user input as String
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }
        // close the input resources
        input.close();
        System.out.println("\nGoodbye!");
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {
        // this method models after the CPSC210 Transaction class
        // displayMenu() method

        System.out.println("\nExpense Manager: Select from following commands:");
        System.out.println("\tn -> enter new expense");
        System.out.println("\tr -> remove an expense");
        System.out.println("\tc -> search by category");
        System.out.println("\ts -> search by sub-category");
        System.out.println("\tp -> print all entries");
        System.out.println("\tw -> write data to file");
        System.out.println("\tl -> load data from file");
        System.out.println("\tq -> quit\n");
    }

    // EFFECTS: processes and validates user command
    private void processCommand(String command) {
        // this method models after the CPSC210 Transaction class
        // processCommand() method

        if (command.equals("n")) {
            addNewExpenseLine();
        } else if (command.equals("p")) {
            printExpenseAccount();
        } else if (command.equals("r")) {
            removeByIndex();
        } else if (command.equals("c")) {
            searchByCategory();
        } else if (command.equals("s")) {
            searchBySubCategory();
        } else if (command.equals("w")) {
            writeDataToFile();
        } else if (command.equals("l")) {
            loadDataFromFile();
        } else { // invalid command
            System.out.println("Selection not valid...");
        }
    }

    // EFFECTS: writes the SpendingEntries to file for data persistence
    private void writeDataToFile() {
        JsonWriter writer = new JsonWriter();
        try {
            writer.write(account, OUTPUT_FILE);
            System.out.println("Write successful to " + OUTPUT_FILE);
        } catch (FileNotFoundException e) {
            // print the error exception messages
            e.printStackTrace();
            System.out.println("Write to file failed");
        }
    }

    // MODIFIES: ExpenseAccount object
    // EFFECTS: load data from file and create a new ExpenseAccount object
    // with the loaded information
    private void loadDataFromFile() {
        JsonReader reader = new JsonReader();
        ExpenseAccount readAccount;
        try {
            readAccount = reader.read(OUTPUT_FILE);
        } catch (IOException e) {
            readAccount = null;
        }
        if (readAccount != null) {
            // read successful, change the current account to the read account
            account = readAccount;
            System.out.println("Loaded data from file");
            // print it
            printExpenseAccount();
        } else { // read failed
            System.out.println("failed to read data from file");
        }
    }

    // MODIFIES: expenseAccount
    // EFFECTS: removes the entry by index
    private void removeByIndex() {
        System.out.println("Enter spending index to be removed: ");
        int removeIndex = input.nextInt(); // get input as integer
        SpendingEntry removedEntry = account.removeEntryByPosition(removeIndex);
        if (removedEntry == null) {
            System.out.println("Removal of index " + removeIndex + " failed");
        } else {
            System.out.println("Removed expense entry at index " + removeIndex);
        }
    }

    // EFFECTS: prints all expense items matching the search criteria
    private void searchByCategory() {
        System.out.print("Enter category:");
        String target = input.next(); // get category from user
        ArrayList<SpendingEntry> results = account.searchByCategory(target);
        // the returned SpendingEntry matching the category will be in an arraylist
        System.out.println("Total results = " + results.size());
        // print every SpendingEntry in the results arraylist
        int counter = 0;
        SpendingEntry entry;
        while (counter < results.size()) {
            entry = results.get(counter);
            System.out.println("\t" + entry.toString());
            counter++;
        }
    }

    // EFFECTS: prints all expense items matching the search criteria
    private void searchBySubCategory() {
        System.out.print("Enter sub-category:");
        String target = input.next(); // get category from user
        ArrayList<SpendingEntry> results = account.searchBySubCategory(target);

        System.out.println("Total results = " + results.size());
        int counter = 0;
        SpendingEntry entry;
        while (counter < results.size()) {
            entry = results.get(counter); // position is 1 based
            System.out.println("\t" + entry.toString());
            counter++;
        }
    }

    // MODIFIES: expenseAccount
    // EFFECTS: starts an expense entry transaction and get the necessary data from
    // user.
    private void addNewExpenseLine() {
        System.out.print("Enter expense amount: $");
        double amount = input.nextDouble(); // get user input as double

        if (amount < 0.0) { // get the amount
            System.out.println("Expense amount must be greater than $0.00...\n");
            return; // just return, since failed to get necessary parameter
        }
        System.out.print("Enter expense date (yyyy/MM/dd): ");
        String dateEntered = input.next(); // get user input as String
        Date date = getDate(dateEntered);

        if (date == null) {
            System.out.println("Invalid date format");
            return; // get date failed, so just return
        }
        System.out.print("Enter expense category: ");
        String category = input.next();

        System.out.print("Enter expense sub-category: ");
        String subCategory = input.next();

        // now we have everything we need to generate an entry
        SpendingEntry newEntry = new SpendingEntry(amount, date, category, subCategory);

        // now add the entry to expense account
        if (account.addExpenseEntry(newEntry) == true) {
            System.out.println("Successfully added new entry");
        } else {
            System.out.println("Expense entered failed");
        }
    }

    // REQUIRES: the date string in the format of yyyy/MM/dd
    // EFFECTS: Returns java.util.Date object if successfully parse the input
    // string. Otherwise, returns null when failed
    public static Date getDate(String dateFormat) {
        // date parsing example taken from
        // http://tutorials.jenkov.com/java-date-time/parsing-formatting-dates.html
        try {
            return DATE_FORMAT.parse(dateFormat);
        } catch (ParseException e) {
            return null;
        }
    }

    // EFFECTS: prints the expense account information
    private void printExpenseAccount() {
        int counter = 1;
        int numEntries = account.getNumEntries();

        System.out.print("\nEntries for expense account " + account.getAccountName());
        System.out.print(".  Total entries = " + numEntries);
        System.out.print("\n");

        SpendingEntry entry;

        while (counter <= numEntries) {
            entry = account.getEntryByPosition(counter);
            System.out.println("\t" + counter + "\t" + entry.toString());
            counter++;
        }
    }
}

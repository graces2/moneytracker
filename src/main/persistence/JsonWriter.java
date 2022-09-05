package persistence;

import model.ExpenseAccount;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// The original code for JsonReader was obtained from CPSC210
// persistence example https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
// Most of the code remains the same as the persistence example, only changed
// from WorkRoom to ExpenseAccount for my needs and combining write into a single method
// The class structure and methods are all from the example indicated above

// Represents a writer that writes JSON representation of workroom to file
public class JsonWriter {
    private static final int TAB = 4;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter() {
    }

    // Combining everything in this write() method
    // REQUIRES: the ExpenseAccount to write and the file name
    // EFFECTS: writes JSON representation of ExpenseAccount to file
    public void write(ExpenseAccount expenseAccount, String destination) throws FileNotFoundException {
        // the File creation and PrintWriter are taken from the class persistence example
        File destinationFile = new File(destination);
        PrintWriter writer = new PrintWriter(destinationFile);
        JSONObject json = expenseAccount.toJson();
        // set the tab size
        writer.print(json.toString(TAB));
        writer.close(); // close the output file
    }
}

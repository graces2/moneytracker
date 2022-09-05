package ui;

import model.ExpenseAccount;
import model.SpendingEntry;

import javax.swing.table.DefaultTableModel;

// table example from https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
// in combination of
// https://www.wikitechy.com/tutorials/java/java-defaulttablemodel
// to form my ExpenseAccountDataModel
public class ExpenseAccountDataModel extends DefaultTableModel {
    public ExpenseAccountDataModel() {
        super(new Object[]{
                "Date",
                "Amount",
                "Category",
                "Sub-Category"}, 0);
    }

    // example from https://stackoverflow.com/questions/9919230/disable-user-edit-in-jtable
    // EFFECTS: disable editing cells from the GUI
    // REQUIRES: any row, any column
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    // EFFECTS: reloads the JTable's data model with provided ExpenseAccount info
    // REQUIRES: ExpenseAccount to be displayed on the GUI
    public void refreshData(ExpenseAccount expenseAccount) {
        this.removeAllRows();
        // the expense account is 1-based
        for (int i = 1; i <= expenseAccount.getNumEntries(); i++) {
            SpendingEntry entry = expenseAccount.getEntryByPosition(i);
            Object[] data = new String[4];
            data[0] = TransactionService.DATE_FORMAT.format(entry.getDate());
            data[1] = String.format("%.2f", entry.getAmount());
            data[2] = entry.getCategory();
            data[3] = entry.getSubCategory();
            super.addRow(data);
        }
    }

    // EFFECTS: remove all data from JTable's data model, so it will display nothing
    public void removeAllRows() {
        while (super.getRowCount() > 0) {
            super.removeRow(0);
        }
    }
}
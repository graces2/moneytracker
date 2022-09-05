package ui;

import model.Event;
import model.EventLog;
import model.ExpenseAccount;
import model.SpendingEntry;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

/**
 * The TransactionGui class is modeled after the LabelChanger example
 * from https://stackoverflow.com/questions/6578205/swing-jlabel-text-change-on-the-running-application
 * Sections are changed to meet my needs
 */
public class TransactionServiceGui extends JFrame implements ActionListener {

    ExpenseAccount account;
    ExpenseAccountDataModel dataModel;
    JTable dataTable;

    JPanel panel;
    BufferedImage loadSuccess;
    BufferedImage loadFailed;
    BufferedImage saveSuccess;
    BufferedImage saveFailed;
    EventLog logger;

    // EFFECTS: creates all visual components
    // MODIFIEDS: this, ExpenseAccount
    public TransactionServiceGui() {
        // setup event logging
        // get the singleton event logger
        logger = EventLog.getInstance();

        this.setTitle("Money Manager");
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        account = new ExpenseAccount("My Expense Account");
        buildButtons();     // create the buttons on the top
        buildDataTable();   // create the data table
        buildJPanel();      // create the drawing JPanel
        loadImages();       // load the images to show on the JPanel

        this.setLocationRelativeTo(null);
        this.setSize(530, 360);
        this.setResizable(false);
        this.setVisible(true);

        // window closing code from
        // https://stackoverflow.com/questions/9093448/how-to-capture-a-jframes-close-button-click-event
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                printLoggerToConsole();
            }
        });
    }


    // EFFECTS: prints all logged events to System.out
    private void printLoggerToConsole() {
        System.out.println("-- START OF LOGGED EVENTS --");
        // use the printLog() example from Alarm project ScreenPrinter.printLog() method
        for (Event event : logger) {
            System.out.println(event.toString());
        }
        System.out.println("-- END OF LOGGED EVENTS --");
    }

    // image loading example from
    // https://stackoverflow.com/questions/17865465/how-do-i-draw-an-image-to-a-jpanel-or-jframe
    // EFFECTS: loads the image files to show load success/fail and save success/fail
    // MODIFIES: this
    private void loadImages() {
        try {
            loadSuccess = ImageIO.read(new File("./data/load_good.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            loadFailed = ImageIO.read(new File("./data/load_bad.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            saveSuccess = ImageIO.read(new File("./data/save_good.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            saveFailed = ImageIO.read(new File("./data/save_bad.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // https://stackoverflow.com/questions/1594423/setting-the-size-of-panels
    // showing how to resize JPanel
    // EFFECTS: build a JPanel to draw the images
    // MODIFIES: this
    private void buildJPanel() {
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 71));
        this.add(panel);
    }

    // REQUIRES: the category to be searched
    // EFFECTS: display a message showing the number of items of same category in ExpenseAccount
    private void displaySameCategory(String category) {
        JOptionPane.showMessageDialog(this,
                String.format("There are %d items in %s\n  with the category of %s",
                        account.searchByCategory(category).size(), account.getAccountName(), category));
    }

    // REQUIRES: the sub-category to be searched
    // EFFECTS: display a message showing the number of items of same Sub-caategory in ExpenseAccount
    private void displaySameSubCategory(String subCategory) {
        JOptionPane.showMessageDialog(this,
                String.format("There are %d items in %s\n  with the sub-category of %s",
                        account.searchBySubCategory(subCategory).size(), account.getAccountName(), subCategory));
    }

    // EFFECTS: edits a cell (remove and add a new SpendingEntry)  prompt the user for new values,
    // and display the previous value
    private void editRow(int row) {
        // ok, get the existing SpendingEntry
        SpendingEntry se = account.getEntryByPosition(row + 1); // 1-based indexing
        Date date = getDateFromUser(TransactionService.DATE_FORMAT.format(se.getDate()));
        if (date == null) {
            return;
        }
        Float amount = getAmountFromUser(String.valueOf(se.getAmount()));
        if (amount == null) {
            return;
        }
        String category = JOptionPane.showInputDialog("Enter Category", se.getCategory());
        if (category == null) {
            return;
        }
        String subCategory = JOptionPane.showInputDialog("Enter Sub-category", se.getSubCategory());
        if (subCategory == null) {
            return;
        }
        // ok, create a new entry
        SpendingEntry newSe = new SpendingEntry(amount, date, category, subCategory);
        // delete the old entry
        account.removeEntryByPosition(row + 1);
        // add the new entry
        account.addExpenseEntry(newSe);
        // refresh
        dataModel.refreshData(account);
    }

    // EFFECTS: processes mouse-click event for double-left-clicks on the category or sub-category
    // columns (2 and 3) and determines which row and column of cell was clicked
    private void processMouseClick(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            if (dataTable.getSelectedColumn() == 0) { // date.. initiate edit feature
                editRow(dataTable.getSelectedRow());
            } else if (dataTable.getSelectedColumn() == 2) { // Category
                displaySameCategory((String) dataModel.getValueAt(dataTable.getSelectedRow(), 2));
            } else if (dataTable.getSelectedColumn() == 3) { // sub-category
                displaySameSubCategory((String) dataModel.getValueAt(dataTable.getSelectedRow(), 3));
            }
        }
    }

    // EFFECTS: adds mouse listener for DataTable.  Only listening for mouseClicked event and send the event
    // to processMouseClick method for processing
    private void addMouseListener() {
        dataTable.addMouseListener(new MouseListener() {
            // EFFECTS: gets the data cel when user double-clicks on the cell
            @Override
            public void mouseClicked(MouseEvent e) {
                processMouseClick(e);
            }

            // EFFECTS: none
            @Override
            public void mousePressed(MouseEvent e) {
            }

            // EFFECTS: none
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            // EFFECTS: none
            @Override
            public void mouseEntered(MouseEvent e) {
            }

            // EFFECTS: none
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    // EFFECTS: build the data table to display the SpendingEntry items in ExpenseAccount
    // MODIFIES: this
    private void buildDataTable() {
        // table example from:
        // https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
        // using the TableDemo() code as example
        dataModel = new ExpenseAccountDataModel();
        dataTable = new JTable(dataModel);
        dataTable.setFillsViewportHeight(true); // fills the entire space with data grid
        // https://www.tutorialspoint.com/how-can-we-sort-a-jtable-on-a-particular-column-in-java
        // dataTable.setAutoCreateRowSorter(true);
        addMouseListener();
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        this.add(scrollPane);
    }

    // EFFECTS: builds the buttons
    // MODIFIES: this
    private void buildButtons() {
        JButton buttonLoad = new JButton("Load");
        JButton buttonSave = new JButton("Save");
        JButton buttonNew = new JButton("New");
        JButton buttonDelete = new JButton("Delete");
        JButton buttonExit = new JButton("Exit");

        buttonLoad.addActionListener(this);
        buttonSave.addActionListener(this);
        buttonNew.addActionListener(this);
        buttonDelete.addActionListener(this);
        buttonExit.addActionListener(this);

        this.add(buttonLoad);
        this.add(buttonSave);
        this.add(buttonNew);
        this.add(buttonDelete);
        this.add(buttonExit);
    }

    // EFFECTS: this is the callback method for all button actions
    // depending on which button was pressed, the appropriate actions
    // would be called
    @Override
    public void actionPerformed(ActionEvent mouseAction) {
        switch (mouseAction.getActionCommand()) {
            case "Load":
                loadDataFromFile();
                break;
            case "Save":
                saveDataToFile();
                break;
            case "New":
                newEntry();
                break;
            case "Delete":
                deleteRowsCommand();
                break;
            case "Exit":
                // close JFrame code from
                // https://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe/1235994
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                break;
        }
    }

    // EFFECTS: prompts the user for SpendingEntry data entries and adds to expense account
    // MODIFIES: this, ExpenseAccount
    private void newEntry() {
        String today = TransactionService.DATE_FORMAT.format(Date.from(Instant.now()));
        Date date = getDateFromUser(today);
        if (date == null) {
            return;
        }
        Float floatAmount = getAmountFromUser(null);
        if (floatAmount == null) {
            return;
        }
        String category = JOptionPane.showInputDialog("Enter Category");
        if (category == null) {
            return;
        }
        String subCategory = JOptionPane.showInputDialog("Enter Sub-category");
        if (subCategory == null) {
            return;
        }
        SpendingEntry se = new SpendingEntry(floatAmount, date, category, subCategory);
        account.addExpenseEntry(se);
        dataModel.refreshData(account);
    }

    // EFFECTS: get the amount from user using pop-up dialog
    private Float getAmountFromUser(String defaultValue) {
        String amount = JOptionPane.showInputDialog("Enter Amount", defaultValue);
        if (amount == null) {
            return null;
        }
        try {
            return Float.parseFloat(amount);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    // EFFECTS: gets Date object from user using pop-up dialog
    private Date getDateFromUser(String defaultValue) {
        String date = JOptionPane.showInputDialog("Enter Date", defaultValue);
        if (date == null) { // user cancelled
            return null;
        }
        Date dateObject;
        try {
            dateObject = TransactionService.DATE_FORMAT.parse(date);
            return dateObject;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return null;
        }
    }

    // EFFECTS: draws the image onto the JPanel for display
    private void showImage(Image image) {
        if (image != null) {
            Graphics g = panel.getGraphics();
            g.drawImage(image, 0, 0, this);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            g.clearRect(0, 0, 500, 71);
        }
    }

    // the JOptionPane dialog are taken from
    // https://www.delftstack.com/howto/java/java-pop-up-window/
    // for display popup and input dialogs
    //
    // EFFECTS: loads ExpenseAccount data from file using json format
    // will display an image indicating the result of the load
    // MODIFIES: ExpenseAccount, this
    private void loadDataFromFile() {
        JsonReader reader = new JsonReader();
        ExpenseAccount readAccount;
        String userChoice = JOptionPane.showInputDialog("Load JSON File...", "./data/ExpenseData.json");
        if (userChoice != null) { // has valid user input
            try {
                readAccount = reader.read(userChoice);
                showImage(loadSuccess);
            } catch (IOException e) {
                readAccount = null;
                showImage(loadFailed);
            }
            if (readAccount != null) {
                // read successful, change the current account to the read account
                account = readAccount;
                // display
                dataModel.refreshData(account);
            } else { // read failed
                System.out.println("failed to read data from file");
            }
        }
    }

    // EFFECTS: writes the ExpenseAccount data to user specified JSON file
    // will display an image indicating the result of the save
    private void saveDataToFile() {
        String userChoice = JOptionPane.showInputDialog("Save JSON File to...", "./data/ExpenseData.json");
        if (userChoice != null) {
            JsonWriter writer = new JsonWriter();
            try {
                writer.write(account, userChoice);
                showImage(saveSuccess);
            } catch (FileNotFoundException e) {
                // print the error exception messages
                showImage(saveFailed);
                System.out.println("Write to file failed");
            }
        }
    }

    // EFFECTS: deletes the selected SpendingEntry items
    // MODIFIES: ExpenseAccount
    private void deleteRowsCommand() {
        int[] selectedRows = dataTable.getSelectedRows();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            account.removeEntryByPosition(selectedRows[i] + 1);
        }
        dataModel.refreshData(account);
    }
}
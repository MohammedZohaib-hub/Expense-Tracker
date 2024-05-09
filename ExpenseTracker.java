import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ExpenseTracker extends JFrame {
    private DefaultTableModel tableModel;
    private JTable expenseTable;
    private JTextField dateField, categoryField, amountField;
    private JButton addButton, sortButton, clearButton, categoryTotalButton;
    private JLabel balanceLabel;
    private JTextArea deductionHistoryArea;
    private double mainBalance = 5000; // Initial main balance
    private DecimalFormat df = new DecimalFormat("#.##");
    private ArrayList<Expense> expenses;
    private Map<String, Double> categoryTotal;

    public ExpenseTracker() {
        setTitle("Expense Tracker");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load expenses from file
        expenses = loadExpenses();
        if (expenses == null) {
            expenses = new ArrayList<>();
        }

        // Create table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Date");
        tableModel.addColumn("Category");
        tableModel.addColumn("Amount");
        expenseTable = new JTable(tableModel);

        // Create text fields
        dateField = new JTextField(10);
        categoryField = new JTextField(10);
        amountField = new JTextField(10);

        // Create buttons
        addButton = new JButton("Add Expense");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                String category = categoryField.getText();
                double amount = Double.parseDouble(amountField.getText());
                addExpense(date, category, amount);
            }
        });

        sortButton = new JButton("Sort Table");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortExpenseTable();
            }
        });

        clearButton = new JButton("Clear Expenses");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearExpenses();
            }
        });

        categoryTotalButton = new JButton("Category-wise Total");
        categoryTotalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayCategoryTotal();
            }
        });

        // Create balance label
        balanceLabel = new JLabel("Balance: $" + df.format(mainBalance));

        // Create deduction history area
        deductionHistoryArea = new JTextArea(5, 20);
        deductionHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(deductionHistoryArea);

        // Create panels
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(addButton);
        inputPanel.add(balanceLabel);
        inputPanel.add(sortButton);
        inputPanel.add(clearButton);
        inputPanel.add(categoryTotalButton);

        // Add components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(expenseTable), BorderLayout.CENTER);
        getContentPane().add(inputPanel, BorderLayout.SOUTH);
        getContentPane().add(scrollPane, BorderLayout.EAST);

        // Update table with existing expenses
        updateExpenseTable();

        // Calculate and display category-wise summation
        calculateCategoryTotal();
    }

    private void addExpense(String date, String category, double amount) {
        // Add expense to list
        Expense expense = new Expense(date, category, amount);
        expenses.add(expense);
        // Update table
        tableModel.addRow(new Object[]{date, category, amount});
        // Update deduction history area
        String deductionHistory = deductionHistoryArea.getText();
        deductionHistory += "Expense: $" + df.format(amount) + "\n";
        deductionHistoryArea.setText(deductionHistory);
        // Update balance label
        updateBalance(amount);
        // Update category-wise total
        calculateCategoryTotal();
        // Save expenses to file
        saveExpenses();
        // Save category-wise total to file
        saveCategoryWiseTotal();
    }

    private void updateBalance(double amount) {
        mainBalance -= amount;
        balanceLabel.setText("Balance: $" + df.format(mainBalance));
    }

    private ArrayList<Expense> loadExpenses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("expenses.dat"))) {
            return (ArrayList<Expense>) ois.readObject();
        } catch (FileNotFoundException e) {
            return null; // File not found, return null
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveExpenses() {
        // Sort expenses by date before saving
        expenses.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("expenses.dat"))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCategoryWiseTotal() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("category_total.txt"))) {
            for (Map.Entry<String, Double> entry : categoryTotal.entrySet()) {
                writer.println(entry.getKey() + ": $" + df.format(entry.getValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateExpenseTable() {
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{expense.getDate(), expense.getCategory(), expense.getAmount()});
        }
    }

    private void clearExpenses() {
        // Clear table
        tableModel.setRowCount(0);
        // Clear list
        expenses.clear();
        // Clear deduction history area
        deductionHistoryArea.setText("");
        // Reset balance
        mainBalance = 5000;
        balanceLabel.setText("Balance: $" + df.format(mainBalance));
        // Clear category-wise total
        categoryTotal.clear();
        // Save expenses to file (clear the file)
        saveExpenses();
        // Save category-wise total to file (clear the file)
        saveCategoryWiseTotal();
    }

    private void calculateCategoryTotal() {
        categoryTotal = new TreeMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();
            categoryTotal.put(category, categoryTotal.getOrDefault(category, 0.0) + amount);
        }
    }

    private void sortExpenseTable() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        expenseTable.setRowSorter(sorter);
        sorter.setSortKeys(null); // Clear any existing sort
        sorter.sort();
    }

    private void displayCategoryTotal() {
        StringBuilder message = new StringBuilder("Category-wise Total:\n");
        for (Map.Entry<String, Double> entry : categoryTotal.entrySet()) {
            message.append(entry.getKey()).append(": $").append(df.format(entry.getValue())).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Category-wise Total Expenses", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExpenseTracker().setVisible(true);
            }
        });
    }
}

class Expense implements Serializable {
    private String date;
    private String category;
    private double amount;

    public Expense(String date, String category, double amount) {
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}


**Expense Tracker**
Expense Tracker is a simple Java application designed to help users manage their expenses effectively. It provides a graphical user interface (GUI) for users to input their expenses, view a summary of expenses by category, and track their overall balance.

**Features**
Add Expense: Users can enter the date, category, and amount of an expense and add it to the list.
View Expenses: Expenses are displayed in a table format showing the date, category, and amount.
Sort Expenses: Users can sort the expense table based on category, date, or amount.
Clear Expenses: Users can clear all expenses from the list and reset the balance to its initial value.
Category-wise Total: Users can view a summary of expenses by category and see the total amount spent in each category.
Persistence: Expenses and category-wise totals are saved to files (expenses.dat and category_total.txt) for future reference.
**How to Use**
Run the Application: Compile and run the ExpenseTracker.java file.
Add Expenses: Enter the date, category, and amount of an expense, then click the "Add Expense" button.
View Expenses: The table displays all entered expenses, sorted by date by default.
Sort Expenses: Click the "Sort Table" button to sort expenses by category, date, or amount.
Clear Expenses: Click the "Clear Expenses" button to remove all expenses from the list.
View Category-wise Total: Click the "Category-wise Total" button to view the total expenses by category.
**File Structure**
ExpenseTracker.java: Main Java file containing the Expense Tracker application.
Expense.java: Class representing an expense object.
expenses.dat: Binary file storing the serialized list of expenses.
category_total.txt: Text file storing category-wise total expenses.
**Dependencies**
Java Development Kit (JDK): Ensure you have JDK installed to compile and run the application.
**Contributing**
Contributions are welcome! If you have any suggestions, bug fixes, or feature enhancements, feel free to open an issue or create a pull request.

License
This project is licensed under the MIT License.

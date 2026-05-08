import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// Expense Class
class Expense {
    String name, category, date;
    double amount;

    Expense(String n, double a, String c, String d) {
        name = n;
        amount = a;
        category = c;
        date = d;
    }
}

// AI Engine
class AIEngine {

    static String autoCategory(String name) {
        name = name.toLowerCase();

        if (name.contains("pizza") || name.contains("burger") || name.contains("food"))
            return "Food";
        else if (name.contains("game") || name.contains("pubg"))
            return "Gaming";
        else if (name.contains("movie") || name.contains("netflix"))
            return "Entertainment";
        else if (name.contains("uber") || name.contains("bus") || name.contains("train"))
            return "Traveling";
        else
            return "Other";
    }

    static double predictExpense(ArrayList<Expense> list) {
        if (list.size() == 0) return 0;

        double sum = 0;
        for (Expense e : list) sum += e.amount;

        return sum / list.size();
    }
}

// MAIN GUI
public class ExpenseAI extends JFrame implements ActionListener {

    JTextField nameField, amountField, dateField, categoryField;
    JTextArea outputArea;
    JTable table;
    DefaultTableModel model;

    JButton addBtn, showBtn, totalBtn, analyzeBtn;

    ArrayList<Expense> expenses = new ArrayList<>();

    JLabel totalLabel, predictionLabel;

    ExpenseAI() {

        setTitle("💡 AI Expense Intelligence System");
        setSize(900, 600);
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("AI Expense Intelligence System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(new Color(52, 152, 219));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // INPUT PANEL
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        nameField = new JTextField();
        amountField = new JTextField();
        dateField = new JTextField();
        categoryField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Category (optional):"));
        inputPanel.add(categoryField);

        addBtn = createButton("Add", new Color(46, 204, 113));
        showBtn = createButton("Show", new Color(155, 89, 182));
        totalBtn = createButton("Total", new Color(241, 196, 15));
        analyzeBtn = createButton("AI Analyze", new Color(231, 76, 60));

        inputPanel.add(addBtn);
        inputPanel.add(showBtn);
        inputPanel.add(totalBtn);
        inputPanel.add(analyzeBtn);

        add(inputPanel, BorderLayout.WEST);

        // TABLE
        model = new DefaultTableModel(new String[]{"Name","Amount","Category","Date"},0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // OUTPUT
        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);

        totalLabel = new JLabel("Total: ₹0");
        predictionLabel = new JLabel("Prediction: ₹0");

        JPanel bottomPanel = new JPanel(new GridLayout(3,1));
        bottomPanel.add(totalLabel);
        bottomPanel.add(predictionLabel);
        bottomPanel.add(new JScrollPane(outputArea));

        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        addBtn.addActionListener(this);
        showBtn.addActionListener(this);
        totalBtn.addActionListener(this);
        analyzeBtn.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    public void actionPerformed(ActionEvent e) {

        try {

            // ADD
            if (e.getSource() == addBtn) {
                String name = nameField.getText();
                double amt = Double.parseDouble(amountField.getText());
                String date = dateField.getText();

                String userCat = categoryField.getText().trim();

                String cat;
                if (userCat.isEmpty()) {
                    cat = AIEngine.autoCategory(name); // AI
                } else {
                    cat = userCat; // User
                }

                Expense exp = new Expense(name, amt, cat, date);
                expenses.add(exp);

                model.addRow(new Object[]{name, amt, cat, date});

                totalLabel.setText("Total: ₹" + getTotal());
                predictionLabel.setText("Prediction: ₹" + String.format("%.2f", AIEngine.predictExpense(expenses)));

                outputArea.setText("✅ Expense Added!");

                nameField.setText("");
                amountField.setText("");
                dateField.setText("");
                categoryField.setText("");
            }

            // SHOW
            else if (e.getSource() == showBtn) {
                outputArea.setText("");

                for (Expense ex : expenses) {
                    outputArea.append(ex.name + " | ₹" + ex.amount + " | " + ex.category + "\n");
                }
            }

            // TOTAL
            else if (e.getSource() == totalBtn) {
                outputArea.setText("💰 Total Expense: ₹" + getTotal());
            }

            // AI ANALYSIS
            else if (e.getSource() == analyzeBtn) {

                HashMap<String, Double> map = new HashMap<>();

                for (Expense ex : expenses) {
                    map.put(ex.category, map.getOrDefault(ex.category, 0.0) + ex.amount);
                }

                String maxCat = "";
                double max = 0;

                for (String key : map.keySet()) {
                    if (map.get(key) > max) {
                        max = map.get(key);
                        maxCat = key;
                    }
                }

                double avg = expenses.size() > 0 ? getTotal() / expenses.size() : 0;

                outputArea.setText(
                        "🤖 AI Analysis\n\n" +
                        "Highest Spending: " + maxCat + "\n" +
                        "Average Expense: ₹" + String.format("%.2f", avg) + "\n" +
                        "Suggestion: Reduce " + maxCat
                );
            }

        } catch (Exception ex) {
            outputArea.setText("⚠️ Invalid Input!");
        }
    }

    double getTotal() {
        double sum = 0;
        for (Expense e : expenses) sum += e.amount;
        return sum;
    }

    public static void main(String[] args) {
        new ExpenseAI();
    }
}

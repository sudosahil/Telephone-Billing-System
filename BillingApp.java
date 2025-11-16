import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class BillingApp extends JFrame {

    JTextField tfName, tfPhone, tfEmail, tfDuration, tfSMS;
    JComboBox<String> cbType;
    JTable table;
    DefaultTableModel model;
    JButton btnProcess, btnPay;

    Color airtelRed = new Color(228, 0, 43);
    Color bgGrey = new Color(240, 240, 245);

    public BillingApp() {
        setTitle("Airtel Enterprise Billing System");
        setSize(1100, 700); // Wider window for new columns
        setLayout(null);
        getContentPane().setBackground(bgGrey);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel header = new JPanel();
        header.setBackground(airtelRed);
        header.setBounds(0, 0, 1100, 60);
        JLabel title = new JLabel("Airtel CRM Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title);
        add(header);

        setupInputs();
        setupTable();
        refreshTable();

        setVisible(true);
    }

    private void setupInputs() {
        int x = 30, y = 100, gap = 50;

        addLabel("Customer Name:", x, y); tfName = addField(x, y + 25);
        addLabel("Phone Number:", x, y + gap); tfPhone = addField(x, y + gap + 25);
        addLabel("Email ID:", x, y + gap * 2); tfEmail = addField(x, y + gap * 2 + 25);

        // Right Side
        addLabel("Usage Type:", x + 180, y + gap);
        cbType = new JComboBox<>(new String[]{"Local", "STD", "ISD"});
        cbType.setBounds(x + 180, y + gap + 25, 150, 30);
        add(cbType);

        addLabel("Call Duration (Mins):", x + 180, y + gap * 2);
        tfDuration = addField(x + 180, y + gap * 2 + 25);

        addLabel("No. of SMS:", x + 180, y + gap * 3);
        tfSMS = addField(x + 180, y + gap * 3 + 25);

        btnProcess = new JButton("CALCULATE & SAVE");
        btnProcess.setBounds(x, 380, 330, 40);
        btnProcess.setBackground(airtelRed);
        btnProcess.setForeground(Color.WHITE);
        btnProcess.addActionListener(e -> processBill());
        add(btnProcess);

        btnPay = new JButton("CLEAR DUES");
        btnPay.setBounds(x, 430, 330, 40);
        btnPay.setBackground(new Color(39, 174, 96));
        btnPay.setForeground(Color.WHITE);
        btnPay.addActionListener(e -> processPayment());
        add(btnPay);
    }

    private void setupTable() {
        // NEW COLUMNS ADDED: Acc No, Date, Period
        String[] cols = {"Acc No", "Phone", "Name", "Calls", "SMS", "Dues (Rs)", "Date", "Period"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(25);

        // Status Color Logic based on Dues column (Index 5)
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSel, hasFoc, row, col);

                // Safely get dues (Index 5)
                try {
                    double dues = Double.parseDouble(table.getValueAt(row, 5).toString());
                    if (dues > 0) c.setForeground(Color.RED);
                    else c.setForeground(new Color(0, 100, 0));
                } catch (Exception e) { c.setForeground(Color.BLACK); }

                return c;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(400, 100, 650, 450);
        add(sp);
    }

    private void processBill() {
        try {
            String name = tfName.getText();
            String phone = tfPhone.getText();
            String email = tfEmail.getText();
            String type = cbType.getSelectedItem().toString();

            int mins = tfDuration.getText().isEmpty() ? 0 : Integer.parseInt(tfDuration.getText());
            int sms = tfSMS.getText().isEmpty() ? 0 : Integer.parseInt(tfSMS.getText());

            double callCost = BillingService.calculateCallCost(type, mins);
            double smsCost = BillingService.calculateSMSCost(type, sms);
            double total = callCost + smsCost;

            // Auto-Generate Billing Details
            String accNo = BillingService.generateAccountNo();
            String date = BillingService.getCurrentDate();
            String period = BillingService.getBillPeriod();

            int choice = JOptionPane.showConfirmDialog(this,
                    "Total Cost: " + total + "\nAccount: " + accNo + "\nDate: " + date + "\n\nSave?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                Customer c = new Customer(name, phone, email, type, mins, sms, callCost, smsCost, total, accNo, date, period);
                BillingService.addUsageToDB(c);
                refreshTable();
                BillingService.showSuccessPopup(c);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input: " + ex.getMessage());
        }
    }

    private void processPayment() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String phone = (String) table.getValueAt(row, 1); // Phone is now at Index 1
        try {
            BillingService.payDues(phone);
            refreshTable();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void refreshTable() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(BillingService.URL, BillingService.USER, BillingService.PASS)) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Subscribers");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("account_number"),
                        rs.getString("phone"),
                        rs.getString("name"),
                        rs.getInt("total_minutes"),
                        rs.getInt("total_sms"),
                        rs.getDouble("outstanding_dues"),
                        rs.getString("last_bill_date"),
                        rs.getString("bill_period")
                });
            }
        } catch (Exception e) {}
    }

    private JTextField addField(int x, int y) {
        JTextField tf = new JTextField(); tf.setBounds(x, y, 150, 30); add(tf); return tf;
    }
    private void addLabel(String t, int x, int y) {
        JLabel l = new JLabel(t); l.setBounds(x, y, 150, 20); add(l);
    }

    public static void main(String[] args) {
        new BillingApp();
    }
}
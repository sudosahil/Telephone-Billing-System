import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;
import java.time.LocalDate; // Syllabus: Date API
import java.time.format.DateTimeFormatter;
import java.util.Random;
import javax.swing.JOptionPane;

public class BillingService {

    static String URL, USER, PASS;

    // RATES
    static final double RATE_CALL_LOCAL = 0.80;
    static final double RATE_CALL_STD   = 1.20;
    static final double RATE_CALL_ISD   = 12.00;
    static final double RATE_SMS_DOMESTIC = 1.00;
    static final double RATE_SMS_INTL     = 5.00;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));

            String fullUrl = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASS = props.getProperty("db.password");

            // AUTO-SETUP
            String serverUrl = "jdbc:mysql://localhost:3306/";
            try (Connection setupCon = DriverManager.getConnection(serverUrl, USER, PASS);
                 Statement stmt = setupCon.createStatement()) {

                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS AirtelBilling");
                stmt.executeUpdate("USE AirtelBilling");

                // NEW TABLE STRUCTURE with Account No, Date, Period
                String tableSQL = "CREATE TABLE IF NOT EXISTS Subscribers (" +
                        "phone VARCHAR(15) PRIMARY KEY, " +
                        "account_number VARCHAR(10), " + // New
                        "name VARCHAR(50), " +
                        "email VARCHAR(50), " +
                        "last_call_type VARCHAR(10), " +
                        "total_minutes INT, " +
                        "total_sms INT DEFAULT 0, " +
                        "total_bill DOUBLE, " +
                        "outstanding_dues DOUBLE DEFAULT 0, " +
                        "last_bill_date VARCHAR(20), " + // New
                        "bill_period VARCHAR(20))";      // New
                stmt.executeUpdate(tableSQL);
            }
            URL = fullUrl;
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- HELPER METHODS ---

    public static String generateAccountNo() {
        Random r = new Random();
        return "ACC" + (100000 + r.nextInt(900000)); // Returns like ACC582934
    }

    public static String getCurrentDate() {
        return LocalDate.now().toString(); // Returns YYYY-MM-DD
    }

    public static String getBillPeriod() {
        // Returns "NOVEMBER 2025"
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")).toUpperCase();
    }

    // --- CALCULATIONS ---

    public static double calculateCallCost(String type, int mins) {
        if (type.equals("Local")) return mins * RATE_CALL_LOCAL;
        if (type.equals("STD")) return mins * RATE_CALL_STD;
        return mins * RATE_CALL_ISD;
    }

    public static double calculateSMSCost(String type, int count) {
        if (type.equals("ISD")) return count * RATE_SMS_INTL;
        return count * RATE_SMS_DOMESTIC;
    }

    // --- DB OPERATIONS ---

    public static void addUsageToDB(Customer c) throws SQLException {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            // Logic: If user exists, we update Date/Period but KEEP their old Account Number
            String query = "INSERT INTO Subscribers (phone, account_number, name, email, last_call_type, total_minutes, total_sms, total_bill, outstanding_dues, last_bill_date, bill_period) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "total_minutes = total_minutes + VALUES(total_minutes), " +
                    "total_sms = total_sms + VALUES(total_sms), " +
                    "total_bill = total_bill + VALUES(total_bill), " +
                    "outstanding_dues = outstanding_dues + VALUES(outstanding_dues), " +
                    "last_call_type = VALUES(last_call_type), " +
                    "last_bill_date = VALUES(last_bill_date), " + // Update Date
                    "bill_period = VALUES(bill_period)";           // Update Period
            // NOTE: We do NOT update account_number. It stays permanent.

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, c.phone);
            pst.setString(2, c.accountNumber);
            pst.setString(3, c.name);
            pst.setString(4, c.email);
            pst.setString(5, c.type);
            pst.setInt(6, c.callDuration);
            pst.setInt(7, c.smsCount);
            pst.setDouble(8, c.totalCost);
            pst.setDouble(9, c.totalCost);
            pst.setString(10, c.billDate);
            pst.setString(11, c.billPeriod);

            pst.executeUpdate();
        }
    }

    public static void payDues(String phone) throws SQLException {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            con.createStatement().executeUpdate("UPDATE Subscribers SET outstanding_dues = 0 WHERE phone = '" + phone + "'");
        }
    }

    public static void showSuccessPopup(Customer c) {
        String msg = "Usage Recorded Successfully!\n" +
                "-----------------------\n" +
                "Account No: " + c.accountNumber + "\n" +
                "Bill Date:  " + c.billDate + "\n" +
                "Period:     " + c.billPeriod + "\n" +
                "-----------------------\n" +
                "TOTAL:      Rs. " + String.format("%.2f", c.totalCost);
        JOptionPane.showMessageDialog(null, msg, "Billing Complete", JOptionPane.INFORMATION_MESSAGE);
    }
}
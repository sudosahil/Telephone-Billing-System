-----

# 📱 Airtel Enterprise Billing System

A robust, Java-based Desktop Application for Telecom CRM and Billing management. This system automates subscriber tracking, call & SMS tariff calculation, invoice generation, and payment status tracking using a MySQL database backend.

-----

## 🚀 Key Features

  * **Auto-Healing Database:** Automatically detects missing databases/tables and creates them on startup. No manual SQL script execution required.
  * **Dynamic Billing:** Calculates costs for Calls (Local/STD/ISD) and SMS (Domestic/Intl) based on standard tariffs.
  * **Smart Tracking:** auto-updates existing records (Upsert logic) without creating duplicates.
  * **Financial Status:** Visual indicators for payment status:
      * \<span style="color:red"\>**RED**\</span\>: Outstanding Dues.
      * \<span style="color:green"\>**GREEN**\</span\>: Fully Paid.
  * **Auto-Generation:** Automatically generates unique 6-digit Account Numbers and Billing Periods.
  * **Secure Config:** Database credentials are stored externally in `db.properties` for security.

-----

## 🛠️ Tech Stack

  * **Language:** Java (JDK 8+)
  * **Frontend:** Java Swing (JFrame, JTable, Custom Renderers)
  * **Backend:** JDBC (Java Database Connectivity)
  * **Database:** MySQL Server (8.0+)
  * **Configuration:** Properties File I/O

-----

## ⚙️ Prerequisites

Before running the application, ensure you have:

1.  **Java Development Kit (JDK)** installed.
2.  **MySQL Server** installed and running (Port 3306).
3.  **MySQL Connector JAR** file inside the project folder.
      * *Recommendation:* Rename the downloaded file to `mysql-connector.jar` for easier execution.

-----

## 📂 Project Structure

Ensure your folder (`TelephoneBilling`) contains these exact files:

```text
TelephoneBilling/
│
├── BillingApp.java        # Main UI & Entry Point
├── BillingService.java    # Logic, Calculations & Database Connectivity
├── Customer.java          # Data Model
├── db.properties          # Configuration File
└── mysql-connector.jar    # Database Driver Library
```

-----

## 📝 Setup Instructions

### 1\. Configure Database Credentials

Open the `db.properties` file and update it with your MySQL password:

```properties
db.url=jdbc:mysql://localhost:3306/AirtelBilling
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

### 2\. Compile the Code

Open your Command Prompt (Windows) or Terminal (Mac/Linux) in the project folder.

**Windows:**

```bash
javac -cp ".;mysql-connector.jar" *.java
```

**Mac/Linux:**

```bash
javac -cp ".:mysql-connector.jar" *.java
```

### 3\. Run the Application

**Windows:**

```bash
java -cp ".;mysql-connector.jar" BillingApp
```

**Mac/Linux:**

```bash
java -cp ".:mysql-connector.jar" BillingApp
```

-----

## 📊 Usage Guide

1.  **Add User:** Enter Name, Phone, Email.
2.  **Usage:** Select Type (Local/STD), enter Minutes and SMS count.
3.  **Calculate:** Click **"CALCULATE & SAVE"**.
      * *Note:* If the user exists, their bill is updated. If new, a new Account ID is generated.
4.  **Payment:** Select a row in the table and click **"CLEAR DUES"** to mark the bill as paid (Status turns Green).

-----

## ⚖️ Tariff Logic (Hardcoded)

| Type | Call Rate (per min) | SMS Rate (per unit) |
| :--- | :--- | :--- |
| **Local** | ₹ 0.80 | ₹ 1.00 |
| **STD** | ₹ 1.20 | ₹ 1.00 |
| **ISD** | ₹ 12.00 | ₹ 5.00 |

-----

## 🤝 Contributing

Feel free to fork this repository and submit pull requests. For major changes, please open an issue first to discuss what you would like to change.

**Author:** [Your Name]
**License:** Educational Use Only

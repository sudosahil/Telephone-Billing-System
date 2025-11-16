public class Customer {
    String name, phone, email, type;
    int callDuration;
    int smsCount;
    double callCost, smsCost, totalCost;

    // NEW FIELDS
    String accountNumber;
    String billDate;
    String billPeriod;

    public Customer(String name, String phone, String email, String type,
                    int callDuration, int smsCount,
                    double callCost, double smsCost, double totalCost,
                    String accountNumber, String billDate, String billPeriod) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.type = type;
        this.callDuration = callDuration;
        this.smsCount = smsCount;
        this.callCost = callCost;
        this.smsCost = smsCost;
        this.totalCost = totalCost;

        this.accountNumber = accountNumber;
        this.billDate = billDate;
        this.billPeriod = billPeriod;
    }
}
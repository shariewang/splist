package palie.splist.model;

public class ReceiptItem {

    String name, amount;

    public ReceiptItem() {}

    public ReceiptItem(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }
}
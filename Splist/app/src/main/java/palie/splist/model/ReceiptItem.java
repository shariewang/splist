package palie.splist.model;

/**
 * Created by Sharie on 7/18/2017.
 */

public class ReceiptItem {

    String name, amount;

    ReceiptItem(String name, String amount) {
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
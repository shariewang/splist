package palie.splist.ocr;

import com.google.firebase.database.FirebaseDatabase;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class ReceiptHandler extends DefaultHandler {

    private static final FirebaseDatabase DB = FirebaseDatabase.getInstance();
    private HashMap<String, String> data;
    private ArrayList<ReceiptItem> itemList;
    private String itemName, itemAmount, listKey;
    private boolean vendor, name, items, text, normalize, total, subtotal, tax;

    public ReceiptHandler(String listKey) {
        this.listKey = listKey;
        data = new HashMap<>();
        itemList = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "vendor":
                vendor = true;
                break;
            case "name":
                name = true;
                break;
            case "text":
                text = true;
                break;
            case "recognizedItems":
                items = true;
                break;
            case "tax":
                tax = true;
                String rate = attributes.getValue("rate");
                if (rate != null) {
                    data.put("taxrate", rate);
                }
                break;
            case "total":
                total = true;
                break;
            case "subTotal":
                subtotal = true;
                break;
            case "normalizedValue":
                normalize = true;
                break;
        }
    }

    private boolean confidence(Attributes a) {
        return Double.parseDouble(a.getValue("confidence")) > 50;
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        switch (qName) {
            case "vendor":
                vendor = false;
                break;
            case "name":
                name = false;
                break;
            case "subTotal":
                subtotal = false;
                break;
            case "total":
                total = false;
                break;
            case "normalizedValue":
                normalize = false;
                break;
            case "tax":
                tax = false;
                break;
            case "text":
                text = false;
                break;
            case "item":
                itemList.add(new ReceiptItem(itemName, itemAmount));
                break;
            case "recognizedItems":
                items = false;
                break;
            case "receipt":
                DB.getReference("Receipts").child(listKey).child("items").setValue(itemList);
                break;
        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) throws SAXException {
        if (vendor && name && text) {
            String s = new String(ch, start, length);
            System.out.println("vendor name: " + s);
            data.put("name", s);
            DB.getReference("Receipts").child(listKey).child("vendor").setValue(s);
        } else if (items && name && text) {
            String s = new String(ch, start, length);
            System.out.println("item name: " + s);
            itemName = s;
        } else if (items && total && normalize) {
            String s = new String(ch, start, length);
            System.out.println("item total: " + s);
            itemAmount = s;
        } else if (tax && normalize) {
            String s = new String(ch, start, length);
            System.out.println("tax: " + s);
            data.put("tax", s);
            DB.getReference("Receipts").child(listKey).child("tax").setValue(s);
        } else if (total && normalize) {
            String s = new String(ch, start, length);
            System.out.println("total: " + s);
            data.put("total", s);
            DB.getReference("Receipts").child(listKey).child("total").setValue(s);
        } else if (subtotal && normalize) {
            String s = new String(ch, start, length);
            System.out.println("subtotal: " + s);
            data.put("subtotal", s);
            DB.getReference("Receipts").child(listKey).child("subtotal").setValue(s);
        }
    }

    private class ReceiptItem {

        String name, amount;

        ReceiptItem(String name, String amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}

package palie.splist.ocr;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class ReceiptHandler extends DefaultHandler {

    private HashMap<String, String> data;
    private ArrayList<ReceiptItem> itemList;
    private String itemName, itemAmount;
    private boolean vendor, name, items, text, total, subtotal, tax;

    public ReceiptHandler() {
        data = new HashMap<>();
        itemList = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("vendor")) {
            vendor = true;
        } else if (qName.equals("name") && vendor) {
            name = true;
        } else if (qName.equals("text")) {
            text = true;
        } else if (qName.equals("recognizedItems")) {
            items = true;
        } else if (qName.equals("name") && items) {
            name = true;
        } else if (qName.equals("total") && items) {
            total = true;
        } else if (qName.equals("tax")) {
            tax = text = true;
            String rate = attributes.getValue("rate");
            if (rate != null) {
                data.put("taxrate", rate);
            }
        } else if (qName.equals("total")) {
            total = text = true;
        } else if (qName.equals("subTotal")) {

        }
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (qName.equals("vendor")) {
            vendor = items = false;
        } else if (qName.equals("item")) {
            itemList.add(new ReceiptItem(itemName, itemAmount));
        } else if (qName.equals("recognizedItems")) {
            items = false;
        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) throws SAXException {
        if (vendor && name && text) {
            String s = new String(ch, start, length);
            System.out.println("vendor name: "+ s);
            data.put("name", s);
            name = vendor = false;
        } else if (items && name && text) {
            String s = new String(ch, start, length);
            System.out.println("item name: " + s);
            itemName = s;
            name = text = false;
        } else if (items && total && text) {
            String s = new String(ch, start, length);
            System.out.println("item total: " + s);
            itemAmount = s;
            total = text = false;
        } else if (tax && text) {
            String s = new String(ch, start, length);
            System.out.println("tax: " + s);
            data.put("tax", s);
            total = text = false;
        } else if (total && text) {
            String s = new String(ch, start, length);
            System.out.println("total: " + s);
            data.put("total", s);
            total = text = false;
        } else if (subtotal && text) {
            String s = new String(ch, start, length);
            System.out.println("subtotal: " + s);
            data.put("subtotal", s);
            subtotal = text = false;
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

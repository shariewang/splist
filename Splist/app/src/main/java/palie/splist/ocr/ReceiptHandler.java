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
    private boolean vendor, name, items, text, normalize, total, subtotal, tax;

    public ReceiptHandler() {
        data = new HashMap<>();
        itemList = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("vendor")) {
            vendor = true;
        } else if (qName.equals("name")) {
            name = true;
        } else if (qName.equals("text")) {
            text = true;
        } else if (qName.equals("recognizedItems")) {
            items = true;
        } else if (qName.equals("name")) {
            name = true;
        } else if (qName.equals("total")) {
            total = true;
        } else if (qName.equals("tax")) {
            tax = true;
            String rate = attributes.getValue("rate");
            if (rate != null) {
                data.put("taxrate", rate);
            }
        } else if (qName.equals("total")) {
            total = true;
        } else if (qName.equals("subTotal")) {
            subtotal = true;
        } else if (qName.equals("normalizedValue")) {
            normalize = true;
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
        }
    }

    @Override
    public void characters(char ch[],
                           int start, int length) throws SAXException {
        if (vendor && name && text) {
            String s = new String(ch, start, length);
            System.out.println("vendor name: "+ s);
            data.put("name", s);
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
        } else if (total && normalize) {
            String s = new String(ch, start, length);
            System.out.println("total: " + s);
            data.put("total", s);
        } else if (subtotal && normalize) {
            String s = new String(ch, start, length);
            System.out.println("subtotal: " + s);
            data.put("subtotal", s);
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
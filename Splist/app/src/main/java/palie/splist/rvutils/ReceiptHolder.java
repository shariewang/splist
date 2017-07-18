package palie.splist.rvutils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import palie.splist.R;

public class ReceiptHolder extends RecyclerView.ViewHolder {

    private EditText name, price;

    public ReceiptHolder(View itemView) {
        super(itemView);
        name = (EditText) itemView.findViewById(R.id.name);
        price = (EditText) itemView.findViewById(R.id.price);
    }

    public void setName(String text) {
        name.setText(text);
    }

    public void setPrice(String text) {
        price.setText(text);
    }

}

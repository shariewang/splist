package palie.splist.rvutils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import palie.splist.R;

public class ItemHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final CheckBox checkbox;

    public ItemHolder(View itemView) {
        super(itemView);
        checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
        name = (TextView) itemView.findViewById(R.id.name);
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public TextView getTextView() {
        return name;
    }
}

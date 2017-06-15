package palie.splist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import palie.splist.model.Item;

public class MyListAdapter extends ArrayAdapter<Item> {

    public MyListAdapter(Context context, int resourceID) {
        super(context, resourceID);
    }

    public MyListAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_view, null);
            holder = new ViewHolder();
            holder.item = (TextView) convertView.findViewById(R.id.item);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = getItem(position);
        holder.checkBox.setChecked(item.getChecked());
        holder.item.setText(item.getItem());

        return convertView;
    }

    private static class ViewHolder {
        private TextView item;
        private CheckBox checkBox;
    }
}

package palie.splist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

import palie.splist.model.Item;

class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Item> items;

    MyListAdapter(Context mContext, ArrayList<Item> items) {
        super();
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = items.get(position);

        //if last item, it's the dummy item
        if (position == getItemCount() - 1) {
            holder.checkBox.setButtonDrawable(R.drawable.ic_add_white_24dp);
            holder.item.setHint("Add item");
        } else {
            holder.checkBox.setChecked(item.getChecked());
            holder.item.setText(item.getItem());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        EditText item;

        ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            item = (EditText) v.findViewById(R.id.item);
        }

    }
}

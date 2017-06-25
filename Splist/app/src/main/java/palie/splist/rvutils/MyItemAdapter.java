package palie.splist.rvutils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import palie.splist.R;
import palie.splist.model.Item;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private Context mContext;

    public MyItemAdapter(ArrayList<Item> items, Context mContext) {
        super();
        this.items = items;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_checkbox, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item i = items.get(position);
        holder.checkbox.setChecked(i.getChecked());
        holder.name.setText(i.getItem());
        holder.delete.setVisibility(View.INVISIBLE);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ArrayList<Item> getList() {
        return items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkbox;
        EditText name;
        ImageView delete;
        int position;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
            name = (EditText) itemView.findViewById(R.id.itemName);
            delete = (ImageView) itemView.findViewById(R.id.delete);

            name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        if (position == getItemCount() - 1) {
                            name.setHint("");
                            items.add(new Item());
                        }
                        delete.setVisibility(View.VISIBLE);
                    } else {
                        items.get(position).setItem(name.getText().toString());
                        delete.setVisibility(View.INVISIBLE);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}

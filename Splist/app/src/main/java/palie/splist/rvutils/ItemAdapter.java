package palie.splist.rvutils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

import palie.splist.R;
import palie.splist.model.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private Activity activity;
    private Context context;

    public ItemAdapter(ArrayList<Item> items, Context context, Activity activity) {
        super();
        this.items = items;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_guest, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item i = items.get(position);
        if (!Objects.equals(i.getImageKey(), "false")) {
            holder.name.setPaintFlags(holder.name.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        }
        holder.checkbox.setChecked(i.getChecked());
        holder.name.setText(i.getItem());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog alertDialog = new Dialog(activity);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_image);
                ImageView image = (ImageView) alertDialog.findViewById(R.id.image);
                Glide.with(context).using(new FirebaseImageLoader())
                        .load(FirebaseStorage.getInstance().getReference().child(i.getImageKey())).into(image);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkbox;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
            name = (TextView) itemView.findViewById(R.id.itemName);
        }
    }
}

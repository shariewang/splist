package palie.splist.rvutils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import palie.splist.ListActivity;
import palie.splist.MainActivity;
import palie.splist.NewGroupActivity;
import palie.splist.R;
import palie.splist.model.Item;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private UploadImageListener listener;
    private static final FirebaseDatabase DB = FirebaseDatabase.getInstance();

    public MyItemAdapter(ArrayList<Item> items, UploadImageListener listener) {
        super();
        this.items = items;
        this.listener = listener;
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
        if (!Objects.equals(i.getImageKey(), "false")) {
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        holder.delete.setVisibility(View.INVISIBLE);
        holder.photo.setVisibility(View.INVISIBLE);
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
        ImageView delete, photo;
        int position;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
            name = (EditText) itemView.findViewById(R.id.itemName);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            photo = (ImageView) itemView.findViewById(R.id.photo);

            name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        if (position == getItemCount() - 1) {
                            name.setHint("");
                            items.add(new Item());
                        }
                        delete.setVisibility(View.VISIBLE);
                        photo.setVisibility(View.VISIBLE);
                    } else {
                        items.get(position).setItem(name.getText().toString());
                        delete.setVisibility(View.INVISIBLE);
                        photo.setVisibility(View.INVISIBLE);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position, getItemCount());
                }
            });

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.uploadImage(position, name);
                }
            });
        }
    }
}

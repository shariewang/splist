package palie.splist.rvutils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import palie.splist.ListClickListener;
import palie.splist.R;
import palie.splist.model.List;

public class ActiveAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private ArrayList<List> mLists;
    private Context mContext;
    private ListClickListener listClickListener;
    private int color;
    private String groupKey;

    public ActiveAdapter(ArrayList<List> mLists, String groupKey, Context mContext, ListClickListener listClickListener) {
        super();
        this.mLists = mLists;
        this.groupKey = groupKey;
        this.mContext = mContext;
        this.listClickListener = listClickListener;
        FirebaseDatabase.getInstance().getReference("Groups").
                child(groupKey).child("main").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                color = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new ListViewHolder(v, listClickListener);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        List list = mLists.get(position);
        holder.name.setText(list.getName());
        holder.position = position;
        holder.listKey = list.getKey();
        switch(list.getType()) {
            case "Office":
                holder.icon.setImageResource(R.drawable.paperclip);
                break;
            case "Clothing":
                holder.icon.setImageResource(R.drawable.ic_hanger);
                break;
            case "Food":
                holder.icon.setImageResource(R.drawable.food);
                break;
        }
        holder.icon.setFillColor(color);
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }


}

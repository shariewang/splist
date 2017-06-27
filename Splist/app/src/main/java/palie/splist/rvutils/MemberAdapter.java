package palie.splist.rvutils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import palie.splist.R;
import palie.splist.model.MemberList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<MemberList> members;
    private Context mContext;
    private Activity activity;

    public MemberAdapter(ArrayList<MemberList> members, Context mContext, Activity activity) {
        this.members = members;
        this.mContext = mContext;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MemberList member = members.get(position);

        Glide.with(mContext).using(new FirebaseImageLoader())
                .load(storage.getReference().child(member.getUid())).into(holder.image);
        holder.name.setText(member.getName());

        holder.listView.setLayoutManager(new LinearLayoutManager(mContext) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        holder.listView.setAdapter(new ItemAdapter(member.getItems(), mContext, activity));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        RecyclerView listView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            listView = (RecyclerView) itemView.findViewById(R.id.items);
        }
    }
}

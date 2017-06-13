package palie.splist;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> mGroups;
    private Context mContext;
    private final GroupClickListener groupClickListener;

    GroupAdapter(List<Group> groups, Context mContext, GroupClickListener groupClickListener) {
        super();
        this.mGroups = groups;
        this.mContext = mContext;
        this.groupClickListener = groupClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = mGroups.get(position);

        System.out.println(group.getMain());
        holder.card.setCardBackgroundColor(group.getMain());
        holder.groupName.setText(group.getName());
        holder.groupMembers.setText(group.getMembers());
        holder.groupKey = group.getKey();
        holder.position = position;
        ViewCompat.setTransitionName(holder.groupImage, group.getKey());
        ViewCompat.setTransitionName(holder.groupName, group.getKey()+"name");
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(FirebaseStorage.getInstance().getReference().child(group.getKey()))
                .into(holder.groupImage);
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView groupImage;
        TextView groupName, groupMembers;
        String groupKey;
        CardView card;
        int position;

        ViewHolder(View v) {
            super(v);
            groupImage = (ImageView) v.findViewById(R.id.img);
            groupName = (TextView) v.findViewById(R.id.name);
            groupMembers = (TextView) v.findViewById(R.id.members);
            card = (CardView) v.findViewById(R.id.card);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupClickListener.onGroupClick(position, groupKey, groupImage);
                }
            });
        }
    }
}

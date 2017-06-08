package palie.splist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> mGroups;
    private Context mContext;

    GroupAdapter(List<Group> groups, Context mContext) {
        super();
        this.mGroups = groups;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = mGroups.get(position);

        holder.groupName.setText(group.getName());
        holder.groupMembers.setText(group.getMembers());
        holder.groupKey = group.getKey();
//        Glide.with(mContext)
//                .using(new FirebaseImageLoader())
//                .load(FirebaseStorage.getInstance().getReference("groupImages").child(group.getImageKey()))
//                .into(holder.groupImage);
        Glide.with(mContext).load(group.getKey()).into(holder.groupImage);
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView groupImage;
        TextView groupName, groupMembers;
        String groupKey;

        ViewHolder(View v) {
            super(v);
            groupImage = (ImageView) v.findViewById(R.id.img);
            groupName = (TextView) v.findViewById(R.id.name);
            groupMembers = (TextView) v.findViewById(R.id.members);
        }

        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.img:
                    Intent i = new Intent(mContext, GroupActivity.class);
                    i.putExtra("key", groupKey);
                    mContext.startActivity(i);
                    break;
            }
        }
    }
}

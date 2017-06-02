package palie.splist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> mGroups;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView groupImage;
        TextView groupName, groupMembers;

        ViewHolder(View v) {
            super(v);
            groupImage = (ImageView) v.findViewById(R.id.groupImg);
            groupName = (TextView) v.findViewById(R.id.groupName);
            groupMembers = (TextView) v.findViewById(R.id.groupMembers);

            groupImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public GroupAdapter(List<Group> groups) {
        super();
        this.mGroups = groups;

    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = mGroups.get(position);

        holder.groupName.setText(group.getName());
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }
}

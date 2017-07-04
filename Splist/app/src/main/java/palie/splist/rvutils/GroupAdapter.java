package palie.splist.rvutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import palie.splist.listeners.GroupClickListener;
import palie.splist.R;
import palie.splist.model.Group;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> mGroups;
    private Context mContext;
    private final GroupClickListener groupClickListener;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public GroupAdapter(List<Group> groups, Context mContext, GroupClickListener groupClickListener) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Group group = mGroups.get(position);

        String key = group.getKey();
//        holder.card.setCardBackgroundColor(group.getMain());
        //holder.layout.setBackgroundColor(group.getMain());
        holder.groupName.setBackgroundColor(group.getMain());
        holder.groupMembers.setBackgroundColor(group.getMain());
        holder.groupName.getBackground().setAlpha(225);
        holder.groupMembers.getBackground().setAlpha(225);
        holder.groupName.setText(group.getName());
        holder.groupMembers.setText(convertToString(group.getNames()));
        holder.groupKey = key;
        holder.position = position;
        ViewCompat.setTransitionName(holder.groupImage, group.getKey());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(storage.getReference().child(key))
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
        RelativeLayout layout;
        int position;

        ViewHolder(View v) {
            super(v);
            groupImage = (ImageView) v.findViewById(R.id.img);
            groupName = (TextView) v.findViewById(R.id.name);
            groupMembers = (TextView) v.findViewById(R.id.members);
            layout = (RelativeLayout) v.findViewById(R.id.layout);
            card = (CardView) v.findViewById(R.id.card);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupClickListener.onGroupClick(position, groupKey, groupImage);
                }
            });
        }
    }

    private String convertToString(ArrayList<String> names) {
        if (names.size() == 1) {
            return names.get(0);
        } else if (names.size() == 2) {
            return names.get(0) + " and " + names.get(1);
        } else {
            return names.get(0) + ", " + names.get(1) + ", and " + (names.size() - 2) + " others";
        }
    }

    private void blur(Bitmap bkg, View view, float radius) {
        Bitmap overlay = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);

        canvas.drawBitmap(bkg, -view.getLeft(),
                -view.getTop(), null);

        RenderScript rs = RenderScript.create(mContext);

        Allocation overlayAlloc = Allocation.createFromBitmap(
                rs, overlay);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                rs, overlayAlloc.getElement());

        blur.setInput(overlayAlloc);

        blur.setRadius(radius);

        blur.forEach(overlayAlloc);

        overlayAlloc.copyTo(overlay);

        view.setBackground(new BitmapDrawable(
                mContext.getResources(), overlay));

        rs.destroy();
    }


}

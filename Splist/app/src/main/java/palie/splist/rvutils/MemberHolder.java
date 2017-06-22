package palie.splist.rvutils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import palie.splist.R;

public class MemberHolder extends RecyclerView.ViewHolder {

    private final ImageView image;
    private final TextView name;
    private final ListView listView;

    public MemberHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
        name = (TextView) itemView.findViewById(R.id.name);
        listView = (ListView) itemView.findViewById(R.id.items);
    }

    public ImageView getImageView() {
        return image;
    }

    public TextView getTextView() {
        return name;
    }

    public ListView getListView() {
        return listView;
    }
}

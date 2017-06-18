package palie.splist.rvutils;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import palie.splist.ListClickListener;
import palie.splist.R;


class ListViewHolder extends RecyclerView.ViewHolder {

    CircleImageView icon;
    TextView name;
    String listKey;
    int position;

    ListViewHolder(View v, final ListClickListener listClickListener) {
        super(v);
        icon = (CircleImageView) v.findViewById(R.id.icon);
        name = (TextView) v.findViewById(R.id.name);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listClickListener.onListClick(position, listKey, name.getText().toString());
            }
        });
    }
}
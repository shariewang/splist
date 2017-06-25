package palie.splist.rvutils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import palie.splist.GroupActivity;
import palie.splist.ListClickListener;
import palie.splist.R;


public class ListViewHolder extends RecyclerView.ViewHolder {

    CircleImageView icon;
    TextView name;
    String listKey;
    int position;



    ListViewHolder(View v) {
        super(v);
        icon = (CircleImageView) v.findViewById(R.id.icon);
        name = (TextView) v.findViewById(R.id.name);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listClickListener.onListClick(position, listKey, name.getText().toString());
//            }
//        });
    }

    public CircleImageView getIcon(){
        return icon;
    }

    public TextView getName() {
        return name;
    }

    public void setKey(String listKey) {
        this.listKey = listKey;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }
}
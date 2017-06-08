package palie.splist

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

internal class GroupAdapterK(private val mGroups: List<Group>, private val mContext: Context) : RecyclerView.Adapter<GroupAdapterK.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupAdapterK.ViewHolder? {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.group_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: GroupAdapterK.ViewHolder?, position: Int) {
        val group = mGroups[position]

        holder!!.groupName.text = group.name
        holder.groupMembers.text = group.members
        holder.groupKey = group.key
        //        Glide.with(mContext)
        //                .using(new FirebaseImageLoader())
        //                .load(FirebaseStorage.getInstance().getReference("groupImages").child(group.getImageKey()))
        //                .into(holder.groupImage);
        Glide.with(mContext).load(group.key).into(holder.groupImage)
    }

    override fun getItemCount(): Int {
        return mGroups.size
    }

    internal inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        var groupImage: ImageView
        var groupName: TextView
        var groupMembers: TextView
        var groupKey: String? = null

        init {
            groupImage = v.findViewById(R.id.img) as ImageView
            groupName = v.findViewById(R.id.name) as TextView
            groupMembers = v.findViewById(R.id.members) as TextView
        }

        override fun onClick(view: View) {
            when (view.id) {
                R.id.img -> {
                    val i = Intent(mContext, GroupActivity::class.java)
                    i.putExtra("key", groupKey)
                    mContext.startActivity(i)
                }
            }
        }
    }
}

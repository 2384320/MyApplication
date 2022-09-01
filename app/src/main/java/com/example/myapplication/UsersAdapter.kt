package com.example.myapplication

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.UsersAdapter.CustomViewHolder

class UsersAdapter(context: Activity?, list: ArrayList<PersonalData>?) :
    RecyclerView.Adapter<CustomViewHolder>() {
    private var mList: ArrayList<PersonalData>? = null
    private var context: Activity? = null

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var id: TextView
        var name: TextView
        var address: TextView

        init {
            id = view.findViewById<View>(R.id.textView_list_id) as TextView
            name = view.findViewById<View>(R.id.textView_list_name) as TextView
            address = view.findViewById<View>(R.id.textView_list_address) as TextView
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_list, null)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(viewholder: CustomViewHolder, position: Int) {
        viewholder.id.text = mList!![position].member_id
        viewholder.name.text = mList!![position].member_name
        viewholder.address.text = mList!![position].member_address
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    init {
        this.context = context
        mList = list
    }
}
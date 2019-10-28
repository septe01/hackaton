package com.biceps_studio.contact.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.biceps_studio.contact.R
import com.biceps_studio.contact.activity.DetailActivity
import com.biceps_studio.contact.data.ContactData
import com.biceps_studio.contact.helper.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    var arrayList: ArrayList<ContactData> = ArrayList()

    lateinit var listener: Listener

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun binData(contactData: ContactData, listener: Listener, index: Int){
            Glide.with(view).load(contactData.image_url).apply(RequestOptions().circleCrop()).into(view.ivPhoto)

            view.tvName.text = contactData.name
            view.tvPhone.text = contactData.phone

            view.llMain.setOnClickListener {
                view.context.startActivity(
                    Intent(view.context, DetailActivity::class.java).putExtra(Utils.ID_CONTACT, contactData.id)
                )
            }

            view.ivDelete.setOnClickListener { listener.onDelete(index) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binData(arrayList[position], listener, position)
    }

    interface Listener {
        fun onDelete(int: Int)
    }
}
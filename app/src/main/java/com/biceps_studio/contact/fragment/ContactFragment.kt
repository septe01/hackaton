package com.biceps_studio.contact.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.biceps_studio.contact.BuildConfig
import com.biceps_studio.contact.R
import com.biceps_studio.contact.activity.DetailActivity
import com.biceps_studio.contact.adapter.ContactsAdapter
import com.biceps_studio.contact.data.ContactData
import com.biceps_studio.contact.data.ResponseData
import com.biceps_studio.contact.helper.Utils
import com.biceps_studio.task_layout.api.API
import kotlinx.android.synthetic.main.fragment_contact.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactFragment : Fragment() {

    private val contactsAdapter = ContactsAdapter()
    private var arrayList: ArrayList<ContactData> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initRecyclerView()
        initEvent()
    }

    override fun onResume() {
        super.onResume()

        getData()
    }

    private fun getData() {
        srlMain.isRefreshing = true

        API.getContacts(object: Callback<ResponseData> {
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                srlMain.isRefreshing = false

                if (BuildConfig.DEBUG) {
                    Utils.showToast(activity!!, t.message!!)
                }
            }

            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                srlMain.isRefreshing = false

                if (response.isSuccessful) {
                    arrayList = response.body()!!.data

                    contactsAdapter.arrayList = arrayList
                    contactsAdapter.notifyDataSetChanged()
                } else {
                    if (BuildConfig.DEBUG) {
                        Utils.showToast(activity!!, response.message())
                    }
                }
            }
        })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        rvContacts.apply {
            setHasFixedSize(true)
            adapter = contactsAdapter
            layoutManager = linearLayoutManager
        }
    }

    private fun initEvent() {
        fabAdd.setOnClickListener { startActivity(Intent(activity!!, DetailActivity::class.java)) }

        srlMain.setOnRefreshListener { getData() }

        contactsAdapter.listener = object: ContactsAdapter.Listener {
            override fun onDelete(int: Int) {
                srlMain.isRefreshing = true

                API.deleteContact(arrayList[int].id, object: Callback<Int> {
                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        srlMain.isRefreshing = false

                        if (BuildConfig.DEBUG) {
                            Utils.showToast(activity!!, t.message!!)
                        }
                    }

                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        srlMain.isRefreshing = false

                        if (response.isSuccessful) {
                            arrayList.removeAt(int)

                            contactsAdapter.notifyItemRemoved(int)
                            contactsAdapter.notifyDataSetChanged()
                        } else {
                            if (BuildConfig.DEBUG) {
                                Utils.showToast(activity!!, response.message())
                            }
                        }
                    }
                })
            }
        }
    }
}

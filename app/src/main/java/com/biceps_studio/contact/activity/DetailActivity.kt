@file:Suppress("DEPRECATION")

package com.biceps_studio.contact.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.biceps_studio.contact.BuildConfig
import com.biceps_studio.contact.R
import com.biceps_studio.contact.data.ContactData
import com.biceps_studio.contact.helper.Utils
import com.biceps_studio.task_layout.api.API
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private var id = -1
    private var isUpdate = false

    private lateinit var contactData: ContactData

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initView()
        initData()
        initEvent()
    }

    private fun initEvent() {
        ivBack.setOnClickListener { finish() }
        tvSubmit.setOnClickListener { handleContact() }
        etDetailName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.e("TAG", p0!!.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.e("TAG", p0!!.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tvDetail.text = p0!!.toString()
            }
        })
    }

    private fun handleContact() {
        if (isUpdate){
            Utils.showLoading(progressDialog)

            contactData.name = etDetailName.text.toString()
            contactData.phone = etDetailPhone.text.toString()

            API.updateContact(contactData, object: Callback<Array<Int>> {
                override fun onFailure(call: Call<Array<Int>>, t: Throwable) {
                    Utils.hideLoading(progressDialog)

                    if (BuildConfig.DEBUG) {
                        Utils.showToast(this@DetailActivity, t.message!!)
                    }
                }

                override fun onResponse(call: Call<Array<Int>>, response: Response<Array<Int>>) {
                    Utils.hideLoading(progressDialog)

                    if (response.isSuccessful) {
                        finish()
                    } else {
                        if (BuildConfig.DEBUG) {
                            Utils.showToast(this@DetailActivity, response.message())
                        }
                    }
                }
            })
        } else {
            Utils.showLoading(progressDialog)

            val hashMap: HashMap<String, String> = HashMap()
            hashMap["name"] = etDetailName.text.toString()
            hashMap["phone"] = etDetailPhone.text.toString()

            API.addContact(hashMap, object: Callback<ContactData> {
                override fun onFailure(call: Call<ContactData>, t: Throwable) {
                    Utils.hideLoading(progressDialog)

                    if (BuildConfig.DEBUG) {
                        Utils.showToast(this@DetailActivity, t.message!!)
                    }
                }

                override fun onResponse(call: Call<ContactData>, response: Response<ContactData>) {
                    Utils.hideLoading(progressDialog)

                    if (response.isSuccessful) {
                        finish()
                    } else {
                        if (BuildConfig.DEBUG) {
                            Utils.showToast(this@DetailActivity, response.message())
                        }
                    }
                }
            })
        }
    }

    private fun initData() {
        progressDialog = Utils.getLoading(this)

        if (isUpdate){
            Utils.showLoading(progressDialog)

            API.getDetail(id, object: Callback<ContactData> {
                override fun onFailure(call: Call<ContactData>, t: Throwable) {
                    Utils.hideLoading(progressDialog)

                    if (BuildConfig.DEBUG) {
                        Utils.showToast(this@DetailActivity, t.message!!)
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<ContactData>, response: Response<ContactData>) {
                    Utils.hideLoading(progressDialog)

                    if (response.isSuccessful) {
                        contactData = response.body()!!

                        tvDetail.text = contactData.name

                        etDetailName.setText(contactData.name)
                        etDetailPhone.setText(contactData.phone)
                        etDetailEmail.setText("kimetsunoyaiba@japan.com")

                        Glide.with(this@DetailActivity).load(contactData.image_url)
                            .apply(RequestOptions().circleCrop()).into(ivDetail)
                    } else {
                        if (BuildConfig.DEBUG) {
                            Utils.showToast(this@DetailActivity, response.message())
                        }
                    }
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        id = intent.getIntExtra(Utils.ID_CONTACT, -1)

        if (id == -1){
            tvTitle.text = "Create"
            tvSubmit.text = "Add"
            isUpdate = false
        } else {
            tvTitle.text = "Edit"
            tvSubmit.text = "Save"
            isUpdate = true
        }
    }
}

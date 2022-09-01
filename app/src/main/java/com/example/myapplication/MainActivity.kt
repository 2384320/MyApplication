package com.example.myapplication

import android.accessibilityservice.GestureDescription
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var mEditTextName: EditText? = null
    private var mEditTextCountry: EditText? = null
    private var mTextViewResult: TextView? = null
    private var mArrayList: ArrayList<PersonalData>? = null
    private var mAdapter: UsersAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mEditTextSearchKeyword: EditText? = null
    private var mJsonString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEditTextName = findViewById<View>(R.id.editText_main_name) as EditText
        mEditTextCountry = findViewById<View>(R.id.editText_main_country) as EditText
        mTextViewResult = findViewById<View>(R.id.textView_main_result) as TextView
        mRecyclerView = findViewById<View>(R.id.listView_main_list) as RecyclerView
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        mEditTextSearchKeyword = findViewById<View>(R.id.editText_main_searchKeyword) as EditText

        mTextViewResult!!.movementMethod = ScrollingMovementMethod()

        mArrayList = ArrayList()

        mAdapter = UsersAdapter(this, mArrayList)
        mRecyclerView!!.adapter = mAdapter

        val buttonInsert = findViewById<View>(R.id.button_main_insert) as Button
        buttonInsert.setOnClickListener {
            val name = mEditTextName!!.text.toString()
            val country = mEditTextCountry!!.text.toString()
            val task = InsertData()
            task.execute(
                "https://z8t1hemls2.execute-api.us-east-2.amazonaws.com/default/lambda-example",
                name,
                country
            )
            mEditTextName!!.setText("")
            mEditTextCountry!!.setText("")
        }

        val button_search = findViewById<View>(R.id.button_main_search) as Button
        button_search.setOnClickListener {
            mArrayList!!.clear()
            mAdapter!!.notifyDataSetChanged()
            val Keyword = mEditTextSearchKeyword!!.text.toString()
            mEditTextSearchKeyword!!.setText("")
            val task = GetData()
            task.execute(
                "https://buyrdke832.execute-api.us-east-2.amazonaws.com/default/GetData",
                Keyword
            )
        }

    }

    private inner class InsertData : AsyncTask<String, String, String>() {
        var progressDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(
                this@MainActivity,
                "Please Wait", null, true, true
            )
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            progressDialog!!.dismiss()
            mTextViewResult!!.text = result
        }

        override fun doInBackground(vararg params: String): String {
            val name = params[1]
            val country = params[2]
            val serverURL = params[0]
            val jsonObject = JSONObject()
            try {
                jsonObject.put("Name", name)
                jsonObject.put("Country", country)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val client = OkHttpClient()
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull() // MediaType.parse("application/json; charset=utf-8")
            val body = RequestBody.create(JSON, jsonObject.toString())
            val request: Request = Request.Builder()
                .url(serverURL)
                .post(body)
                .build()
            var response: Response? = null
            return try {
                response = client.newCall(request).execute()
                response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                e.toString()
            }
        }
    }

    private inner class GetData : AsyncTask<String, Void, String>() {
        var progressDialog: ProgressDialog? = null
        var errorString: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog.show(
                this@MainActivity,
                "Please Wait", null, true, true
            )
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog!!.dismiss()
            mTextViewResult!!.text = result
            Log.d(TAG, "response - $result")
            if (result == null) {
                mTextViewResult!!.text = errorString
            } else {
                mJsonString = result
                showResult()
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val name = params[1]
            val serverURL = params[0]
            val jsonObject = JSONObject()
            try {
                jsonObject.put("Name", name)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val client = OkHttpClient()
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(JSON, jsonObject.toString())
            val request: Request = Request.Builder()
                .url(serverURL)
                .post(body)
                .build()
            var response: Response? = null
            return try {
                response = client.newCall(request).execute()
                response.body!!.string()
            } catch (e: IOException) {
                e.printStackTrace()
                e.toString()
            }
        }
    }

    private fun showResult() {
        val TAG_JSON = "webnautes"
        val TAG_ID = "id"
        val TAG_NAME = "Name"
        val TAG_COUNTRY = "Country"
        try {
            val jsonObject = JSONObject(mJsonString)
            val str = jsonObject.getString("body")
            val jsonObjectBody = JSONObject(str)
            val name = jsonObjectBody.getString(TAG_NAME)
            val address = jsonObjectBody.getString(TAG_COUNTRY)
            val personalData = PersonalData()
            personalData.member_name = name
            personalData.member_address = address
            mArrayList!!.add(personalData)
            mAdapter!!.notifyDataSetChanged()
        } catch (e: JSONException) {
            Log.d(TAG, "showResult : ", e)
        }
    }

    companion object {
        private const val TAG = "awsexample"
    }
}

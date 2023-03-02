package com.example.firebase

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_firebase.*
import kotlinx.android.synthetic.main.activity_firebase.btnChooseImage
import kotlinx.android.synthetic.main.activity_firebase.btnSave
import kotlinx.android.synthetic.main.activity_firebase.etEmail
import kotlinx.android.synthetic.main.activity_firebase.etName
import kotlinx.android.synthetic.main.activity_firebase.etPhoneNumber
import kotlinx.android.synthetic.main.activity_firebase.recyclerView
import kotlinx.android.synthetic.main.activity_firebase.tvDatePicker
import kotlinx.android.synthetic.main.activity_shared_preferences.*
import java.util.*

class SharedPreferencesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var uriData = ""
    private val REQUEST_CODE = 100
    private lateinit var usersList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_preferences)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        tvDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        btnChooseImage.setOnClickListener {
            openGalleryForImage()
        }

        usersList = arrayListOf()

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhoneNumber.text.toString()
            val email = etEmail.text.toString()
            val date = tvDatePicker.text.toString()
            val uri = uriData
            val user = User(name, phone, email, date, uri)

            usersList.add(user)
            recyclerView.adapter?.notifyItemInserted(usersList.size-1)
            saveUser()
            loadUser()

            etName.setText("")
            etPhoneNumber.setText("")
            etEmail.setText("")
            tvDatePicker.text = "Date"
        }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            uriData = (data?.data!!).toString()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = "$dayOfMonth/${month + 1}/$year"
                tvDatePicker.text = date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveUser() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(usersList)
        editor.putString("userList", json)
        editor.apply()
    }

    private fun loadUser(){
        val gson = Gson()
        val json = sharedPreferences.getString("userList","")
        val type = object : TypeToken<ArrayList<User>>() {}.type
        usersList = gson.fromJson(json,type)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserAdapter(usersList,{index -> deleteUser(index)},{index -> editUser(index)})
    }

    private fun deleteUser(index: Int) {
        val user = usersList[index]
        usersList.removeAt(index)
        recyclerView.adapter =
            UserAdapter(usersList, { index -> deleteUser(index) }, {index -> editUser(index)})
    }

    private fun editUser(index: Int) {
        val user = usersList[index]
        deleteUser(index)
        etName.setText(user.name)
        etEmail.setText(user.emailAdd)
        etPhoneNumber.setText(user.phoneNumber)
        tvDatePicker.text = user.birthDate
        uriData = user.imageUri.toString()
    }

}
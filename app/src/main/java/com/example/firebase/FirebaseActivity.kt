package com.example.firebase

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_firebase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class FirebaseActivity : AppCompatActivity() {

    private val userRef = Firebase.firestore.collection("users")
    private val REQUEST_CODE = 100
    private var uriData: String = ""
    private lateinit var usersList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)

        tvDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        btnChooseImage.setOnClickListener {
            openGalleryForImage()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val phone = etPhoneNumber.text.toString()
            val email = etEmail.text.toString()
            val date = tvDatePicker.text.toString()
            val uri = uriData
            val user = User(name, phone, email, date, uri)
            if (user.name == "" && user.emailAdd == "" && user.birthDate == "" && user.phoneNumber == "" && user.imageUri == "") {
                Toast.makeText(this@FirebaseActivity, "Please enter some value", Toast.LENGTH_SHORT)
                    .show()
            } else {
                saveUser(user)
                recyclerView.layoutManager = LinearLayoutManager(this)
                usersList = arrayListOf()
                userRef.get()
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            for (data in it.documents) {
                                val user1: User? = data.toObject(User::class.java)
                                if (user1 != null) {
                                    usersList.add(user1)
                                }
                            }
                            recyclerView.adapter = UserAdapter(usersList,
                                { index -> deleteUser(index) },
                                { index -> editUser(index) })
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    }
                etName.setText("")
                etPhoneNumber.setText("")
                etEmail.setText("")
                tvDatePicker.text = "Date"
                uriData = ""
            }
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
            // handle chosen image
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

    private fun editUser(index: Int) {
        val user = usersList[index]
        deleteUser(index)
        etName.setText(user.name)
        etEmail.setText(user.emailAdd)
        etPhoneNumber.setText(user.phoneNumber)
        tvDatePicker.text = user.birthDate
        uriData = user.imageUri.toString()
    }

    private fun deleteUser(index: Int) {
        val user = usersList[index]
        usersList.removeAt(index)
        CoroutineScope(Dispatchers.IO).launch {
            val userQuery = userRef
                .whereEqualTo("name", user.name)
                .whereEqualTo("birthDate", user.birthDate)
                .whereEqualTo("phoneNumber", user.phoneNumber)
                .whereEqualTo("emailAdd", user.emailAdd)
                .get()
                .await()
            if (userQuery.documents.isNotEmpty()) {
                for (document in userQuery) {
                    try {
                        userRef.document(document.id).delete().await()
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@FirebaseActivity, e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
        recyclerView.adapter =
            UserAdapter(usersList, { index -> deleteUser(index) }, { index -> editUser(index) })
    }

    private fun saveUser(user: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            userRef.add(user).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@FirebaseActivity,
                    "Successfully Saved User Info",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@FirebaseActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
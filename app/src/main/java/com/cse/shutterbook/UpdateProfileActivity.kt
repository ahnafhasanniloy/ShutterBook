package com.cse.shutterbook

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var nameEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var addressEdit: EditText
    private lateinit var requirementsEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var updateBtn: Button

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("clients")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        nameEdit = findViewById(R.id.editName)
        phoneEdit = findViewById(R.id.editPhone)
        emailEdit = findViewById(R.id.editEmail)
        addressEdit = findViewById(R.id.editAddress)
        requirementsEdit = findViewById(R.id.editRequirements)
        passwordEdit = findViewById(R.id.editPassword)
        updateBtn = findViewById(R.id.btnUpdate)

        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null && user.email != null) {

            dbRef.child(userId).get().addOnSuccessListener { snapshot ->
                nameEdit.setText(snapshot.child("name").value?.toString())
                phoneEdit.setText(snapshot.child("phone").value?.toString())
                emailEdit.setText(user.email)
                addressEdit.setText(snapshot.child("address").value?.toString())
                requirementsEdit.setText(snapshot.child("requirements").value?.toString())
                passwordEdit.setText("")
            }

            updateBtn.setOnClickListener {
                val newName = nameEdit.text.toString().trim()
                val newPhone = phoneEdit.text.toString().trim()
                val newAddress = addressEdit.text.toString().trim()
                val newRequirements = requirementsEdit.text.toString().trim()
                val newPassword = passwordEdit.text.toString().trim()
                val email = user.email!!

                if (newName.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty() ||
                    newRequirements.isEmpty() || newPassword.length < 6
                ) {
                    Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val currentPasswordInput = EditText(this).apply {
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    hint = "Enter current password"
                }

                AlertDialog.Builder(this)
                    .setTitle("Re-authentication Required")
                    .setMessage("Enter your current password to update profile and password.")
                    .setView(currentPasswordInput)
                    .setPositiveButton("Confirm") { _, _ ->
                        val currentPassword = currentPasswordInput.text.toString()

                        val credential = EmailAuthProvider.getCredential(email, currentPassword)
                        user.reauthenticate(credential).addOnSuccessListener {

                            user.updatePassword(newPassword).addOnSuccessListener {

                                val updatedData = mapOf(
                                    "name" to newName,
                                    "phone" to newPhone,
                                    "email" to email,
                                    "address" to newAddress,
                                    "requirements" to newRequirements,
                                    "password" to newPassword
                                )

                                dbRef.child(userId).updateChildren(updatedData).addOnSuccessListener {
                                    Toast.makeText(this, "Profile and password updated", Toast.LENGTH_SHORT).show()
                                    finish()
                                }.addOnFailureListener {
                                    Toast.makeText(this, "Database update failed: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Password update failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Authentication failed: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
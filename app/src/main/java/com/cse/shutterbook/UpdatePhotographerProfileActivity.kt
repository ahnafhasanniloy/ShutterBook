package com.cse.shutterbook

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class UpdatePhotographerProfileActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etExperience: EditText
    private lateinit var etAboutMe: EditText
    private lateinit var etPassword: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var btnSubmit: Button


    private lateinit var checkboxWedding: CheckBox
    private lateinit var checkboxBirthday: CheckBox
    private lateinit var checkboxOutdoor: CheckBox
    private lateinit var checkboxEvent: CheckBox

    private lateinit var etWeddingCost: EditText
    private lateinit var etWeddingHours: EditText
    private lateinit var etBirthdayCost: EditText
    private lateinit var etBirthdayHours: EditText
    private lateinit var etOutdoorCost: EditText
    private lateinit var etOutdoorHours: EditText
    private lateinit var etEventCost: EditText
    private lateinit var etEventHours: EditText

    private var isPasswordVisible = false
    private lateinit var currentPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_photographer_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etExperience = findViewById(R.id.etExperience)
        etAboutMe = findViewById(R.id.etAboutMe)
        etPassword = findViewById(R.id.etPassword)
        passwordToggle = findViewById(R.id.passwordToggle)
        btnSubmit = findViewById(R.id.btnSubmit)

        checkboxWedding = findViewById(R.id.checkboxWedding)
        checkboxBirthday = findViewById(R.id.checkboxBirthday)
        checkboxOutdoor = findViewById(R.id.checkboxOutdoor)
        checkboxEvent = findViewById(R.id.checkboxEvent)

        etWeddingCost = findViewById(R.id.etWeddingCost)
        etWeddingHours = findViewById(R.id.etWeddingHours)
        etBirthdayCost = findViewById(R.id.etBirthdayCost)
        etBirthdayHours = findViewById(R.id.etBirthdayHours)
        etOutdoorCost = findViewById(R.id.etOutdoorCost)
        etOutdoorHours = findViewById(R.id.etOutdoorHours)
        etEventCost = findViewById(R.id.etEventCost)
        etEventHours = findViewById(R.id.etEventHours)
        etEmail.isEnabled = false
        etEmail.isFocusable = false
        etEmail.inputType = InputType.TYPE_NULL

        loadData()


        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            etPassword.inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }


        setCheckboxListeners()

        btnSubmit.setOnClickListener {
            val phone = etPhone.text.toString().trim()

            if (phone.length != 11) {
                etPhone.error = "Phone number must be 11 digits"
                return@setOnClickListener
            }

            promptCurrentPasswordThenUpdate()
        }
    }

    private fun loadData() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Photographers").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.value as? Map<*, *> ?: return

                    etName.setText(data["name"] as? String)
                    etPhone.setText(data["phone"] as? String)
                    etEmail.setText(data["email"] as? String)
                    etAddress.setText(data["address"] as? String)
                    etExperience.setText(data["experience"]?.toString())
                    etAboutMe.setText(data["aboutMe"] as? String)


                    if (data.containsKey("weddingCost")) {
                        checkboxWedding.isChecked = true
                        etWeddingCost.setText(data["weddingCost"].toString())
                        etWeddingHours.setText(data["weddingHours"].toString())
                        showCategoryFields(checkboxWedding, etWeddingCost, etWeddingHours)
                    }
                    if (data.containsKey("birthdayCost")) {
                        checkboxBirthday.isChecked = true
                        etBirthdayCost.setText(data["birthdayCost"].toString())
                        etBirthdayHours.setText(data["birthdayHours"].toString())
                        showCategoryFields(checkboxBirthday, etBirthdayCost, etBirthdayHours)
                    }
                    if (data.containsKey("outdoorCost")) {
                        checkboxOutdoor.isChecked = true
                        etOutdoorCost.setText(data["outdoorCost"].toString())
                        etOutdoorHours.setText(data["outdoorHours"].toString())
                        showCategoryFields(checkboxOutdoor, etOutdoorCost, etOutdoorHours)
                    }
                    if (data.containsKey("eventCost")) {
                        checkboxEvent.isChecked = true
                        etEventCost.setText(data["eventCost"].toString())
                        etEventHours.setText(data["eventHours"].toString())
                        showCategoryFields(checkboxEvent, etEventCost, etEventHours)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun setCheckboxListeners() {
        setCheckboxToggle(checkboxWedding, etWeddingCost, etWeddingHours)
        setCheckboxToggle(checkboxBirthday, etBirthdayCost, etBirthdayHours)
        setCheckboxToggle(checkboxOutdoor, etOutdoorCost, etOutdoorHours)
        setCheckboxToggle(checkboxEvent, etEventCost, etEventHours)
    }

    private fun setCheckboxToggle(cb: CheckBox, cost: EditText, hours: EditText) {
        cb.setOnCheckedChangeListener { _, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            cost.visibility = visibility
            hours.visibility = visibility
        }
    }

    private fun showCategoryFields(cb: CheckBox, cost: EditText, hours: EditText) {
        cost.visibility = View.VISIBLE
        hours.visibility = View.VISIBLE
    }

    private fun promptCurrentPasswordThenUpdate() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Authentication Required")
        builder.setMessage("Enter your current password to continue:")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Verify") { dialog, _ ->
            val password = input.text.toString()
            reauthenticateAndUpdate(password)
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun reauthenticateAndUpdate(currentPass: String) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return

        val credential = EmailAuthProvider.getCredential(email, currentPass)

        user.reauthenticate(credential).addOnSuccessListener {
            updateToFirebase()
        }.addOnFailureListener {
            Toast.makeText(this, "Authentication failed. Incorrect password.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateToFirebase() {
        val uid = auth.currentUser?.uid ?: return

        val updates = mutableMapOf<String, Any>(
            "name" to etName.text.toString().trim(),
            "phone" to etPhone.text.toString().trim(),
            "address" to etAddress.text.toString().trim(),
            "experience" to etExperience.text.toString().trim(),
            "aboutMe" to etAboutMe.text.toString().trim()
        )


        if (checkboxWedding.isChecked) {
            updates["weddingCost"] = etWeddingCost.text.toString()
            updates["weddingHours"] = etWeddingHours.text.toString()
        } else {
            updates.remove("weddingCost")
            updates.remove("weddingHours")
        }

        if (checkboxBirthday.isChecked) {
            updates["birthdayCost"] = etBirthdayCost.text.toString()
            updates["birthdayHours"] = etBirthdayHours.text.toString()
        }

        if (checkboxOutdoor.isChecked) {
            updates["outdoorCost"] = etOutdoorCost.text.toString()
            updates["outdoorHours"] = etOutdoorHours.text.toString()
        }

        if (checkboxEvent.isChecked) {
            updates["eventCost"] = etEventCost.text.toString()
            updates["eventHours"] = etEventHours.text.toString()
        }


        val newPassword = etPassword.text.toString()
        if (newPassword.isNotEmpty()) {
            auth.currentUser?.updatePassword(newPassword)?.addOnSuccessListener {
                database.child("Photographers").child(uid).updateChildren(updates).addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }?.addOnFailureListener {
                Toast.makeText(this, "Failed to update password: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            database.child("Photographers").child(uid).updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
package com.cse.shutterbook

import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PhotographerSignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAddress: EditText
    private lateinit var etExperience: EditText
    private lateinit var etAboutMe: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var passwordToggle: ImageView
    private lateinit var confirmPasswordToggle: ImageView

    private lateinit var checkboxWedding: CheckBox
    private lateinit var etWeddingCost: EditText
    private lateinit var etWeddingHours: EditText

    private lateinit var checkboxBirthday: CheckBox
    private lateinit var etBirthdayCost: EditText
    private lateinit var etBirthdayHours: EditText

    private lateinit var checkboxOutdoor: CheckBox
    private lateinit var etOutdoorCost: EditText
    private lateinit var etOutdoorHours: EditText

    private lateinit var checkboxEvent: CheckBox
    private lateinit var etEventCost: EditText
    private lateinit var etEventHours: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographer_signup)

        auth = FirebaseAuth.getInstance()

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        etAddress = findViewById(R.id.etAddress)
        etExperience = findViewById(R.id.etExperience)
        etAboutMe = findViewById(R.id.etAboutMe)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        passwordToggle = findViewById(R.id.passwordToggle)
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle)

        checkboxWedding = findViewById(R.id.checkboxWedding)
        etWeddingCost = findViewById(R.id.etWeddingCost)
        etWeddingHours = findViewById(R.id.etWeddingHours)

        checkboxBirthday = findViewById(R.id.checkboxBirthday)
        etBirthdayCost = findViewById(R.id.etBirthdayCost)
        etBirthdayHours = findViewById(R.id.etBirthdayHours)

        checkboxOutdoor = findViewById(R.id.checkboxOutdoor)
        etOutdoorCost = findViewById(R.id.etOutdoorCost)
        etOutdoorHours = findViewById(R.id.etOutdoorHours)

        checkboxEvent = findViewById(R.id.checkboxEvent)
        etEventCost = findViewById(R.id.etEventCost)
        etEventHours = findViewById(R.id.etEventHours)

        setupTogglePassword(passwordToggle, etPassword)
        setupTogglePassword(confirmPasswordToggle, etConfirmPassword)

        setupCategoryVisibility(checkboxWedding, etWeddingCost, etWeddingHours)
        setupCategoryVisibility(checkboxBirthday, etBirthdayCost, etBirthdayHours)
        setupCategoryVisibility(checkboxOutdoor, etOutdoorCost, etOutdoorHours)
        setupCategoryVisibility(checkboxEvent, etEventCost, etEventHours)

        btnSubmit.setOnClickListener {
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()


            if (!phone.matches(Regex("^\\d{11}\$"))) {
                etPhone.background = getDrawable(R.drawable.red_border)
                Toast.makeText(this, "Phone number must be exactly 11 digits", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                etPhone.background = getDrawable(R.drawable.default_edittext_border)
            }


            if (password != confirmPassword) {
                etConfirmPassword.background = getDrawable(R.drawable.red_border)
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                etConfirmPassword.background = getDrawable(R.drawable.default_edittext_border)
            }


            if (!validateCategoryFields()) {
                return@setOnClickListener
            }


            registerPhotographer()
        }
    }

    private fun validateCategoryFields(): Boolean {
        fun isEmpty(field: EditText): Boolean = field.text.toString().trim().isEmpty()

        if (checkboxWedding.isChecked && (isEmpty(etWeddingCost) || isEmpty(etWeddingHours))) {
            Toast.makeText(this, "Enter cost and hours for Wedding", Toast.LENGTH_SHORT).show()
            return false
        }
        if (checkboxBirthday.isChecked && (isEmpty(etBirthdayCost) || isEmpty(etBirthdayHours))) {
            Toast.makeText(this, "Enter cost and hours for Birthday", Toast.LENGTH_SHORT).show()
            return false
        }
        if (checkboxOutdoor.isChecked && (isEmpty(etOutdoorCost) || isEmpty(etOutdoorHours))) {
            Toast.makeText(this, "Enter cost and hours for Outdoor", Toast.LENGTH_SHORT).show()
            return false
        }
        if (checkboxEvent.isChecked && (isEmpty(etEventCost) || isEmpty(etEventHours))) {
            Toast.makeText(this, "Enter cost and hours for Event", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerPhotographer() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                val databaseRef = FirebaseDatabase.getInstance().getReference("Photographers")

                val photographer = Photographer(
                    id = uid,
                    name = etName.text.toString(),
                    phone = etPhone.text.toString(),
                    email = email,
                    address = etAddress.text.toString(),
                    experience = etExperience.text.toString(),
                    aboutMe = etAboutMe.text.toString(),
                    weddingCost = if (checkboxWedding.isChecked) etWeddingCost.text.toString() else null,
                    weddingHours = if (checkboxWedding.isChecked) etWeddingHours.text.toString() else null,
                    birthdayCost = if (checkboxBirthday.isChecked) etBirthdayCost.text.toString() else null,
                    birthdayHours = if (checkboxBirthday.isChecked) etBirthdayHours.text.toString() else null,
                    outdoorCost = if (checkboxOutdoor.isChecked) etOutdoorCost.text.toString() else null,
                    outdoorHours = if (checkboxOutdoor.isChecked) etOutdoorHours.text.toString() else null,
                    eventCost = if (checkboxEvent.isChecked) etEventCost.text.toString() else null,
                    eventHours = if (checkboxEvent.isChecked) etEventHours.text.toString() else null
                )

                databaseRef.child(uid).setValue(photographer).addOnSuccessListener {
                    Toast.makeText(this, "Photographer Registered!", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Database Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCategoryVisibility(checkbox: CheckBox, costField: EditText, hoursField: EditText) {
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            costField.visibility = if (isChecked) EditText.VISIBLE else EditText.GONE
            hoursField.visibility = if (isChecked) EditText.VISIBLE else EditText.GONE
        }
    }

    private fun setupTogglePassword(toggleView: ImageView, passwordField: EditText) {
        var isVisible = false
        toggleView.setOnClickListener {
            isVisible = !isVisible
            passwordField.inputType = if (isVisible)
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordField.setSelection(passwordField.text.length)
            toggleView.setImageResource(
                if (isVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
            )
        }
    }
}
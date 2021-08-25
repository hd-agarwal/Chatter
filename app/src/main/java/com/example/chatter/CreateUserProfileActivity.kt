package com.example.chatter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chatter.databinding.ActivityCreateUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CreateUserProfileActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCreateUserProfileBinding
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private var uri: Uri? = null
    private var username: String? = null
    private var status: String? = null
    private var isFileSelected = false

    companion object {
        private const val STORAGE_PERMISSION_CODE: Int = 123
        private const val SELECT_FILE_CODE: Int = 80
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateUserProfileBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        initUI()
    }

    private fun initUI() {
        this.title = "Personal details"
        _binding.btnProfilePic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                selectFile()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }
        }
        _binding.btnSkip.setOnClickListener {
            intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        _binding.btnContinue.setOnClickListener {
            updateProfile()
            updateUI(true)

        }
        _binding.btnDelete.setOnClickListener {
            isFileSelected = false
            Glide.with(this).load(R.drawable.ic_default_profile_foreground)
                .placeholder(R.drawable.ic_default_profile_foreground)
                .into(_binding.ivUserPhoto)
            _binding.btnDelete.apply {
                visibility = View.INVISIBLE
            }
        }
    }

    private fun updateUI(isUploading: Boolean) {
        _binding.progressBar.apply {
            visibility = if (isUploading) View.VISIBLE else View.INVISIBLE
        }
        _binding.btnDelete.apply {
            isEnabled = !isUploading
        }
        _binding.btnContinue.apply {
            isEnabled = !isUploading
        }
        _binding.btnSkip.apply {
            isEnabled = !isUploading
        }
        _binding.btnProfilePic.apply {
            isEnabled = !isUploading
        }
        _binding.etStatus.apply {
            isEnabled = !isUploading
        }
        _binding.etUsername.apply {
            isEnabled = !isUploading
        }
    }

    private fun selectFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select an image"), SELECT_FILE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile()
        } else {
            Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_FILE_CODE && resultCode == RESULT_OK && data != null && data.data != null) {
            uri = data.data!!
            isFileSelected = true
            _binding.btnProfilePic.apply {
                text = context.getString(R.string.change_profile_button_text)
            }
            Glide.with(this).load(uri)
                .placeholder(R.drawable.ic_default_profile_foreground)
                .into(_binding.ivUserPhoto)
            _binding.btnDelete.apply {
                visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(applicationContext, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile() {
        if (_binding.etUsername.text.toString().isNotEmpty()) {
            username = _binding.etUsername.text.toString()
        }
        if (_binding.etStatus.text.toString().isNotEmpty()) {
            status = _binding.etStatus.text.toString()
        }
        val map: MutableMap<String, Any?> = HashMap()
        map[getString(R.string.username)] = username
        map[getString(R.string.status)] = status

        if (isFileSelected) {
            uri?.let { uploadPhoto(it, map) }
        } else {
            updateDatabase(map)
        }
    }

    private fun uploadPhoto(uri: Uri, map: MutableMap<String, Any?>) {
        firebaseAuth.currentUser?.let {
            firebaseStorage.child("profile_photos")
                .child(it.uid).putFile(uri).addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener { uri ->
                            map[getString(R.string.photo_url)] = uri.toString()
                            updateDatabase(map)
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "An error occurred! Couldn't upload profile photo",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(false)
                }
        }
    }

    private fun updateDatabase(map: MutableMap<String, Any?>) {
        firebaseAuth.currentUser?.let {
            firebaseDatabase.child(getString(R.string.users)).child(it.uid).updateChildren(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT)
                        .show()
                    updateUI(false)
                    intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "An error occurred! Profile not created",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    updateUI(false)
                }
        }
    }


}
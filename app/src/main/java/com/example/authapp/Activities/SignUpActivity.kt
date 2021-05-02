@file:Suppress("PackageName")

package com.example.authapp.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.authapp.CommonUtils.CommonUtils
import com.example.authapp.Models.User
import com.example.authapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*


class SignUpActivity : AppCompatActivity() {
    private val utils: CommonUtils = CommonUtils()
    private lateinit var auth:FirebaseAuth
    private var selectedPhotoUri: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        Log.d("Sign Up - emailpwd", "Page Load")
        //initialised firebase auth
        auth = FirebaseAuth.getInstance()
        //initialised firestore instance
        firestore = FirebaseFirestore.getInstance()

        btnSignUp.setOnClickListener {
            if (utils.isNetworkAvailable(applicationContext)) {
                if (validationCheckInEmailPwdAuth()) {
                    Log.d("Sign Up - emailpwd", "Ok")
                    createAccount(editTextEmail.text.toString(), editTextPassword.text.toString())
                    Log.d("createAccount", auth.currentUser.uid)
                    userDataSave(auth.currentUser.email, editTextUsername.text.toString(), selectedPhotoUri.toString())
//                    startActivity(Intent(applicationContext, SignInActivity::class.java))
                }
            } else {
                val snack = Snackbar.make(it, "No Internet Connect", Snackbar.LENGTH_LONG)
                snack.show()
            }
        }

        textViewChooseImage.setOnClickListener {
            startFileChooser()
        }
    }

    //field validation for email password authentication
    private fun validationCheckInEmailPwdAuth(): Boolean {

        when {
            TextUtils.isEmpty(editTextUsername.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Username",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            TextUtils.isEmpty(editTextEmail.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Email",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text.toString()).matches()) -> {
                Toast.makeText(
                    this,
                    "Invalid email",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            TextUtils.isEmpty(editTextPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please Enter Password",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            TextUtils.isEmpty(
                editTextReEnterPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Please ReEnter Your Password",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            else -> {
                var pwd = editTextPassword.text.toString()
                var repwd = editTextReEnterPassword.text.toString()
                if (pwd == repwd) {
                    return true
                } else {
                    Toast.makeText(
                        this,
                        "Password does not matched.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

            }
        }

        return false
    }

    private fun createAccount(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("createAccount", "createUserWithEmail:success")
//                    val currentUser = auth.currentUser
                    Toast.makeText(baseContext, "Authenticated", Toast.LENGTH_SHORT).show()
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w("createAccount", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    //firebase authentication
    private fun userDataSave(email: String, username: String, photoUri: String){
        var downloadUri: String = ""
        if (selectedPhotoUri != null) {
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("SetingsProfileActivity", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("SettingProfile", "FileLocation $it")
                        downloadUri = it.toString()
                    }
                }
        }

        val user: User = User(email, username, downloadUri)
        firestore.collection("users")
            .document(auth.currentUser.uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Successfully registered", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
            }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "Failed to register", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
            }
    }


    private fun startFileChooser(){
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), 111)
    }

    //Image Setting Method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            imgViewProfileUploader.setImageBitmap(bitmap)
        }
    }

}
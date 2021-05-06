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
import android.view.View
import android.widget.Toast
import com.example.authapp.CommonUtils.CommonUtils
import com.example.authapp.Constants.Constants
import com.example.authapp.Models.ChatRoomModel
import com.example.authapp.Models.UserModel
import com.example.authapp.R
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import kotlinx.android.synthetic.main.activity_sign_up.editTextPassword
import kotlin.collections.ArrayList


class SignUpActivity : AppCompatActivity() {

    private val utils: CommonUtils = CommonUtils()
    private val constant: Constants = Constants()
    private lateinit var auth:FirebaseAuth
    private var selectedPhotoUri: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        Log.d("Sign Up - emailpwd", "Page Load")
        //initialised firebase auth
        auth = FirebaseAuth.getInstance()
        //initialised firestore instance
        firestore = FirebaseFirestore.getInstance()
        progressBarSignUp.visibility = View.GONE
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnGoogleSignUp.setOnClickListener {
            Log.d("btnGoogle", "Clicked")
            progressBarSignUp.visibility = View.VISIBLE
            val googleSignInIntent = googleSignInClient.signInIntent
            startActivityForResult(googleSignInIntent, constant.RC_SIGN_IN)
        }

        btnSignUp.setOnClickListener {
            progressBarSignUp.visibility = View.VISIBLE
            if (utils.isNetworkAvailable(applicationContext)) {
                if (validationCheckInEmailPwdAuth()) {
                    Log.d("Sign Up - emailpwd", "Ok")
                    createAccount(editTextEmail.text.toString(), editTextPassword.text.toString())
                }
            } else {
                val snack = Snackbar.make(it, "No Internet Connect", Snackbar.LENGTH_LONG)
                snack.show()
            }
            progressBarSignUp.visibility = View.GONE
        }

        textViewChooseImage.setOnClickListener {
            startFileChooser()
        }

        callbackManager = CallbackManager.Factory.create()
        buttonFacebookLogin = btnFacebookSignUp
        buttonFacebookLogin.setPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("FB", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("FB", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("FB", "facebook:onError", error)
            }
        })

        textViewSwitchToSignIN.setOnClickListener {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    //on start method
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            finish()
            startActivity(intent)
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
                    val userChatRoomModelList: ArrayList<String> = ArrayList()
                    val currentUser = auth.currentUser
                    userDataSave(currentUser.email, editTextUsername.text.toString(), selectedPhotoUri.toString(), userChatRoomModelList)
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
    private fun userDataSave(email: String, username: String, photoUri: String, userChatRoomModelList: ArrayList<String>){
        Log.d("userDataSave", "Okkkkkkkkkkkkkkkkkkkkk")
        Log.d("URI", photoUri)
        val userModel: UserModel = UserModel(email, username, photoUri, userChatRoomModelList)
        firestore.collection("users")
            .document(auth.currentUser.uid)
            .set(userModel)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Successfully registered", Toast.LENGTH_SHORT).show()
                Log.d("userDataSave", "user collection success")
                finish()
                progressBarSignUp.visibility = View.GONE
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
            }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "Failed to register", Toast.LENGTH_SHORT).show()
                Log.d("userDataSave", it.toString())
                finish()
                progressBarSignUp.visibility = View.GONE
                startActivity(Intent(applicationContext, SignUpActivity::class.java))
            }
    }

    private fun startFileChooser(){
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), 111)
    }

    private fun setProfilePicUrl(selectedPhotoUri: Uri){
        this.selectedPhotoUri = selectedPhotoUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Google Sign in", "$requestCode")
        Log.d("Google Sign in", "${requestCode == constant.RC_SIGN_IN}")
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==constant.RC_IMAGE_URI && resultCode == Activity.RESULT_OK && data != null){
            var tempUri = data.data!!

                val filename = UUID.randomUUID().toString()

                val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

                ref.putFile(tempUri!!)
                    .addOnSuccessListener {
                        Log.d("SetingsProfileActivity", "Successfully uploaded image: ${it.metadata?.path}")

                        ref.downloadUrl.addOnSuccessListener {
                            Log.d("SettingProfile", "FileLocation $it")
                            setProfilePicUrl(it)
                        }

            }
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,tempUri)
            imgViewProfileUploader.setImageBitmap(bitmap)
        }
        else if(requestCode == constant.RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("Google Sign indata", "$data")
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Google Sign in", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Sign in", "$e")
            }
        }
        else{ callbackManager.onActivityResult(requestCode, resultCode, data) }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("firebaseAuthWithGoogle", "signInWithCredential:success")
                    val userChatRoomModelList: ArrayList<String> = ArrayList()
                    val user = auth.currentUser
                    userDataSave(user.email.toString(), user.displayName, user.photoUrl.toString(), userChatRoomModelList)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("firebaseAuthWithGoogle", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("handleFacebook", "handleFacebookAccessToken:$token")
        progressBarSignUp.visibility = View.VISIBLE
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("handleFacebook", "signInWithCredential:success")
                        val userChatRoomModelList: ArrayList<String> = ArrayList()
                        val user = auth.currentUser
                        userDataSave(user.email.toString(), user.displayName, user.photoUrl.toString(), userChatRoomModelList )

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("handleFacebook", "signInWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                }
    }

}
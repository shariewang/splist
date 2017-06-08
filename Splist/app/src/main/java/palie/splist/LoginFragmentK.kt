package palie.splist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginFragmentK : Fragment(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null
    private var email: String? = null
    private var password: String? = null
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var forgotPass: TextView? = null
    private var register: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_login, container, false)
        mAuth = FirebaseAuth.getInstance()

        emailField = v.findViewById(R.id.email) as EditText
        passwordField = v.findViewById(R.id.password) as EditText
        forgotPass = v.findViewById(R.id.forgotPassword) as TextView
        register = v.findViewById(R.id.register) as TextView
        val signInButton = v.findViewById(R.id.sign_in_button) as Button

        signInButton.setOnClickListener(this)
        forgotPass!!.setOnClickListener(this)
        register!!.setOnClickListener(this)

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> {
                email = emailField!!.text.toString()
                password = passwordField!!.text.toString()
                signIn(email!!, password!!)
            }
            R.id.forgotPassword ->
                // TODO: Re-authenticate
                mAuth!!.sendPasswordResetEmail(email!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Check your email for instructions to reset your password.",
                                Toast.LENGTH_LONG).show()
                    }
                }
            R.id.register -> activity.supportFragmentManager
                    .beginTransaction().replace(R.id.content, SignUpFragment()).commit()
        }
    }

    fun signIn(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity) { task ->
            if (!task.isSuccessful) {
                Toast.makeText(activity, "Login failed",
                        Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }
}
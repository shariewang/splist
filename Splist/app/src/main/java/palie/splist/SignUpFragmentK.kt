package palie.splist

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpFragmentK : Fragment(), View.OnClickListener {

    private var name: String? = null
    private var email: String? = null
    private var password: String? = null
    private var emailField: EditText? = null
    private var passwordField: EditText? = null
    private var nameField: EditText? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater!!.inflate(R.layout.fragment_sign_up, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        emailField = v.findViewById(R.id.email) as EditText
        passwordField = v.findViewById(R.id.password) as EditText
        nameField = v.findViewById(R.id.name) as EditText

        return v
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login -> activity.supportFragmentManager
                    .beginTransaction().replace(R.id.content, LoginFragment()).commit()
            R.id.register_button -> {
                name = nameField!!.text.toString()
                email = emailField!!.text.toString()
                password = passwordField!!.text.toString()
                register(email!!, password!!)
            }
        }
    }

    fun register(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(activity, "Registration failed", Toast.LENGTH_SHORT).show()
                    } else {
                        val userRef = db!!.getReference("Users")
                        userRef.child(mAuth!!.currentUser!!.uid).child("name").setValue(name)
                        userRef.child(mAuth!!.currentUser!!.uid).child("email").setValue(email)
                        startActivity(Intent(activity, MainActivity::class.java))
                    }
                }
    }
}
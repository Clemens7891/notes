package at.fh.swengb.ambrosch

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response.success

class MainActivity : AppCompatActivity() {

    companion object {
        val KEY_USER_TOKEN = "KEY_USER_TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val isUserloggedIn: String? = sharedPreferences.getString(KEY_USER_TOKEN, null)

        if (isUserloggedIn == null || isUserloggedIn == ""){

            loginbutton.setOnClickListener{

                val inputUsername: String? = usernamelogin.text.toString()
                val inputPassword: String? = passwordlogin.text.toString()

                if(inputUsername == null || inputUsername == "" || inputPassword == null || inputPassword == ""){

                    Toast.makeText(this, getString(R.string.loginmsg2),Toast.LENGTH_LONG).show()
                } else{


                    val newAuthRequest = AuthRequest(inputUsername, inputPassword)

                    NoteRepository.login(newAuthRequest,
                        success = {
                            val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                            sharedPreferences.edit().putString(KEY_USER_TOKEN, it.token).apply()

                            val intent = Intent(this, NoteListActivity::class.java)
                            startActivity(intent)
                            finish()
                        },
                        error = {
                            Toast.makeText(this, getString(R.string.loginmsg2),Toast.LENGTH_LONG).show()
                        })
                }

            }

        } else{
            val intent = Intent(this, NoteListActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}



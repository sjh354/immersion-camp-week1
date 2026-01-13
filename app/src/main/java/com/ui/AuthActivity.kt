package com.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import com.data.remote.dto.AuthGoogleReq
import com.data.remote.dto.AuthGoogleRes
import com.data.remote.dto.AuthStore
import com.data.repository.UserRepository
import androidx.lifecycle.lifecycleScope
import com.example.test1.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import android.provider.Settings



class AuthActivity : ComponentActivity() {

    private lateinit var credentialManager: CredentialManager

    private val repo = UserRepository(this)

    // 서버 검증용 Web Client ID (Google Cloud Console에서 만든 Web OAuth Client ID)
//    private val webClientId = "1038876443378-2ujktdvpg88aep51kkq55mpcpiq5gfog.apps.googleusercontent.com"
    private val webClientId = "581842099820-juajrh3q9vinkhgb6vrbd0r777idset6.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.login)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        loginBtn.setOnClickListener {
            startSignIn(onlyAuthorized = true)
            Log.d("TESTESTETSETESTEST", "sign in btn pushed")
        }



        credentialManager = CredentialManager.create(this)


        // (1) 이미 서버 토큰이 있으면 메인으로
        val savedAppToken = getSharedPreferences("auth", MODE_PRIVATE).getString("app_token", null)

        if (!savedAppToken.isNullOrBlank()) {
            goMain()
            return
        }

        // (2) 없으면 즉시 구글 로그인 시작
        startSignIn(onlyAuthorized = true)
    }

    private fun openAddGoogleAccount() {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
        startActivity(intent)
    }

    private fun startSignIn(onlyAuthorized: Boolean) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(onlyAuthorized)
            .setAutoSelectEnabled(false)           // ✅ 자동선택 끔(chooser 강제):"
            .build()


        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@AuthActivity, request)

                val googleCred = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = googleCred.idToken

                loginToBackend(idToken)

            } catch (e: androidx.credentials.exceptions.NoCredentialException) {
                Log.e("AUTH", "getCredential failed: ${e::class.java.name} / ${e.message}", e)
                openAddGoogleAccount()
                if (onlyAuthorized) {
                    startSignIn(onlyAuthorized = false) // 🔥 자동 로그인 실패 → 전체 계정 선택 UI 띄우기
                } else {
                    finish()
                }
            } catch (e: Exception) {
                Log.e("AUTH", "getCredential failed: ${e::class.java.name} / ${e.message}", e)
                finish()
            }
        }
    }


    private fun loginToBackend(googleIdToken: String) {
        // Retrofit enqueue로 Flask에 전송 → 서버가 검증 후 app_token 내려줌
        repo.authGoogle(AuthGoogleReq(googleIdToken))
            .enqueue(object : retrofit2.Callback<AuthGoogleRes> {
                override fun onResponse(
                    call: retrofit2.Call<AuthGoogleRes>,
                    response: retrofit2.Response<AuthGoogleRes>
                ) {
                    if (!response.isSuccessful || response.body() == null) return
                    val appToken = response.body()!!.app_token
                    AuthStore.setToken(this@AuthActivity, appToken)
                    goMain()

                }

                override fun onFailure(call: retrofit2.Call<AuthGoogleRes>, t: Throwable) {}
            })
    }

    private fun goMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

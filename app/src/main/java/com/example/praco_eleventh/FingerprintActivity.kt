package com.example.praco_eleventh

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.praco_eleventh.databinding.ActivityFingerprintBinding
import java.util.concurrent.Executor


class FingerprintActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFingerprintBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFingerprintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the executor
        executor = ContextCompat.getMainExecutor(this)

        // Create the BiometricPrompt instance
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // If the authentication succeeds, start the VotingActivity
                    startActivity(Intent(this@FingerprintActivity, VotingActivity::class.java))
                    finish()
                }
            })

            // Create the prompt info
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Touch the fingerprint sensor to authenticate")
                .setNegativeButtonText("Cancel")
                .build()

            // Show the biometric prompt when the user presses the "Authenticate" button
            binding.authenticateButton.setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
        } else {
            // If the device does not have biometric capabilities or the user has not set up biometric authentication,
            // display a message to inform the user
            binding.authenticateButton.isEnabled = false
            binding.messageTextView.text = "Biometric authentication is not available on this device."
        }
    }
}

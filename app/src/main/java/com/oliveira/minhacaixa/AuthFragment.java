package com.oliveira.minhacaixa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.Executor;

public class AuthFragment extends Fragment {

    private AuthViewModel mViewModel;
    private SharedPreferences prefs;
    private TextView welcomeUserText, authPromptText, switchAccountButton;
    private ImageView fingerprintIcon;
    private EditText passwordInput;
    private Button confirmButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = requireActivity().getSharedPreferences("MeuCaixaPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        welcomeUserText = view.findViewById(R.id.welcome_user_text);
        authPromptText = view.findViewById(R.id.auth_prompt_text);
        switchAccountButton = view.findViewById(R.id.switch_account_button);
        fingerprintIcon = view.findViewById(R.id.fingerprint_icon);
        passwordInput = view.findViewById(R.id.password_input);
        confirmButton = view.findViewById(R.id.confirm_button);

        // **NOVA LÓGICA: Adiciona a máscara de data**
        passwordInput.addTextChangedListener(new DateInputMask(passwordInput));

        String loggedInUsername = prefs.getString("LOGGED_IN_USERNAME", null);
        if (loggedInUsername != null) {
            welcomeUserText.setText("Olá, " + loggedInUsername.split(" ")[0]);
            tryDisplayBiometricPrompt(loggedInUsername);
        } else {
            navigateToLogin();
        }

        confirmButton.setOnClickListener(v -> {
            String password = passwordInput.getText().toString().trim();
            mViewModel.login(loggedInUsername, password).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    navigateToDashboard();
                } else {
                    Toast.makeText(getContext(), "Senha incorreta", Toast.LENGTH_SHORT).show();
                }
            });
        });

        switchAccountButton.setOnClickListener(v -> {
            prefs.edit().remove("LOGGED_IN_USERNAME").apply();
            navigateToLogin();
        });
    }

    private void tryDisplayBiometricPrompt(String username) {
        BiometricManager biometricManager = BiometricManager.from(requireContext());
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            displayBiometricPrompt(username);
        } else {
            showPasswordInput();
        }
    }

    private void displayBiometricPrompt(String username) {
        Executor executor = ContextCompat.getMainExecutor(requireContext());

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login no Meu Caixa")
                .setSubtitle("Faça login usando sua digital")
                .setNegativeButtonText("Usar senha")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                navigateToDashboard();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(), "Autenticação falhou", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    showPasswordInput();
                } else {
                    Toast.makeText(getContext(), "Erro de autenticação: " + errString, Toast.LENGTH_SHORT).show();
                }
            }
        });

        biometricPrompt.authenticate(promptInfo);
    }

    private void showPasswordInput() {
        fingerprintIcon.setVisibility(View.GONE);
        authPromptText.setText("Digite sua senha para entrar");
        passwordInput.setVisibility(View.VISIBLE);
        confirmButton.setVisibility(View.VISIBLE);
    }

    private void navigateToLogin() {
        if (isAdded()) {
             NavHostFragment.findNavController(AuthFragment.this).navigate(R.id.action_authFragment_to_loginFragment);
        }
    }

    private void navigateToDashboard() {
         if (isAdded()) {
            NavHostFragment.findNavController(AuthFragment.this).navigate(R.id.action_authFragment_to_dashboardFragment);
        }
    }
}

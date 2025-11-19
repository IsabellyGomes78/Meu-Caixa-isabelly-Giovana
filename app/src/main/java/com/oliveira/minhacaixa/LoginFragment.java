package com.oliveira.minhacaixa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> mGoogleSignInLauncher;

    private Group manualLoginGroup, googleWelcomeGroup;
    private CircleImageView googleProfileImage;
    private TextView googleUserName, googleUserEmail, switchAccountButton;
    private Button googleContinueButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        mGoogleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(requireActivity());
        updateUI(lastSignedInAccount);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Views do Login Manual
        manualLoginGroup = view.findViewById(R.id.manual_login_group);
        EditText emailInput = view.findViewById(R.id.email);
        EditText passwordInput = view.findViewById(R.id.password);
        Button loginButton = view.findViewById(R.id.login_button);
        Button googleLoginButton = view.findViewById(R.id.google_login_button);
        TextView registerTextView = view.findViewById(R.id.register_text_view);
        TextView errorTextView = view.findViewById(R.id.error_text_view);

        // Views do Login Google
        googleWelcomeGroup = view.findViewById(R.id.google_welcome_group);
        googleProfileImage = view.findViewById(R.id.google_profile_image);
        googleUserName = view.findViewById(R.id.google_user_name);
        googleUserEmail = view.findViewById(R.id.google_user_email);
        googleContinueButton = view.findViewById(R.id.google_continue_button);
        switchAccountButton = view.findViewById(R.id.switch_account_button);

        // Ações do Login Manual
        loginButton.setOnClickListener(v -> {
            String emailStr = emailInput.getText().toString();
            String passwordStr = passwordInput.getText().toString();
            mViewModel.login(emailStr, passwordStr).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    navigateToDashboard();
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                }
            });
        });

        googleLoginButton.setOnClickListener(v -> signInWithGoogle());
        registerTextView.setOnClickListener(v -> navigateToRegister());

        // Ações do Login Google
        googleContinueButton.setOnClickListener(v -> navigateToDashboard());
        switchAccountButton.setOnClickListener(v -> {
            mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
                updateUI(null);
            });
        });

        // **NOVA LÓGICA: Observa o status do login/cadastro com Google**
        mViewModel.getGoogleSignInStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                navigateToDashboard();
            }
        });
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            manualLoginGroup.setVisibility(View.GONE);
            googleWelcomeGroup.setVisibility(View.VISIBLE);

            googleUserName.setText(account.getDisplayName());
            googleUserEmail.setText(account.getEmail());
            googleContinueButton.setText("Continuar como " + account.getGivenName());

            Uri personPhoto = account.getPhotoUrl();
            if (personPhoto != null) {
                 googleProfileImage.setImageURI(personPhoto);
            }

        } else {
            manualLoginGroup.setVisibility(View.VISIBLE);
            googleWelcomeGroup.setVisibility(View.GONE);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mGoogleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // **NOVA LÓGICA: Chama o ViewModel para encontrar ou criar o usuário**
            mViewModel.findOrCreateUserFromGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(getContext(), "Login com Google falhou. Tente novamente.", Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    private void navigateToDashboard() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_dashboardFragment);
    }

    private void navigateToRegister() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_registerFragment);
    }
}

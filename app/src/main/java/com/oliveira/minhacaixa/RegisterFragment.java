package com.oliveira.minhacaixa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class RegisterFragment extends Fragment {

    private RegisterViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        TextView loginTextView = view.findViewById(R.id.login_text_view);
        Button registerButton = view.findViewById(R.id.register_button);

        EditText fullName = view.findViewById(R.id.full_name);
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);

        loginTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
        });

        registerButton.setOnClickListener(v -> {
            String fullNameStr = fullName.getText().toString();
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();

            mViewModel.registerUser(fullNameStr, emailStr, passwordStr);
        });

        mViewModel.getRegistrationStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(RegisterFragment.this).popBackStack();
            } else {
                Toast.makeText(getContext(), "Este e-mail já está em uso!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

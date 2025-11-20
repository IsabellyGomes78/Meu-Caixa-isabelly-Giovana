package com.oliveira.minhacaixa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        EditText nameInput = view.findViewById(R.id.name_input);
        EditText birthDateInput = view.findViewById(R.id.birth_date_input);
        Button loginButton = view.findViewById(R.id.login_button);
        TextView registerTextView = view.findViewById(R.id.register_text_view);
        TextView errorTextView = view.findViewById(R.id.error_text_view);

        // **NOVA LÓGICA: Adiciona a máscara de data**
        birthDateInput.addTextChangedListener(new DateInputMask(birthDateInput));

        loginButton.setOnClickListener(v -> {
            String nameStr = nameInput.getText().toString().trim();
            String birthDateStr = birthDateInput.getText().toString().trim();

            if (nameStr.isEmpty() || birthDateStr.isEmpty()) {
                errorTextView.setText("Nome e data de nascimento não podem estar vazios.");
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }

            mViewModel.login(nameStr, birthDateStr).observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    errorTextView.setVisibility(View.GONE);

                    SharedPreferences prefs = requireActivity().getSharedPreferences("MeuCaixaPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putString("LOGGED_IN_USERNAME", user.fullName).apply();

                    NavHostFragment.findNavController(LoginFragment.this)
                            .navigate(R.id.action_loginFragment_to_dashboardFragment);
                } else {
                    errorTextView.setText("Nome ou data de nascimento inválidos.");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            });
        });

        registerTextView.setOnClickListener(v -> {
            errorTextView.setVisibility(View.GONE);
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }
}

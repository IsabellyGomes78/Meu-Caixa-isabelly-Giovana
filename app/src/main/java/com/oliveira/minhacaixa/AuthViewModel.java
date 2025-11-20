package com.oliveira.minhacaixa;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class AuthViewModel extends AndroidViewModel {

    private final UserDao userDao;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
    }

    // Lógica para login com nome e data de nascimento
    public LiveData<User> login(String name, String birthDatePassword) {
        // Reutiliza a mesma lógica de login do LoginViewModel
        return userDao.login(name, birthDatePassword);
    }
}

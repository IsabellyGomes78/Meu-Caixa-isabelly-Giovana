package com.oliveira.minhacaixa;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;

public class RegisterViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final ExecutorService databaseWriteExecutor;

    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    }

    public LiveData<Boolean> getRegistrationStatus() {
        return registrationStatus;
    }

    public void registerUser(String fullName, String email, String password) {
        databaseWriteExecutor.execute(() -> {
            if (userDao.findUserByEmail(email) != null) {
                registrationStatus.postValue(false); // Usuário já existe
            } else {
                User newUser = new User();
                newUser.fullName = fullName;
                newUser.email = email;
                newUser.password = password;
                userDao.insert(newUser);
                registrationStatus.postValue(true); // Cadastro bem-sucedido
            }
        });
    }
}

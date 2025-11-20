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

    public void registerUser(String fullName, String birthDate) {
        databaseWriteExecutor.execute(() -> {
            if (userDao.findUserByFullName(fullName) != null) {
                registrationStatus.postValue(false); // Usuário com este nome já existe
            } else {
                User newUser = new User();
                newUser.fullName = fullName;
                newUser.birthDate = birthDate;
                userDao.insert(newUser);
                registrationStatus.postValue(true); // Cadastro bem-sucedido
            }
        });
    }
}

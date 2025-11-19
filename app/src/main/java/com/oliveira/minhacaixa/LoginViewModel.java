package com.oliveira.minhacaixa;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.concurrent.ExecutorService;

public class LoginViewModel extends AndroidViewModel {

    private final UserDao userDao;
    private final ExecutorService databaseWriteExecutor;

    // LiveData para o status do login com Google
    private final MutableLiveData<Boolean> googleSignInStatus = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    }

    // Retorna o status do login com Google para o Fragment observar
    public LiveData<Boolean> getGoogleSignInStatus() {
        return googleSignInStatus;
    }

    // Lógica para login manual com e-mail e senha
    public LiveData<User> login(String email, String password) {
        return userDao.login(email, password);
    }

    // NOVA LÓGICA: Encontra ou cria um usuário a partir da conta Google
    public void findOrCreateUserFromGoogle(GoogleSignInAccount account) {
        databaseWriteExecutor.execute(() -> {
            User existingUser = userDao.findUserByEmail(account.getEmail());

            if (existingUser == null) {
                // Usuário não existe, cria um novo
                User newUser = new User();
                newUser.fullName = account.getDisplayName();
                newUser.email = account.getEmail();
                // A senha pode ser nula ou um valor aleatório, já que o login será via Google
                newUser.password = "google_signed_in"; 
                userDao.insert(newUser);
            }
            // Notifica o Fragment que o processo (encontrar ou criar) foi concluído
            googleSignInStatus.postValue(true);
        });
    }
}

package com.oliveira.minhacaixa;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    // Procura por um usuário cujo nome contenha o texto de login e a senha (data de nascimento) corresponda
    @Query("SELECT * FROM users WHERE full_name LIKE '%' || :nameQuery || '%' AND birth_date_password = :birthDatePassword LIMIT 1")
    LiveData<User> login(String nameQuery, String birthDatePassword);

    // Procura por um usuário com o mesmo nome para evitar duplicatas no cadastro
    @Query("SELECT * FROM users WHERE full_name = :fullName LIMIT 1")
    User findUserByFullName(String fullName);
}

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

    @Query("SELECT * FROM users WHERE (email = :username OR cpf = :username) AND password = :password LIMIT 1")
    LiveData<User> login(String username, String password);

    @Query("SELECT * FROM users WHERE email = :email OR cpf = :cpf LIMIT 1")
    User findUserByEmailOrCpf(String email, String cpf);
}

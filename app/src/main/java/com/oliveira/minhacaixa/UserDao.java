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

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    LiveData<User> login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findUserByEmail(String email);
}

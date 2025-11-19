package com.oliveira.minhacaixa;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "full_name")
    public String fullName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "birth_date")
    public String birthDate;

    @ColumnInfo(name = "cpf")
    public String cpf;

    @ColumnInfo(name = "password")
    public String password;
}

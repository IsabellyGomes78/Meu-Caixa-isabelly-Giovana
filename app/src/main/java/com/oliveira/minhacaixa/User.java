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

    // A data de nascimento será armazenada como a "senha"
    @ColumnInfo(name = "birth_date_password")
    public String birthDate;

}

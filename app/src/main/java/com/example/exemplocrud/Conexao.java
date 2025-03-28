package com.example.exemplocrud;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Conexao extends SQLiteOpenHelper {
    private static final String name = "banco.db";
    private static final int version = 1;

  public Conexao(Context context){super(context,name,null,version);}

    @Override
    public void onCreate(SQLiteDatabase db) {
       // db.execSQL("create table aluno(id integer primary key autoincrement, "+
         //       "nome varchar(50), cpf varchar(50), telefone varchar(50))");

        db.execSQL("create table aluno(id integer primary key autoincrement, " +"nome varchar(50), " +
                "cpf varchar(50), telefone varchar(50), foto_bytes BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE aluno ADD COLUMN fotoBytes BLOB");
        }
    }
}

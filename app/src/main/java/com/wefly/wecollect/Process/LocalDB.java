package com.wefly.wecollect.Process;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wefly.wecollect.model.Recipient;

public class LocalDB extends SQLiteOpenHelper implements LocalDBInterface{


    //CONSTRUCTORS
    public LocalDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LocalDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    //METHODS
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }


    //Store Recipients
    @Override
    public void saveRecipient(Recipient recipient) {

    }
}

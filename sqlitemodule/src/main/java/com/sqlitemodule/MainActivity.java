package com.sqlitemodule;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private  SQLiteDatabase db;
    private TextView tv_show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StudyHelper studyHelper = new StudyHelper(this);
        db = studyHelper.getWritableDatabase();
        tv_show = (TextView) findViewById(R.id.show);
        findViewById(R.id.insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("insert into "+StudyHelper.TABLE_NAME+"(data,income,cost) values('2016',30,10);");
            }
        });

        findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void selectData(){
        Cursor cursor = db.rawQuery("select count(*),cost from "+StudyHelper.TABLE_NAME+" group by cost",null);
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()){
//            int id = cursor.getInt(cursor.getColumnIndex("id"));
//            stringBuilder.append("id ->");
//            stringBuilder.append(id);
//            String data = cursor.getString(cursor.getColumnIndex("data"));
//            stringBuilder.append(" data ->");
//            stringBuilder.append(data);
//            int income = cursor.getInt(cursor.getColumnIndex("income"));
//            stringBuilder.append(" income ->");
//            stringBuilder.append(income);
            int cost = cursor.getInt(cursor.getColumnIndex("cost"));
            int count = cursor.getInt(cursor.getColumnIndex("count(*)"));
            stringBuilder.append(count);
            stringBuilder.append(" cost ->");
            stringBuilder.append(cost);
            stringBuilder.append("\n");
         }
        cursor.close();
        tv_show.setText(stringBuilder.toString());
    }
}

package com.example.a05062024;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a05062024.databinding.ActivityEkleBinding;

public class Ekle_Activity extends AppCompatActivity {
 private ActivityEkleBinding binding;
 String markalar[]={"Audi","BMW","Mercedes","Renault","Fiat","Toyota","Hyundai","Honda","Nissan","Peugeot","TOGG"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           binding=ActivityEkleBinding.inflate(getLayoutInflater());
              View view=binding.getRoot();
                setContentView(view);

        ArrayAdapter<String>adapterAraçlar=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,markalar);
        binding.spinnerMarka.setAdapter(adapterAraçlar);

        binding.floatingActionButtonAraEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbHelper dbHelper=new DbHelper(Ekle_Activity.this);
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("Marka",binding.spinnerMarka.getSelectedItem().toString());
                values.put("Model",binding.editTextModel.getText().toString());
                values.put("UretimYili",Integer.parseInt(binding.editTextModelYL.getText().toString()));
                db.insert("Araclar",null,values);

                Intent intent=new Intent(Ekle_Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
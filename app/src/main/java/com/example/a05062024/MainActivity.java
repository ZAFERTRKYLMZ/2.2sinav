package com.example.a05062024;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a05062024.databinding.ActivityMainBinding;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Araç> araçlar =new ArrayList<>();
    ArrayAdapter<Araç> adapter;
    String KANAL_ID = "Bildirim"; //Global tanımlanacak

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        //region Bildirim İçin erişim izni
        //region Andorid 13 ve üzeri için Bilfdirim izin penceresinin Açılması için gerekli yapı
        if (Build.VERSION.SDK_INT >= 33) { // Burada eğer android sürümü 13 ve üstü ise ve bildirim izni ekranı daha önce hiç gösterilmemişse aşağıdaki işlemleri yap diyoruz.
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { // Burada uygulamaya bildirim izni verilmemiş ise aşağıdaki işlemleri yap diyoruz.
                ActivityResultLauncher<String> requestPermissionLauncher =
                        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> { // Bildirim izin ekranındaki kullanıcının yaptığı işlemi yani kabul ettiğini veya etmediğini döndürüyor.
                            if (isGranted) // eğer izin verdiyse alttaki yazıyı gösteriyor.
                                Toast.makeText(this, "Tebrikler! Uygulamanın bildirimlerini alacaksınız.", Toast.LENGTH_SHORT).show();
                            else // eğer izin vermediyse alttaki yazıyı gösteriyor.
                                Toast.makeText(this, "Üzgünüz! Uygulamanın bildirimlerini alamayacaksınız.", Toast.LENGTH_SHORT).show();

                        });
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS); // Bildirim izin ekranını gösteriyoruz.
            }
        }

        //endregion
        //endregion
        //region Bildirim Kanal Oluşumu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(KANAL_ID, "Araç", NotificationManager.IMPORTANCE_DEFAULT);

            //Sisteme kanalın kaydedilmesi
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationChannel.enableLights(true);//Bildirim ışığını aç
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(notificationChannel);

        }
        //endregion

        //region db işlemleri
        DbHelper dbHelper=new DbHelper(MainActivity.this);
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM Araclar",null);
        while (cursor.moveToNext()){
            Araç yaniaraç=new Araç();
            yaniaraç.ID=cursor.getInt(cursor.getColumnIndexOrThrow("ID") );
            yaniaraç.Marka=cursor.getString(cursor.getColumnIndexOrThrow("Marka") ); //);
            yaniaraç.Model=cursor.getString(cursor.getColumnIndexOrThrow("Model"));
            yaniaraç.UretimYili=cursor.getInt(cursor.getColumnIndexOrThrow("UretimYili"));
            araçlar.add(yaniaraç);
        }
    binding.textViewDetay.setText("Toplam araç sayısı"+araçlar.size());
    //endregion
        binding.floatingActionButtonEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Ekle_Activity.class);
                startActivity(intent);
            }
        });
        binding.ListViewListele.setAdapter(adapter);

        binding.ListViewListele.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Araç secilenAraç=araçlar.get(i);
                NotificationCompat.Builder bildirimoluşturucu=new NotificationCompat.Builder((MainActivity.this),KANAL_ID);
                bildirimoluşturucu.setSmallIcon(R.drawable.car);
                bildirimoluşturucu.setContentTitle("Araç Bilgileri");
                bildirimoluşturucu.setContentText(secilenAraç.toString());
                bildirimoluşturucu.setAutoCancel(true);
                if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED) {
                }
                NotificationManagerCompat.from(MainActivity.this).notify(1000,bildirimoluşturucu.build());
                }
        });
        binding.ListViewListele.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Araç secilenAraç=araçlar.get(i);
                DbHelper dbHelper=new DbHelper(MainActivity.this);
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                db.delete("Araclar","ID=?",new String[]{String.valueOf(secilenAraç.ID)});
                araçlar.remove(secilenAraç);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
            }
}


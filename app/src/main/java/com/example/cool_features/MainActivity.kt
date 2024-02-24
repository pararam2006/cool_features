package com.example.cool_features

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cool_features.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //переменные для уведомления
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIF_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //СОЗДАНИЕ КАНАЛА УВЕДОМЛЕНИЯ
        fun createNotifChannel() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    lightColor = Color.BLUE
                    enableLights(true)
                }
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }
        createNotifChannel()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        //создание уведомления
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Лучший файлообменник?")
            .setContentText("Конечно же скайп!")
            .setSmallIcon(R.drawable.baseline_check_circle_24)
//            .setLargeIcon(R.drawable.baseline_check_circle_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
//            .addAction(R.drawable.baseline_outlet_24, "Кнопка?", pendingIntent)
            .setAutoCancel(true); // автоматически закрыть уведомление после нажатия


        val notifyManager = NotificationManagerCompat.from(this)


        binding.button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions()
            }
            notifyManager.notify(NOTIF_ID,notif.build())
        }
    }

    //ЗАПРОС РАЗРЕШЕНИЙ
    private fun requestPermissions() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.POST_NOTIFICATIONS)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        Toast.makeText(this@MainActivity, "Все разрешения предоставлены...", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@MainActivity, "Нажмите еще раз", Toast.LENGTH_SHORT).show()

                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(this@MainActivity, "Все разрешения отклонены...", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest?>?, permissionToken: PermissionToken) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(applicationContext, "Ошибка! ", Toast.LENGTH_SHORT).show()
            }
            .onSameThread().check()
    }
}
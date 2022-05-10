package pl.bartulacode.notificationlooper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import pl.bartulacode.notificationlooper.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var index: Int = 0

    private var listNotifications: List<String> = ArrayList<String>()
    private lateinit var editText: EditText


    private fun save() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(
                getString(R.string.preference_name),
                listNotifications.joinToString(separator = "\n")
            )
            apply()
        }
    }

    private fun load() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val savedCheats = sharedPref.getString(getString(R.string.preference_name), "")
        listNotifications = savedCheats?.split("\n") ?: ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        createNotificationChannel()

        editText = findViewById<View>(R.id.editTextTextMultiLine) as EditText

        load()
        editText.setText(listNotifications.joinToString(separator = "\n"))

        val myTimer = Timer()
        myTimer.schedule(object : TimerTask() {
            override fun run() {
                loop()
            }
        }, 0, 15000)

    }


    fun loop() {
        val count = listNotifications.count()

        if (count > 0) {
            val currentIndex = index % count

            addNotification(
                (currentIndex + 1).toString() + " of $count ",
                listNotifications[currentIndex]
            )
            index++;
        }

    }

    fun saveNotifications() {
        listNotifications = editText.text.split("\n");
        save()
        addNotification("Save", listNotifications.joinToString(separator = "\n"))
        index = 0;
    }

    private fun addNotification(title: String, description: String) {
        createNotificationChannel()
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(description)
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val contentIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        builder.setContentIntent(contentIntent)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(index, builder.build())
    }

    private fun createNotificationChannel() {
// Create the NotificationChannel, but only on API 26+ because
// the NotificationChannel class is new and not in the support library
        val name: CharSequence = getString(R.string.channel_name)
        val description = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
        channel.description = description
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
package com.example.floatingwindowapp

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.floatingwindowapp.Common.Companion.currDes

class MainActivity : AppCompatActivity() {
    private lateinit var dialog: AlertDialog
    private lateinit var btnMin : Button
    private lateinit var edtDes: EditText
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnMin = findViewById(R.id.btnMin)
        edtDes = findViewById(R.id.edt_des)
        if (isServiceRunning()){
            stopService(Intent(this@MainActivity,FloatingWindowApp::class.java))
        }
        edtDes.setText(currDes)
        edtDes.setSelection(edtDes.text.toString().length)
        edtDes.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){
                currDes = edtDes.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        btnMin.setOnClickListener {
            if (checkOverlayPermission()){
                startService(Intent(this@MainActivity,FloatingWindowApp::class.java))
                finish()
            }else{
                requestFloatingWindowPermission()
            }
        }



        /*
        isServiceRunning()
        requestPermission()
        checkAndroidVersion()
         */
    }

    private fun isServiceRunning() : Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)){
            if (FloatingWindowApp::class.java.name==service.service.className){
                return true
            }
        }
        return false
    }

    private fun requestFloatingWindowPermission (){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission Needed")
        builder.setMessage("Enable 'Display Over the App 'from settings")
        builder.setPositiveButton("Open Settings",DialogInterface.OnClickListener{dialog,which ->

            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, RESULT_OK)

        })
        dialog = builder.create()
        dialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkOverlayPermission (): Boolean{
        return if (SDK_INT>Build.VERSION_CODES.M){
            Settings.canDrawOverlays(this)

        }
        else return true
    }
}
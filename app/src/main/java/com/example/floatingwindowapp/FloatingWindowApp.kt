package com.example.floatingwindowapp

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import com.example.floatingwindowapp.Common.Companion.currDes

class FloatingWindowApp : Service() {

    private lateinit var floatView : ViewGroup
    private lateinit var floatWindowLayoutParams : WindowManager.LayoutParams
    private var LAYOUT_TYPE : Int?=null
    private lateinit var windowManager: WindowManager
    private lateinit var edtDes:EditText
    private lateinit var btnmax:Button

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        windowManager = getSystemService(WINDOW_SERVICE)as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE)as LayoutInflater
        floatView = inflater.inflate(R.layout.floating_layout,null)as ViewGroup
        btnmax = floatView.findViewById(R.id.btnMax)
        edtDes = floatView.findViewById(R.id.edt_des)

        edtDes.setText(currDes)
        edtDes.setSelection(edtDes.text.toString().length)
        edtDes.isCursorVisible = false

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST

        floatWindowLayoutParams = WindowManager.LayoutParams(
            (width*0.55f).toInt(),
            (height*0.55f).toInt(),
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParams.gravity = Gravity.CENTER
        floatWindowLayoutParams.x = 0
        floatWindowLayoutParams.y = 0

        windowManager.addView(floatView,floatWindowLayoutParams)

        btnmax.setOnClickListener{

            stopSelf()
            windowManager.removeView(floatView)

            val back = Intent(this@FloatingWindowApp,MainActivity::class.java)
            back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(back)

        }
        edtDes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){
                currDes = edtDes.text.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        floatView.setOnTouchListener(object :View.OnTouchListener{

            val updatedFloatWindowLayoutParam = floatWindowLayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0


            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when(event!!.action){
                    MotionEvent.ACTION_DOWN -> {
                        x = updatedFloatWindowLayoutParam.x.toDouble()
                        y = updatedFloatWindowLayoutParam.y.toDouble()

                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }
                    MotionEvent.ACTION_MOVE ->{
                        updatedFloatWindowLayoutParam.x = (x + event.rawX-px).toInt()
                        updatedFloatWindowLayoutParam.y = (y + event.rawY-py).toInt()

                        windowManager.updateViewLayout(floatView,updatedFloatWindowLayoutParam)

                    }

                }
                return false

            }

        })
        edtDes.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                edtDes.isCursorVisible = true
                val updatedFloatParamFlag = floatWindowLayoutParams
                updatedFloatParamFlag.flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                windowManager.updateViewLayout(floatView,updatedFloatParamFlag)
                return false
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        windowManager.removeView(floatView)
    }
}
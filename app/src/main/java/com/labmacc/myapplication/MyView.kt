package com.labmacc.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withRotation
import kotlin.math.PI
import kotlin.math.atan2

const val TAG = "MYDEBUG"

class MyView(context: Context?) : View(context),SensorEventListener2 {

    val size = 1000 //Size in pixels of the compass
    val a = 0.9f //Low-pass filter

    var mLastRotationVector = FloatArray(3) //The last value of the rotation vector
    var mRotationMatrix = FloatArray(9)
    var yaw = 0f
    var compass : Bitmap

    init {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //Register the rotation vector sensor to the listener
        sensorManager.registerListener(
            this,  //use this since MyView implements the listener interface
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL)

        //Read .svg compass
        compass = ResourcesCompat.getDrawable(resources,R.drawable.compass,
            null)?.
        toBitmap(size,size)!!
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            drawColor(Color.YELLOW)
            withRotation (-yaw,width/2f,height/2f) {
                drawBitmap(compass, (width - size) / 2f, (height - size) / 2f, null)
        }
        }
    }


    //Implementation of Event Listener Interface
    override fun onSensorChanged(p0: SensorEvent?) {
        mLastRotationVector = p0?.values?.clone()!! //Get last rotation vector

        //Log.i(TAG,""+mLastRotationVector[0]+""+mLastRotationVector[1]+" "+mLastRotationVector[2])

        //Compute the rotation matrix from the rotation vector
        SensorManager.getRotationMatrixFromVector(mRotationMatrix,mLastRotationVector)

        //Calculate the yaw angle, see slides of the lesson——
        yaw = a*yaw+(1-a)*atan2(mRotationMatrix[1],mRotationMatrix[4])*180f/PI.toFloat()
        invalidate()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        TODO("Not yet implemented")
    }

    override fun onFlushCompleted(p0: Sensor?) {
  //      TODO("Not yet implemented")
    }


}
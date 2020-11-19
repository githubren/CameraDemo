package com.rb.camerademo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.CameraDevice
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceView
import com.rb.camerademo.utils.CameraUtil
import com.rb.camerademo.utils.setOnSurfaceTextureAvailable
import com.rb.camerademo.utils.setOnSurfaceViewAvailable
import com.rb.camerademo.utils.toImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    private var cameraDevice : CameraDevice? = null
    private var bitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surface.setOnSurfaceViewAvailable(
            created = {
                CameraUtil.openCameraBySurfacePreview(
                    this,
                    CameraUtil.getCameraManager(this),
                    surface,
                    onOpened = { camera ->
                        cameraDevice = camera
                        surface.setOnClickListener {
                            val imageReader = ImageReader.newInstance(200, 200, ImageFormat.JPEG, 2)
                            imageReader.setOnImageAvailableListener({ reader ->
                                val image = reader?.acquireNextImage()
                                val buffer = image!!.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                image.close()
                                bitmap = BitmapFactory.decodeByteArray(bytes, 0, 200)
                                Log.e("TAG","bitmap:$bitmap")
                                setContentView(bitmap?.toImageView(this))
                            }, Handler(Looper.getMainLooper()))

                        }
                    })

            },
            destoryed = {
                cameraDevice?.close()
                cameraDevice = null
            })

//        // 设置TextureView加载进度监听
//        textture.setOnSurfaceTextureAvailable { surface, width, height ->
//            // 当TextureView加载完成后, 会调用该方法。
//            // 此时，可以打开摄影头，开始预览
//            CameraUtil.openCameraByTextureViwePreView(this,CameraUtil.getCameraManager(this), textture) {
//                // 打开摄影头成功，设置点击事件
//                textture.setOnClickListener {
//                    // 点击屏幕，拍照。
//                    // textureView.bitmap就是Bitmap图。
//                    // toImageView用于将bitmap转成ImageView用来展示。
//                    setContentView(textture.bitmap.toImageView(this))
//                }
//            }
//        }
    }
}

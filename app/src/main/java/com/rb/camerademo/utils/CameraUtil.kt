package com.rb.camerademo.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

/**
 * Camera2的一些处理
 * @author RenBing
 * @date 2020/11/18 0018
 */
object CameraUtil {

    fun getCameraManager(context: Context) = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    @SuppressLint("MissingPermission")
    fun openCameraBySurfacePreview(
        activity: FragmentActivity,
        cameraManager: CameraManager,
        surfaceView: SurfaceView,
        onError: (CameraDevice,Int) -> Unit = {_: CameraDevice,_:Int ->},
        onOpened: (CameraDevice) -> Unit = {}
    ){
        PermissionX.init(activity)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted){
                    cameraManager.openCamera(cameraManager.cameraIdList[0],object : CameraDevice.StateCallback(){
                        override fun onOpened(camera: CameraDevice) {
                            val targets = listOf(surfaceView.holder.surface)
                            camera.createCaptureSession(targets,object : CameraCaptureSession.StateCallback(){
                                override fun onConfigureFailed(session: CameraCaptureSession) {

                                }

                                override fun onConfigured(session: CameraCaptureSession) {
                                    session.setRepeatingRequest(camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                        .apply { addTarget(surfaceView.holder.surface) }
                                        .build(),null,null)
                                }

                            },null)
                            onOpened.invoke(camera)
                        }

                        override fun onDisconnected(camera: CameraDevice) {

                        }

                        override fun onError(camera: CameraDevice, error: Int) {
                            onError.invoke(camera,error)
                        }

                    }, null)
                }else{
                    Toast.makeText(activity,"请配置相机权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun openCameraByTextureViwePreView(
        activity: FragmentActivity,
        cameraManager: CameraManager,
        textureView: TextureView,
        onError: (CameraDevice, Int) -> Unit = { _: CameraDevice, _: Int -> },
        onOpened: (CameraDevice) -> Unit = {}
    ) {
        PermissionX.init(activity)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    cameraManager.openCamera(
                        cameraManager.cameraIdList[0],
                        object : CameraDevice.StateCallback() {
                            override fun onOpened(camera: CameraDevice) {
                                val targets = listOf(Surface(textureView.surfaceTexture))
                                camera.createCaptureSession(
                                    targets,
                                    object : CameraCaptureSession.StateCallback() {
                                        override fun onConfigureFailed(session: CameraCaptureSession) {

                                        }

                                        override fun onConfigured(session: CameraCaptureSession) {
                                            session.setRepeatingRequest(camera.createCaptureRequest(
                                                CameraDevice.TEMPLATE_PREVIEW
                                            )
                                                .apply { addTarget(Surface(textureView.surfaceTexture)) }
                                                .build(), null, null)
                                            onOpened.invoke(camera)
                                        }

                                    },
                                    null
                                )
                            }

                            override fun onDisconnected(camera: CameraDevice) {

                            }

                            override fun onError(camera: CameraDevice, error: Int) {
                                onError.invoke(camera, error)
                            }

                        },
                        null
                    )
                } else {
                    Toast.makeText(activity, "请配置相机权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

}

fun TextureView.setOnSurfaceTextureAvailable(callback: (surface: SurfaceTexture?, width: Int, height: Int) -> Unit) {

    this.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {

        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            callback.invoke(surface, width, height)
        }

    }
}

fun SurfaceView.setOnSurfaceViewAvailable(created: (holder: SurfaceHolder?) -> Unit,destoryed: (holder: SurfaceHolder?) -> Unit){
    this.holder.addCallback(object : SurfaceHolder.Callback{
        override fun surfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            destoryed.invoke(holder)
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            created.invoke(holder)
        }

    })
}

fun Bitmap.toImageView(context: Context):ImageView{
    return ImageView(context)
        .also {
            it.layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
            200,200
            )
//            it.scaleType = ImageView.ScaleType.FIT_XY
            it.setImageBitmap(this)
        }
}
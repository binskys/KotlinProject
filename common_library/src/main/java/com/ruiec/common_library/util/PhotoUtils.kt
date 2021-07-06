package com.ruiec.common_library.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.ruiec.common_library.Constant.RequestIntentConstant
import java.io.File
import java.io.IOException

/**
 *
 * @author pfm
 * @date 2021/3/16 10:22
 */
object PhotoUtils {

    /**
     * 裁剪图片
     */
    fun cropPhotoBanner(context: Context, uri: Uri?, cropAspectX: Int, cropAspectY: Int): Intent? {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        val file = File(getCropPath(context))
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val outputUri = Uri.fromFile(file)
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(uri, "image/*")
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", cropAspectX)
        intent.putExtra("aspectY", cropAspectY)
        //可裁剪
        intent.putExtra("crop", "true")
        // 裁剪后输出图片的尺寸大小
        //intent.putExtra("outputX", 400);
        //intent.putExtra("outputY", 200);
        //支持缩放
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        //输出图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        //取消人脸识别
        intent.putExtra("noFaceDetection", true)
        intent.putExtra("return-data", false)
        return intent
    }

    /**
     * 裁剪图片
     */
    fun cropPhoto(context: Context, uri: Uri?): Intent? {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        val file = File(getCropPath(context))
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val outputUri = Uri.fromFile(file)
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(uri, "image/*")
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        //可裁剪
        intent.putExtra("crop", "true")
        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 160);
//        intent.putExtra("outputY", 160);
        //支持缩放
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        //输出图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        //取消人脸识别
        intent.putExtra("noFaceDetection", true)
        intent.putExtra("return-data", false)
        return intent
    }

    /**
     * 跳转到相机
     */
    fun toCameraForN(activity: Activity, uri: Uri?) {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        //如果不设置EXTRA_OUTPUT getData()  获取的是bitmap数据  是压缩后的
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        //步骤四：调取系统拍照
        activity.startActivityForResult(intent, RequestIntentConstant.TAKE_PHOTO_REQUEST)
    }

    /**
     * 跳转到相机并裁剪
     */
    fun toCameraForNAndCrop(activity: Activity, uri: Uri?) {
        val intent = Intent()
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        //如果不设置EXTRA_OUTPUT getData()  获取的是bitmap数据  是压缩后的
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        //步骤四：调取系统拍照
        activity.startActivityForResult(intent, RequestIntentConstant.CROP_CAMERA_REQUEST)
    }

    /**
     * 跳转到相册
     */
    fun toPhotoAlbum(activity: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        activity.startActivityForResult(intent, RequestIntentConstant.CHOOSE_PHOTO_REQUEST)
    }

    /**
     * 跳转到相册
     */
    fun toPhotoAlbum(fragment: Fragment) {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        fragment.startActivityForResult(intent, RequestIntentConstant.CHOOSE_PHOTO_REQUEST)
    }

    /**
     * 跳转到相册并裁剪
     */
    fun toPhotoAlbumAndCrop(activity: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        activity.startActivityForResult(intent, RequestIntentConstant.CROP_PHOTO_REQUEST)
    }

    fun createUriForN(activity: Activity?): Uri? {
        // 步骤一：创建存储照片的文件
        val parentFilePath = activity?.getExternalFilesDir(null)?.absolutePath + "/"+ AppUtils.getAppPackageName() + "/image/"
        cropPath = parentFilePath + System.currentTimeMillis() + ".jpg"
        val file = File(cropPath)
        if(!FileUtils.isFileExists(parentFilePath)){
            file.mkdirs()
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 通过FileProvider创建一个content类型的Uri
            val provider = AppUtils.getAppPackageName() + ".provider"
            FileProvider.getUriForFile(activity!!, provider, file)
        } else {
            Uri.fromFile(file)
        }
    }

    var cropPath = ""
    private fun getCropPath(context: Context): String {
        val parentFilePath = context.getExternalFilesDir(null)?.absolutePath + "/"+ AppUtils.getAppPackageName() + "/image/"
        cropPath = parentFilePath + System.currentTimeMillis() + ".jpg"
        val file = File(cropPath)
        if(!FileUtils.isFileExists(parentFilePath)){
            file.mkdirs()
        }
        return file.absolutePath
    }
}
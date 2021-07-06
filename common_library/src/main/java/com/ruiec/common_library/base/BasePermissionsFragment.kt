package com.ruiec.common_library.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

/**
 * 权限配置基类
 * @author pfm
 * @date 2021/2/22 17:13
 */
@RuntimePermissions
open class BasePermissionsFragment : Fragment() {
    private lateinit var grantResults : IntArray

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    open fun requestLocation(){
        openLocation()
    }

    open fun openLocation(){
        LogUtils.d("定位权限已开启")
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    open fun openLocationTip(){
        Toast.makeText(activity, "您已禁止了定位权限，请在应用权限设置内开启", Toast.LENGTH_SHORT).show()
    }

    /**
     * 拨打电话
     * */
    @NeedsPermission(Manifest.permission.CALL_PHONE)
    open fun callPhone(phone : String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel: $phone")
        intent.data = data
        startActivity(intent)
    }

    @OnNeverAskAgain(Manifest.permission.CALL_PHONE)
    open fun callPhoneRefuseTip(){
        Toast.makeText(activity, "您已禁止了拨打电话权限，请在应用权限设置内开启", Toast.LENGTH_SHORT).show()
    }

    /**
     * 选择照片或拍照
     * */
    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    open fun takePhoto() {
        Toast.makeText(activity, "您已开启了相机拍照权限", Toast.LENGTH_SHORT).show()
    }

    /**弹授权框(第一次拒绝后,第二次打开弹出的授权框)**/
//    @OnShowRationale(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    fun showDialog(request: PermissionRequest){
//        Toast.makeText(this, "您已拒绝了相机权限", Toast.LENGTH_SHORT).show()
//    }

    /**弹授权框(第一次拒绝后,第二次打开弹出的授权框)**/
    @OnPermissionDenied(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    open fun deniedTakePhoto(){

    }

    @OnNeverAskAgain(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    open fun takePhotoRefuseTip(){

        if(grantResults[0] != 0){
            Toast.makeText(activity, "您已禁止了相机权限，请在应用权限设置内开启", Toast.LENGTH_SHORT).show()
        }
        if(grantResults.size >1 && grantResults[1] != 0){
            Toast.makeText(activity, "您已禁止了写入权限，请在应用权限设置内开启", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("all")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        this.grantResults = IntArray(grantResults.size)
        this.grantResults = grantResults
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
package com.ruiec.zxing_library

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.google.zxing.*
import com.google.zxing.client.result.ResultParser
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.ruiec.common_library.Constant.RequestIntentConstant
import com.ruiec.zxing_library.DecodeFormatManager.parseDecodeFormats
import com.ruiec.zxing_library.camera.CameraManager
import com.ruiec.zxing_library.result.ResultHandlerFactory
import kotlinx.android.synthetic.main.activity_capture.*
import java.io.IOException
import java.util.*

class CaptureActivity : Activity() ,SurfaceHolder.Callback{


    private val TAG = CaptureActivity::class.java.simpleName

    private val DEFAULT_INTENT_RESULT_DURATION_MS = 1500L
    private val BULK_MODE_SCAN_DELAY_MS = 1000L

    private val ZXING_URLS = arrayOf("http://zxing.appspot.com/scan", "zxing://scan/")

    private val HISTORY_REQUEST_CODE = 0x0000bacc

    private val DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
            ResultMetadataType.SUGGESTED_PRICE,
            ResultMetadataType.ERROR_CORRECTION_LEVEL,
            ResultMetadataType.POSSIBLE_COUNTRY)

    var cameraManager: CameraManager? = CameraManager(this)
    var handler: CaptureActivityHandler? = null

    private var inactivityTimer: InactivityTimer= InactivityTimer(this);
    private var lastResult: Result? = null
    private var source: IntentSource? = null

    private var decodeFormats: AbstractCollection<BarcodeFormat>? = null
    private var decodeHints: Map<DecodeHintType, Any>? = null
    private var characterSet: String? = null
    private var savedResultToShow: Result? = null
    private var hasSurface: Boolean = false

    private lateinit var beepManager: BeepManager
    private var ambientLightManager = AmbientLightManager(this)

    private lateinit var barView : View

    override fun onPause() {
        if (handler != null) {
            handler!!.quitSynchronously()
            handler = null
        }
        inactivityTimer.onPause()
        ambientLightManager.stop()
        beepManager.close()
        cameraManager!!.closeDriver()
        //historyManager = null; // Keep for onActivityResult
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            val surfaceView =
                findViewById<View>(R.id.preview_view) as SurfaceView
            val surfaceHolder = surfaceView.holder
            surfaceHolder.removeCallback(this)
        }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)
        barView = findViewById(R.id.status_bar)

        BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.transparent))
        val linearParams = barView.layoutParams
        linearParams.height = BarUtils.getStatusBarHeight()
        barView.layoutParams = linearParams

        beepManager=BeepManager(this)
        hasSurface = false
    }

    override fun onResume() {
        super.onResume()
        viewfinderView.setCameraManager(cameraManager!!)

        handler = null
        lastResult = null

        beepManager.updatePrefs()
        ambientLightManager.start(cameraManager!!)

        inactivityTimer.onResume()

        val intent = intent


        source = IntentSource.NONE
        decodeFormats = null
        characterSet = null

        if (intent != null) {

            val action = intent.action
            val dataString = intent.dataString

            if (Intents.Scan.ACTION.equals(action)) {
                // Scan the formats the intent requested, and return the result to the calling activity.
                source = IntentSource.NATIVE_APP_INTENT
                decodeFormats = parseDecodeFormats(intent) as AbstractCollection<BarcodeFormat>
                decodeHints = DecodeHintManager.parseDecodeHints(intent)

                if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                    val width = intent.getIntExtra(Intents.Scan.WIDTH, 0)
                    val height = intent.getIntExtra(Intents.Scan.HEIGHT, 0)
                    if (width > 0 && height > 0) {
                        cameraManager!!.setManualFramingRect(width, height)
                    }
                }

                if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
                    val cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1)
                    if (cameraId >= 0) {
                        cameraManager!!.setManualCameraId(cameraId)
                    }
                }


            } else if (dataString != null &&
                    dataString.contains("http://www.google") &&
                    dataString.contains("/m/products/scan")) {

                // Scan only products and send the result to mobile Product Search.
                source = IntentSource.PRODUCT_SEARCH_LINK
                //sourceUrl = dataString
                decodeFormats = DecodeFormatManager.PRODUCT_FORMATS as AbstractCollection<BarcodeFormat>

            } else if (isZXingURL(dataString)) {

                // Scan formats requested in query string (all formats if none specified).
                // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
                source = IntentSource.ZXING_LINK
                //sourceUrl = dataString
                val inputUri = Uri.parse(dataString)
                //scanFromWebPageManager = ScanFromWebPageManager(inputUri)
                decodeFormats = parseDecodeFormats(inputUri) as AbstractCollection<BarcodeFormat>
                // Allow a sub-set of the hints to be specified by the caller.
                decodeHints = DecodeHintManager.parseDecodeHints(inputUri) as Map<DecodeHintType, Any>?

            }

            characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET)

        }

        val surfaceHolder = preview_view.holder
        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder)
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        if (surfaceHolder == null) {
            throw IllegalStateException("No SurfaceHolder provided")

        }
        if (cameraManager!!.isOpen) {
            return
        }
        try {

            cameraManager!!.openDriver(surfaceHolder)
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager!!)
            }
            decodeOrStoreSavedBitmap(null, null)
        } catch (ioe: IOException) {
            Log.w(TAG, ioe)
        } catch (e: RuntimeException) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e)
        }

    }

    private fun decodeOrStoreSavedBitmap(bitmap: Bitmap?, result: Result?) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result
        } else {
            if (result != null) {
                savedResultToShow = result
            }
            if (savedResultToShow != null) {
                val message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow)
                handler!!.sendMessage(message)
            }
            savedResultToShow = null
        }
    }


    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        hasSurface = false
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {

        if (p0 == null) {
        }
        if (!hasSurface) {
            hasSurface = true
            initCamera(p0)
        }
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode   A greyscale bitmap of the camera data which was decoded.
     */
    fun handleDecode(rawResult: Result, barcode: Bitmap?, scaleFactor: Float) {
        inactivityTimer.onActivity()
        lastResult = rawResult

        val resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult)
        val codestr=rawResult.text
        contents_text_view.text=codestr
    }

    fun drawViewfinder() {
        viewfinderView.drawViewfinder()
    }

    private fun isZXingURL(dataString: String?): Boolean {
        if (dataString == null) {
            return false
        }
        for (url in ZXING_URLS) {
            if (dataString.startsWith(url)) {
                return true
            }
        }
        return false
    }

    data class StartReceive(var code:String,var message:String)

    fun openAlbum(view: View) {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, RequestIntentConstant.CHOOSE_PHOTO_REQUEST)
    }

    fun back(view: View){
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResultImage(requestCode, resultCode, data)
    }

    private fun onActivityResultImage(requestCode : Int, resultCode : Int, data: Intent?){
        if(data == null){
            return
        }
        val uri =data.data
        val path = UriUtils.uri2File(uri).absolutePath
        handleDecodeInternally(parsePathBitmap(path), null)
    }

    private fun handleDecodeInternally(rawResult: Result?, barcode: Bitmap?){
        beepManager.playBeepSoundAndVibrate()
        val result = ResultParser.parseResult(rawResult).displayResult.toString().trim()
        ToastUtils.showShort(result)
    }

    /**
     * 解析二维码图片,返回结果封装在Result对象中
     */
    private fun parsePathBitmap(bitmapPath: String): Result? {
        //解析转换类型UTF-8
        val hints =
            Hashtable<DecodeHintType, String?>()
        hints[DecodeHintType.CHARACTER_SET] = "utf-8"
        //获取到待解析的图片
        val options = BitmapFactory.Options()
        //如果我们把inJustDecodeBounds设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true
        //此时的bitmap是null，这段代码之后，options.outWidth 和 options.outHeight就是我们想要的宽和高了
        var scanBitmap = BitmapFactory.decodeFile(bitmapPath, options)
        //我们现在想取出来的图片的边长（二维码图片是正方形的）设置为400像素
        /**
         * options.outHeight = 400;
         * options.outWidth = 400;
         * options.inJustDecodeBounds = false;
         * bitmap = BitmapFactory.decodeFile(bitmapPath, options);
         */
        //以上这种做法，虽然把bitmap限定到了我们要的大小，但是并没有节约内存，如果要节约内存，我们还需要使用inSimpleSize这个属性
        options.inSampleSize = options.outHeight / 400
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1 //防止其值小于或等于0
        }
        /**
         * 辅助节约内存设置
         *
         * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
         * options.inPurgeable = true;
         * options.inInputShareable = true;
         */
        options.inJustDecodeBounds = false
        scanBitmap = BitmapFactory.decodeFile(bitmapPath, options)
        //新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        val intArray =
            IntArray(scanBitmap.width * scanBitmap.height)
        scanBitmap.getPixels(
            intArray,
            0,
            scanBitmap.width,
            0,
            0,
            scanBitmap.width,
            scanBitmap.height
        )
        val rgbLuminanceSource =
            RGBLuminanceSource(scanBitmap.width, scanBitmap.height, intArray)
        //将图片转换成二进制图片
        val binaryBitmap = BinaryBitmap(HybridBinarizer(rgbLuminanceSource))
        //初始化解析对象
        val reader = QRCodeReader()
        //开始解析
        var result: Result? = null
        try {
            result = reader.decode(binaryBitmap, hints)
        } catch (e: Exception) {
            // TODO: handle exception
        }
        return result
    }
}

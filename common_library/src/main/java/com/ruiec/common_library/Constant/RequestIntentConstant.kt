package com.ruiec.common_library.Constant

/**
 *
 * @author pfm
 * @date 2021/2/23 17:49
 */
object RequestIntentConstant {
    const val BEAN = "Bean"
    const val REQUEST_CODE_SCAN = 100

    /** 选择相册并裁剪  */
    const val CROP_PHOTO_REQUEST = 108
    /** 拍照并裁减 */
    const val CROP_CAMERA_REQUEST = 109
    /** 拍照 */
    const val TAKE_PHOTO_REQUEST = 110
    /** 相册 */
    const val CHOOSE_PHOTO_REQUEST = 111
    /** 文件 */
    const val CHOOSE_FILE_REQUEST = 112

    /** 裁剪后返回的结果  */
    const val CROP_RESULT_REQUEST = 210
}
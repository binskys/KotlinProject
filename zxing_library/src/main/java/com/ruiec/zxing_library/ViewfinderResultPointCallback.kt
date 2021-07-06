package com.ruiec.zxing_library

import com.google.zxing.ResultPoint
import com.google.zxing.ResultPointCallback

internal class ViewfinderResultPointCallback(private val viewfinderView: ViewfinderView) : ResultPointCallback {

    override fun foundPossibleResultPoint(point: ResultPoint) {
        viewfinderView.addPossibleResultPoint(point)
    }

}
package jp.gr.java_conf.qkuronekop.gesturetransformableview.detector

import android.view.MotionEvent
import jp.gr.java_conf.qkuronekop.gesturetransformableview.listener.RotateGestureListener

class RotateGestureDetector() {

  var deltaAngle = 0f
  private var downX = 0f
  private var downY = 0f
  private var downX2 = 0f
  private var downY2 = 0f
  private var isFirstPointerUp = false

  var rotationGestureListener: RotateGestureListener? = null

  @Synchronized fun onTouchEvent(event: MotionEvent): Boolean {

    val eventX = event.x
    val eventY = event.y
    val count = event.pointerCount

    when (event.action and MotionEvent.ACTION_MASK) {
      MotionEvent.ACTION_DOWN -> {
        downX = eventX
        downY = eventY
        if (count >= 2) {
          downX2 = event.getX(1)
          downY2 = event.getY(1)
        }
      }
      MotionEvent.ACTION_POINTER_DOWN -> {
        downX2 = event.getX(1)
        downY2 = event.getY(1)
      }
      MotionEvent.ACTION_MOVE ->

        if (count >= 2) {

          // 回転角度の取得
          val angle = getAngle(downX, downY, downX2, downY2, eventX, eventY, event.getX(1),
              event.getY(1))

          // 画像の回転
          if (angle != SLOPE_0) {
            this.deltaAngle -= (angle * 180.0 / Math.PI).toFloat()
          }

          downX2 = event.getX(1)
          downY2 = event.getY(1)

          rotationGestureListener?.onRotation(this)
        }
      MotionEvent.ACTION_POINTER_UP -> {
        if (event.getPointerId(0) == 0) {
          isFirstPointerUp = true
        }
      }
    }

    if (isFirstPointerUp) {
      downX = downX2
      downY = downY2
      isFirstPointerUp = false
    } else {
      downX = eventX
      downY = eventY
    }

    return true
  }

  companion object {

    val SLOPE_0 = 10000f

    private fun getAngle(xi1: Float, yi1: Float, xm1: Float, ym1: Float, xi2: Float, yi2: Float,
        xm2: Float, ym2: Float): Float {

      if (!(xm1 - xi1 != 0f && ym1 - yi1 != 0f) || !(xm2 - xi2 != 0f && ym2 - yi2 != 0f)) {
        return SLOPE_0
      }

      // 2本の直線の傾き・y切片を算出
      val firstLinearSlope = (xm1 - xi1) / (ym1 - yi1)
      val secondLinearSlope = (xm2 - xi2) / (ym2 - yi2)

      if (firstLinearSlope * secondLinearSlope == -1f) {
        return 90.0f
      }

      val tan = (secondLinearSlope - firstLinearSlope) / (1 + secondLinearSlope * firstLinearSlope)

      val degree = Math.atan(tan.toDouble()).toFloat()

      return degree
    }
  }
}


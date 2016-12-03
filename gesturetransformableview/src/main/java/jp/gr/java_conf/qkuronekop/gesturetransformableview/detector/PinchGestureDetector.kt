package jp.gr.java_conf.qkuronekop.gesturetransformableview.detector

import android.view.MotionEvent
import jp.gr.java_conf.qkuronekop.gesturetransformableview.listener.PinchGestureListener

class PinchGestureDetector() {

    var scale = 1.0f

    var distance = 0f

    var preDistance = 0f

    var pinchGestureListener : PinchGestureListener? = null

    @Synchronized fun onTouchEvent(event: MotionEvent): Boolean {

        val eventX = event.x * scale
        val eventY = event.y * scale
        val count = event.pointerCount

        val action = event.action and MotionEvent.ACTION_MASK
        val actionPointerIndex = event.action and MotionEvent.ACTION_POINTER_INDEX_MASK

        when (action) {
            MotionEvent.ACTION_DOWN -> {
            }
        /** 最初のpointしか来ない  */
            MotionEvent.ACTION_MOVE -> {

                if (count == 2) {

                    val multiTouchX = event.getX(1) * scale
                    val multiTouchY = event.getY(1) * scale

                    distance = cirulcDistance(eventX, eventY, multiTouchX, multiTouchY)
                    pinchGestureListener?.onPinchGestureListener(this)
                    scale *= distance / preDistance
                    preDistance = distance

                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {

                /** 2本の位置を記録　以後、moveにて距離の差分を算出  */

                if (count == 2) {
                    val downId = actionPointerIndex shr MotionEvent.ACTION_POINTER_INDEX_SHIFT

                    val multiTouchX = event.getX(downId) * scale
                    val multiTouchY = event.getY(downId) * scale

                    distance = cirulcDistance(eventX, eventY, multiTouchX, multiTouchY)
                    pinchGestureListener?.onPinchGestureListener(this)
                    preDistance = distance
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {

                distance = 0f
                preDistance = 0f
                scale = 1.0f
            }
        }
        return false
    }

    private fun cirulcDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x1 - x2
        val dy = y1 - y2
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }
}

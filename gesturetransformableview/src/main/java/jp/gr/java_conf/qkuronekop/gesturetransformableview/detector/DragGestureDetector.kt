package jp.gr.java_conf.qkuronekop.gesturetransformableview.detector

import android.view.MotionEvent
import jp.gr.java_conf.qkuronekop.gesturetransformableview.listener.DragGestureListener
import java.util.*

class DragGestureDetector() {

    var deltaX = 0f

    var deltaY = 0f

    private var originalIndex: Int = 0

    private val pointMap = HashMap<Int, TouchPoint>()


    init {
        pointMap.put(0, createPoint(0f, 0f))
        originalIndex = 0
    }

    var dragGestureListener: DragGestureListener? = null

    @Synchronized fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.pointerCount >= 3) {
            return false
        }

        val eventX = event.getX(originalIndex)
        val eventY = event.getY(originalIndex)

        val action = event.action and MotionEvent.ACTION_MASK
        val actionPointer = event.action and MotionEvent.ACTION_POINTER_INDEX_MASK

        when (action) {
            MotionEvent.ACTION_DOWN -> {

                /** 最初のpointしか来ない  */

                var downPoint: TouchPoint? = pointMap[0]
                if (downPoint != null) {
                    downPoint.setXY(eventX, eventY)
                } else {
                    downPoint = createPoint(eventX, eventY)
                    pointMap.put(0, downPoint)
                }

                originalIndex = 0
            }
            MotionEvent.ACTION_MOVE -> {

                val originalPoint = pointMap[originalIndex]
                if (originalPoint != null) {
                    deltaX = eventX - originalPoint.x
                    deltaY = eventY - originalPoint.y

                    dragGestureListener?.onDragGestureListener(this)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {

                val downId = actionPointer shr MotionEvent.ACTION_POINTER_INDEX_SHIFT

                val multiTouchX = event.getX(downId)
                val multiTouchY = event.getY(downId)

                val p = pointMap[downId]

                if (p != null) {
                    p.x = multiTouchX
                    p.y = multiTouchY
                } else {
                    pointMap.put(downId, createPoint(multiTouchX, multiTouchY))
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {

                val upId = actionPointer shr MotionEvent.ACTION_POINTER_INDEX_SHIFT

                if (originalIndex == upId) {
                    /** 起点の指が離れた  */
                    pointMap.remove(upId)

                    (0..(pointMap.size - 1))
                            .filter { it -> originalIndex != it }
                            .map { pointMap[it]?.setXY(event.getX(it), event.getY(it)) }
                }
            }
        }
        return false
    }

    private fun createPoint(x: Float, y: Float): TouchPoint {
        return TouchPoint(x, y)
    }

    internal inner class TouchPoint(var x: Float, var y: Float) {

        fun setXY(x: Float, y: Float): TouchPoint {
            this.x = x
            this.y = y
            return this
        }
    }
}


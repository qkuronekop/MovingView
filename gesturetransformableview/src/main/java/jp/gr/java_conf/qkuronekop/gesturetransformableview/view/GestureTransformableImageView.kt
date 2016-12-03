package jp.gr.java_conf.qkuronekop.gesturetransformableview.view

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import jp.gr.java_conf.qkuronekop.gesturetransformableview.detector.DragGestureDetector
import jp.gr.java_conf.qkuronekop.gesturetransformableview.detector.PinchGestureDetector
import jp.gr.java_conf.qkuronekop.gesturetransformableview.detector.RotateGestureDetector
import jp.gr.java_conf.qkuronekop.gesturetransformableview.listener.DragGestureListener
import jp.gr.java_conf.qkuronekop.gesturetransformableview.listener.PinchGestureListener
import jp.gr.java_conf.qkuronekop.gesturetransformableview.listener.RotateGestureListener

class GestureTransformableImageView : ImageView, OnTouchListener, DragGestureListener, PinchGestureListener, RotateGestureListener {

  private var limitScaleMax = DEFAULT_LIMIT_SCALE_MAX

  private var limitScaleMin = DEFAULT_LIMIT_SCALE_MIN

  private var scaleFactor = 1.0f

  private var rotateGestureDetector: RotateGestureDetector? = null

  private var dragGestureDetector: DragGestureDetector? = null

  private var pinchGestureDetector: PinchGestureDetector? = null

  private var angle: Float = 0.toFloat()

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs,
      defStyle) {
    init(context, GESTURE_DRAGGABLE or GESTURE_ROTATABLE or GESTURE_SCALABLE)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init(context, GESTURE_DRAGGABLE or GESTURE_ROTATABLE or GESTURE_SCALABLE)
  }

  constructor(context: Context) : super(context) {
    init(context, GESTURE_DRAGGABLE or GESTURE_ROTATABLE or GESTURE_SCALABLE)
  }

  constructor(context: Context, gestureFlag: Int) : super(context) {
    init(context, gestureFlag)
  }

  fun setLimitScaleMax(limit: Float) {
    this.limitScaleMax = limit
  }

  fun setLimitScaleMin(limit: Float) {
    this.limitScaleMin = limit
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    rotateGestureDetector?.rotationGestureListener = this
    dragGestureDetector?.dragGestureListener = this
    pinchGestureDetector?.pinchGestureListener = this
  }

  override fun onTouch(v: View, event: MotionEvent): Boolean {
    rotateGestureDetector?.onTouchEvent(event)
    dragGestureDetector?.onTouchEvent(event)
    pinchGestureDetector?.onTouchEvent(event)

    return true
  }

  private fun init(context: Context, gestureFlag: Int) {

    setOnTouchListener(this)

    if (gestureFlag and GESTURE_DRAGGABLE == GESTURE_DRAGGABLE) {
      dragGestureDetector = DragGestureDetector()
    }
    if (gestureFlag and GESTURE_ROTATABLE == GESTURE_ROTATABLE) {
      rotateGestureDetector = RotateGestureDetector()
    }
    if (gestureFlag and GESTURE_SCALABLE == GESTURE_SCALABLE) {
      pinchGestureDetector = PinchGestureDetector()
    }

  }

  private fun rotateXY(centerX: Float, centerY: Float, angle: Float, x: Float, y: Float): PointF {

    val rad = Math.toRadians(angle.toDouble())

    val resultX = ((x - centerX) * Math.cos(rad) - (y - centerY) * Math.sin(
        rad) + centerX).toFloat()
    val resultY = ((x - centerX) * Math.sin(rad) + (y - centerY) * Math.cos(
        rad) + centerY.toDouble()).toFloat()

    return PointF(resultX, resultY)
  }

  override fun onDragGestureListener(dragGestureDetector: DragGestureDetector) {
    var dx = dragGestureDetector.deltaX
    var dy = dragGestureDetector.deltaY
    val pf = rotateXY(0f, 0f, angle, dx, dy)

    dx = pf.x
    dy = pf.y

    x += dx * scaleFactor
    y += dy * scaleFactor
  }

  override fun onPinchGestureListener(dragGestureDetector: PinchGestureDetector) {

    val scale = dragGestureDetector.distance / dragGestureDetector.preDistance
    val tmpScale = scaleFactor * scale

    if (limitScaleMin <= tmpScale && tmpScale <= limitScaleMax) {
      scaleFactor = tmpScale
      scaleX = scaleFactor
      scaleY = scaleFactor

      return
    }
  }

  override fun onRotation(detector: RotateGestureDetector) {
    angle += detector.deltaAngle

    rotation += detector.deltaAngle
  }

  override fun onRotationBegin(detector: RotateGestureDetector) {
  }

  override fun onRotationEnd(detector: RotateGestureDetector) {
  }

  companion object {

    val GESTURE_DRAGGABLE = 0x0001

    val GESTURE_ROTATABLE = 0x0002

    val GESTURE_SCALABLE = 0x0004

    val DEFAULT_LIMIT_SCALE_MAX = 6.0f

    val DEFAULT_LIMIT_SCALE_MIN = 0.2f
  }
}

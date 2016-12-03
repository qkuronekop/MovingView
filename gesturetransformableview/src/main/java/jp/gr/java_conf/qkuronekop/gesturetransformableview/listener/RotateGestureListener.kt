package jp.gr.java_conf.qkuronekop.gesturetransformableview.listener

import jp.gr.java_conf.qkuronekop.gesturetransformableview.detector.RotateGestureDetector

/**
 * Created by satomitusjimura on 2016/12/03.
 */
interface RotateGestureListener {

  fun onRotation(detector: RotateGestureDetector)

  fun onRotationBegin(detector: RotateGestureDetector)

  fun onRotationEnd(detector: RotateGestureDetector)
}

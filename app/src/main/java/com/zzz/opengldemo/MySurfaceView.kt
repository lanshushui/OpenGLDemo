package com.zzz.opengldemo

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 *  author : chentao
 *  date : 2020/12/18
 *  email: chentao3@yy.com
 */
//   Manifest文件设置  <uses-feature android:glEsVersion="0x00020000" android:required="true" />
class MySurfaceView @JvmOverloads constructor(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    /*
    * 1. 伴生对象中成员变量初始化
    *2. 伴生对象中init代码块按先后顺序执行
    * 3. 类的init代码块按先后顺序执行
    * 4. 类的主构造函数
    * 5. 类的次构造函数
    * */
    init {
        setEGLContextClientVersion(2)
        setRenderer(MySurfaceViewRender())
    }
}
package com.zzz.opengldemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *  author : chentao
 *  date : 2020/12/18
 *  email: chentao3@yy.com
 */
class MySurfaceViewRender :GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle
    private lateinit var mSquare: Square

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // initialize a triangle
        mTriangle = Triangle()
        // initialize a square
        mSquare = Square()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        //设置视窗大小及位置为整个view范围
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //设置屏幕背景色RGBA
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        //清除深度缓冲与颜色缓冲
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)

        mTriangle.draw()
    }
}
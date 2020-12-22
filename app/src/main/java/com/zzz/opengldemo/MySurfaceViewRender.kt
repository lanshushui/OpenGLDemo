package com.zzz.opengldemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
    private lateinit var openGLImage: OpenGLImage
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // initialize a triangle
        mTriangle = Triangle()
        // initialize a square
        mSquare = Square()

        openGLImage= OpenGLImage()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

        //设置视窗大小及位置为整个view范围
        GLES20.glViewport(0, 0, width, height)

        /**透视投影(符合人们心理习惯，即离视点近的物体大，离视点远的物体小)
         * Matrix.orthoM (float[] m,           //接收正交投影的变换矩阵
         *   int mOffset,        //变换矩阵的起始位置（偏移量）
         *   float left,         //相对观察点近面的左边距
         *   float right,        //相对观察点近面的右边距
         *   float bottom,       //相对观察点近面的下边距
         *   float top,          //相对观察点近面的上边距
         *   float near,         //相对观察点近面距离
         *   float far)          //相对观察点远面距离
         *   <link>  R.drawable.frustumm.jpg
         */
        //Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)

        /**
         * Computes an orthographic projection matrix.
         *
         * @param m returns the result 目标矩阵，这个数组的长度至少有16个元素，这样它才能存储正交投影矩阵；
         * @param mOffset 结果矩阵起始的偏移量
         * @param left x轴的最小范围
         * @param right x轴的最大范围
         * @param bottom y轴的最小范围
         * @param top y轴的最大范围
         * @param near z轴的最小范围
         * @param far z轴的最大范围
         */
        //正交投影(无论物体距离相机多远，投影后的物体大小尺寸不变)
        if(width>height){
            val ratio: Float = width.toFloat() / height.toFloat()
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        }else{
            val ratio: Float = height.toFloat() / width.toFloat()
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, 3f, 7f)
        }

        //右手坐标系  x向右，y向上，z轴面向我们
        //  //接收相机变换矩阵
        Matrix.setLookAtM(viewMatrix, 0, //变换矩阵的起始位置（偏移量）
            0f, 0f, 3f, //相机位置
            0f, 0f, 0f, //观察点位置
            0f, 1.0f, 0.0f) //up向量在xyz上的分量
        // （摄影机相当于人眼，upx,upy,upz相当于头的方向，当upy=1时，人眼才是朝向z轴）
        //https://www.jianshu.com/p/21315a4b39b4
    }

    override fun onDrawFrame(gl: GL10?) {
        //设置屏幕背景色RGBA
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        //清除深度缓冲与颜色缓冲
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)


        /**
         *  Matrix.multiplyMM (float[] result, //接收相乘结果
         *  int resultOffset,  //接收矩阵的起始位置（偏移量）
         *  float[] lhs,       //左矩阵
         *  int lhsOffset,     //左矩阵的起始位置（偏移量）
         *  float[] rhs,       //右矩阵
         *  int rhsOffset)     //右矩阵的起始位置（偏移量）
         */
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

       // mTriangle.initMvpMatrix(vPMatrix)
        //mTriangle.draw()
        //mSquare.draw()
        openGLImage.initMvpMatrix(vPMatrix)
        openGLImage.draw()
    }
}
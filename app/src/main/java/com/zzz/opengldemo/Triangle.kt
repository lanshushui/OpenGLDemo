package com.zzz.opengldemo

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *  author : chentao
 *  date : 2020/12/18
 *  email: chentao3@yy.com
 */

class Triangle : Figure() {

    //每个顶点的坐标数
    val COORDS_PER_VERTEX = 3
    var mvpMatrix: FloatArray? = null
    var lastChange: Long = 0;

    /**
     * 请注意，此形状的坐标是按照逆时针顺序定义的。绘制顺序非常重要
     * 因为它定义了哪一边是形状的正面（您通常想要绘制的那一面）
     * 哪一边是背面（您可以使用 OpenGL ES 面剔除功能选择不绘制的那一面）
     */
    var triangleCoords = floatArrayOf(
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
    )

    // 红，绿，蓝，透明度
    var color = floatArrayOf(1f, 1f, 1f, 1f)
    private var vertexBuffer: FloatBuffer =
        // 初始化ByteBuffer，长度为数组的长度*4，因为一个float占4个字节
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // 使用设备硬件的本机字节顺序
            order(ByteOrder.nativeOrder())

            // 创建FloatBuffer
            asFloatBuffer().apply {
                // 放数据
                put(triangleCoords)
                // 设置缓冲区读取位置为第一个坐标
                position(0)
            }
        }

    private var vPMatrixHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX //顶点数

    init {
        //mProgram创建后便可以获得下面变量 不需要调用 GLES20.glUseProgram(mProgram)才获得变量
        // GLES20.glUseProgram(mProgram) 每次绘画都要调用
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
    }

    override fun draw() {
        //将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram)
        mvpMatrix?.let {
            //传入矩阵
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, it, 0)
        }

        positionHandle.let {

            // 启用三角形顶点的手柄
            GLES20.glEnableVertexAttribArray(it)

            // 准备三角形坐标数据
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX, //每个顶点的坐标数 3
                GLES20.GL_FLOAT,  //float类型
                false,
                COORDS_PER_VERTEX * 4, // 每个顶点占用的字节
                vertexBuffer
            )
        }

        mColorHandle.let {
            if (System.currentTimeMillis() - lastChange > 1000) {
                var red: Float = (0..100).random() / 100f
                var blue: Float = (0..100).random() / 100f
                var green: Float = (0..100).random() / 100f
                color = floatArrayOf(red, blue, green, 1.0f)
                lastChange = System.currentTimeMillis()
            }
            // 设置绘制三角形的颜色
            GLES20.glUniform4fv(it, 1, color, 0)
        }

        // 画三角形 顶点绘制法
        /**
         * GL_POINTS	将传入的顶点坐标作为单独的点绘制
         * GL_LINES	将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
         * GL_LINE_STRIP	将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
         * GL_LINE_LOOP	将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
         * GL_TRIANGLES	将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
         * GL_TRIANGLE_STRIP	将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
         * GL_TRIANGLE_FAN	将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
         */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(positionHandle)

    }


    override fun getVertexShaderCode(): String {
        return "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +

                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"
    }

    fun initMvpMatrix(mvpMatrix: FloatArray) {
        this.mvpMatrix = mvpMatrix;
    }


}
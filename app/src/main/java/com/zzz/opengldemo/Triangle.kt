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
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
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
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 每个顶点占用的字节


    override fun draw() {
        //将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram)

        mvpMatrix?.let {
            vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
            //传入矩阵
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, it, 0)
        }

        // 获取顶点着色器的vPosition成员的句柄
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // 启用三角形顶点的手柄
            GLES20.glEnableVertexAttribArray(it)

            // 准备三角形坐标数据
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX, //每个顶点的坐标数 3
                GLES20.GL_FLOAT,  //float类型
                false,
                vertexStride, // 所有顶点占用的字节
                vertexBuffer
            )

            // 获取片段着色器的vColor成员的句柄
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // 设置绘制三角形的颜色
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // 画三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

            // 禁用顶点数组
            GLES20.glDisableVertexAttribArray(it)
        }
    }


    override fun getVertexShaderCode(): String {
        return "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +

                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"
    }

    fun initMvpMatrix(mvpMatrix: FloatArray) {
        this.mvpMatrix=mvpMatrix;
    }


}
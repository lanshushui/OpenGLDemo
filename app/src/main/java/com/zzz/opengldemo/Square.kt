package com.zzz.opengldemo

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 *  author : chentao
 *  date : 2020/12/18
 *  email: chentao3@yy.com
 */

// number of coordinates per vertex in this array

class Square :Figure() {
    //每个顶点的坐标数
    val COORDS_PER_VERTEX = 3
    var squareCoords = floatArrayOf(
        -0.5f, 0.5f, 0.0f,      // top left
        -0.5f, -0.5f, 0.0f,      // bottom left
        0.5f, -0.5f, 0.0f,      // bottom right
        0.5f, 0.5f, 0.0f       // top right
    )

    //怎么绘制顶点 0 1 2  和 0 2 3 绘制两个三角形来绘制正方形
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    // 红，绿，蓝，透明度
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    // 初始化顶点字节缓冲区
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // 初始化绘制顺序字节缓冲区
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    override fun draw() {
        //将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram)


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
                COORDS_PER_VERTEX*4, // 每个顶点占用的字节
                vertexBuffer
            )

            // 获取片段着色器的vColor成员的句柄
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->

                // 设置绘制三角形的颜色
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // 画正方形
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // 禁用顶点数组
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}




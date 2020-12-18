package com.zzz.opengldemo

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

class Square {
    //每个顶点的坐标数
    val COORDS_PER_VERTEX = 3
    var squareCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f,      // top left
        -0.5f, -0.5f, 0.0f,      // bottom left
        0.5f, -0.5f, 0.0f,      // bottom right
        0.5f,  0.5f, 0.0f       // top right
    )
    //怎么绘制顶点 0 1 2  和 0 2 3 绘制两个三角形来绘制正方形
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

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
}

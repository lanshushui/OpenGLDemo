package com.zzz.opengldemo

import android.graphics.BitmapFactory
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


/**
 *  author : chentao
 *  date : 2020/12/21
 *  email: chentao3@yy.com
 */
class OpenGLImage : Figure() {
    //每个顶点的坐标数
    val COORDS_PER_VERTEX = 2
    var mTextureId: Int = -1

    //opengl坐标轴 前后3个坐标绘制两个三角形，构成正方形
    private val sPos = floatArrayOf(
        -1.0f, 1.0f,  //左上角
        -1.0f, -1.0f,  //左下角
        1.0f, 1.0f,  //右上角
        1.0f, -1.0f //右下角
    )

    //纹理坐标 android的纹理坐标左上为原点
    private val sCoord = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )
    private var vertexBuffer: FloatBuffer =
        // 初始化ByteBuffer，长度为数组的长度*4，因为一个float占4个字节
        ByteBuffer.allocateDirect(sPos.size * 4).run {
            // 使用设备硬件的本机字节顺序
            order(ByteOrder.nativeOrder())

            // 创建FloatBuffer
            asFloatBuffer().apply {
                // 放数据
                put(sPos)
                // 设置缓冲区读取位置为第一个坐标
                position(0)
            }
        }
    private var coordBuffer: FloatBuffer =
        // 初始化ByteBuffer，长度为数组的长度*4，因为一个float占4个字节
        ByteBuffer.allocateDirect(sCoord.size * 4).run {
            // 使用设备硬件的本机字节顺序
            order(ByteOrder.nativeOrder())

            // 创建FloatBuffer
            asFloatBuffer().apply {
                // 放数据
                put(sCoord)
                // 设置缓冲区读取位置为第一个坐标
                position(0)
            }
        }

    private var mvpMatrix: FloatArray? = null
    private var vPMatrixHandle: Int = 0
    private var vCoordinateHandle: Int = 0
    private var vTextureHandle: Int = 0

    init {
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        vCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate")
        vTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")
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
                COORDS_PER_VERTEX, //每个顶点的坐标数 2
                GLES20.GL_FLOAT,  //float类型
                false,
                COORDS_PER_VERTEX * 4, // 每个顶点占用的字节
                vertexBuffer
            )
        }

        vCoordinateHandle.let {

            // 启用三角形顶点的手柄
            GLES20.glEnableVertexAttribArray(it)

            // 准备三角形坐标数据
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX, //每个顶点的坐标数 2
                GLES20.GL_FLOAT,  //float类型
                false,
                COORDS_PER_VERTEX * 4, // 每个顶点占用的字节
                coordBuffer
            )
        }

        if (mTextureId == -1) {
            loadTexture()
        }
        // 绘制顶点 ，方式有顶点法和索引法
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLE_STRIP,
            0,
            sPos.size / COORDS_PER_VERTEX
        ); // 顶点法，按照传入渲染管线的顶点顺序及采用的绘制方式将顶点组成图元进行绘制

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(vCoordinateHandle);

    }

    fun initMvpMatrix(mvpMatrix: FloatArray) {
        this.mvpMatrix = mvpMatrix;
    }

    /**
     * 1.代码中获取到shader中改sampler的句柄
     * 2.设置纹理单元与shader中sampler的关系 GLES20.glUniform1i
     * 3.绑定目标纹理  GLES20.glActiveTexture   GLES20.glBindTexture
     */
    fun loadTexture() {
        val img =
            BitmapFactory.decodeResource(LocalApplication.context.resources, R.drawable.frustumm)
        val textures = intArrayOf(-1)

        GLES20.glGenTextures(1, textures, 0)
        mTextureId = textures[0]

        GLES20.glUniform1i(vTextureHandle, 0);  //绑定到0号纹理单位
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  //激活0号纹理单位
        //默认情况下，调用函数glBindTexture(GL_TEXTURE_2D, textureID);会默认将当前纹理关联至GL_TEXTURE0
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)// 将纹理ID绑定到当前活动的纹理单元上

        //纹理过滤参数，指定当我们渲染出来的纹理比原来的纹理小或者大时要如何处理
        // 这里我们使用了GL_LINEAR的方式，这是一种线性插值的方式，得到的结果会更平滑
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        //纹理也有坐标系，称UV坐标，或者ST坐标
        // S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_REPEAT.toFloat()
        )
        // T轴的拉伸方式为重复
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_REPEAT.toFloat()
        )

        val pixels = ByteBuffer.allocateDirect(img.getByteCount())
        pixels.order(ByteOrder.nativeOrder())
        img.copyPixelsToBuffer(pixels)
        pixels.position(0)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            img.getWidth(),
            img.getHeight(),
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            pixels
        )
        img.recycle()

    }

    override fun getVertexShaderCode(): String {
        return "attribute vec4 vPosition;" +
                "attribute vec2 vCoordinate;" +
                "uniform mat4 vMatrix;" +
                "varying vec2 aCoordinate;" +
                "void main(){" +
                "    gl_Position=vMatrix*vPosition;" +
                "    aCoordinate=vCoordinate;" +
                "}"
    }

    /**
     * 顶点着色器中默认精度：
    precision highp float;
    precision highp int;
    precision lowp sampler2D;
    precision lowp samplerCube;

    像素着色器中默认精度
    precision mediump int;
    precision lowp sampler2D;
    precision lowp samplerCube;
    float 没有默认精度
     */
    override fun getFragmentShaderCode(): String {
        return "precision mediump float;" +
                "uniform sampler2D vTexture;" +
                "varying vec2 aCoordinate;" +
                "void main(){" +
                "    gl_FragColor=texture2D(vTexture,aCoordinate);" +
                "}"
    }
}
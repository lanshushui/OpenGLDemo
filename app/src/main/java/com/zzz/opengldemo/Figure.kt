package com.zzz.opengldemo

import android.opengl.GLES20

/**
 *  author : chentao
 *  date : 2020/12/18
 *  email: chentao3@yy.com
 */
abstract class Figure {


    var mProgram: Int
    var positionHandle: Int = 0
    var mColorHandle: Int = 0

    init {
        var vertexShaderCode = getVertexShaderCode()
        var fragmentShaderCode = getFragmentShaderCode()
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }
    }

    /**
     * OpenGL ES2.0 的三种变量类型（uniform，attribute和varying）
     * attribute变量是只能在vertex shader中使用的变量。（它不能在fragment shader中声明attribute变量，也不能被fragment shader中使用）
     *
     *
     * 如果uniform变量在vertex和fragment两者之间声明方式完全一样，则它可以在vertex和fragment共享使用
     * uniform变量是外部application程序传递给（vertex和fragment）shader的变量。因此它是application通过函数glUniform**（）函数赋值的
     *
     * varying变量是vertex和fragment shader之间做数据传递用的。一般vertex shader修改varying变量的值，然后fragment shader使用该varying变量的值。
     * 因此varying变量在vertex和fragment shader二者之间的声明必须是一致的。application不能使用此变量。
     */

    open fun getVertexShaderCode(): String {
        return "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = vPosition;" +
                "}"
    }

    open fun getFragmentShaderCode(): String {
        return "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"
    }



    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    abstract fun draw()
}
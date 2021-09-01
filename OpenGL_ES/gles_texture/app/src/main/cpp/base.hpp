//
// Created by thrha on 2021-08-26.
//

#include <jni.h>
#include <android/log.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <vector>

#include <opencv2/opencv.hpp>

#define  LOG_TAG    "TAG_libgl2jni"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace cv;

int w, h;

GLuint loadShader(GLenum shaderType, const char* pSource) {

    GLuint shader = glCreateShader(shaderType);
    glShaderSource(shader, 1, &pSource, NULL);
    glCompileShader(shader);

    int success;
    char infoLog[512];

    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
    if (!success)
    {
        glGetShaderInfoLog(shader, 512, NULL, infoLog);
        LOGD("ERROR::SHADER::COMPILATION_FAILED %s",infoLog);
    }

    return shader;
}

GLuint createProgram(const char* pVertexSource, const char* pFragmentSource) {

    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, pVertexSource);
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, pFragmentSource);

    GLuint program = glCreateProgram();

    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);

    glLinkProgram(program);

    int success;
    char infoLog[512];

    glGetProgramiv(program, GL_LINK_STATUS, &success);
    if (!success) {
        glGetProgramInfoLog(program, 512, NULL, infoLog);
        LOGD("ERROR::SHADER::PROGRAM::LINKING_FAILED %s",infoLog);
    }

    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);

    return program;
}

Mat mPMatrix = Mat::eye(4, 4, CV_32F);
Mat mMMatrix = Mat::eye(4, 4, CV_32F);

/*float *mMVPMatrix = new float[16];
float *mMMatrix = new float[16];
float *mVMatrix = new float[16];
float *mMVMatrix = new float[16];
float *mPMatrix = new float[16];*/

void myOrthof(float left, float right,float bottom, float top,float near, float far) {

    mPMatrix.at<float>(0) = 2.0f / (right - left);
    mPMatrix.at<float>(5) = -2.0f / (top - bottom);
    mPMatrix.at<float>(10) = 2.0f / (far - near);

    mPMatrix.at<float>(12) = - (right + left) / (right - left);
    mPMatrix.at<float>(13) = (top + bottom) / (top - bottom);
    mPMatrix.at<float>(14) = (far + near) / (far - near);

}


float vertices[] = {
        -1.0f, -1.0f, 0.0f,       // top
        1.0f, -1.0f, 0.0f,       // bottom left
        1.0f, 1.0f, 0.0f,       // bottom right
        -1.0f, 1.0f, 0.0f,       // bottom right
};

float vertices1[] = {
        0.0f, 0.0f, 0.0f,       // top
        500.0f, 0.0f, 0.0f,       // bottom left
        500.0f, 500.0f, 0.0f,       // bottom right
        0.0f, 500.0f, 0.0f,       // bottom right
};

float vertices2[] = {
        -0.5f, -1.0f, 0.0f,       // top
        1.0f, -1.0f, 0.0f,       // bottom left
        1.0f, 1.0f, 0.0f,       // bottom right
        -0.5f, 1.0f, 0.0f,       // bottom right
};

float texture[] = {
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f
};

/*const char* gVertexShader = "uniform mat4 uPMatrix;\n"
                            "uniform mat4 uMMatrix;\n"
                            "attribute vec4 aPosition;\n"
                            "attribute vec2 aTextureCoord;\n"
                            "varying vec2 vTextureCoord;\n"
                            "\n"
                            "void main () \n"
                            "{\n"
                            "    gl_Position = vec4(1.0, -1.0, -1.0, 1.0) * uPMatrix * ( uMMatrix * aPosition);\n"
                            "    vTextureCoord = aTextureCoord;\n"
                            "}\n";*/

const char* gVertexShader = "uniform mat4 uPMatrix;\n"
                            "uniform mat4 uMMatrix;\n"
                            "attribute vec4 aPosition;\n"
                            "attribute vec2 aTextureCoord;\n"
                            "varying vec2 vTextureCoord;\n"
                            "\n"
                            "void main () \n"
                            "{\n"
                            "    gl_Position = uPMatrix * ( uMMatrix * aPosition );\n"
                            "    vTextureCoord = aTextureCoord;\n"
                            "}\n";

const char* gFragmentShader = "precision mediump float;\n"
                              "varying vec2 vTextureCoord;\n"
                              "uniform sampler2D sTexture;\n"
                              "\n"
                              "void main()\n"
                              "{\n"
                              "    gl_FragColor = texture2D(sTexture, vTextureCoord); // Fragment Shader에서 시스템으로 전달할  값을 할당한다\n"
                              "}\n";
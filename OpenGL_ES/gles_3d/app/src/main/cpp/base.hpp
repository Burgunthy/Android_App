//
// Created by thrha on 2021-08-30.
//

#ifndef GLES_3D_BASE_HPP
#define GLES_3D_BASE_HPP

#include <jni.h>
#include <android/log.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <vector>

#include <opencv2/opencv.hpp>

// open GL libs
#include <GLES2/gl2.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <glm/gtx/rotate_vector.hpp>
#include <glm/gtx/closest_point.hpp>

#define  LOG_TAG    "TAG_libgl2jni"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace cv;

int w, h;
std::vector<Mat> img;

glm::mat4 mPMatrix = glm::mat4(1.0f);
glm::mat4 mMMatrix = glm::mat4(1.0f);

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
/*
void myPerspective(float fovy, float aspect, float near, float far) {

    float f = 1 / tanf(fovy * (float)M_PI / 180);

    mPMatrix.at<float>(0) = f / aspect;
    mPMatrix.at<float>(5) = f;
    mPMatrix.at<float>(10) = (near + far) / (near - far);

    mPMatrix.at<float>(11) = -1.f;
    mPMatrix.at<float>(14) = (2.f * near * far) / (near - far);

}

void myTranslatef(float x, float y, float z) {

    mMMatrix.at<float>(12) += x;
    mMMatrix.at<float>(13) += y;
    mMMatrix.at<float>(14) += z;

}

void myRotatef(float angle, int x, int y, int z) {

    Mat buf = Mat::eye(4, 4, CV_32F);

    float r = angle * (float)M_PI / 180;

    float c = cosf(r);
    float s = sinf(r);

    buf.at<float>(0) = (1 - c) * (float)pow(x, 2) + c;
    buf.at<float>(4) = (1 - c) * x * y - s * z;
    buf.at<float>(8) = (1 - c) * x * z + s * y;

    buf.at<float>(1) = (1 - c) * x * y + s * z;
    buf.at<float>(5) = (1 - c) * (float)pow(y, 2) + c;
    buf.at<float>(9) = (1 - c) * y * z - s * x;

    buf.at<float>(2) = (1 - c) * x * z - s * y;
    buf.at<float>(6) = (1 - c) * y * z + s * x;
    buf.at<float>(10) = (1 - c) * (float)pow(z, 2) + c;

    mMMatrix = buf * mMMatrix;

}*/

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

float vertices[] = {
        // 앞면
        -1.0f, -1.0f, 1.0f, // 왼쪽 아래 정점
        1.0f, -1.0f, 1.0f,  // 오른쪽 아래
        -1.0f, 1.0f, 1.0f,  // 왼쪽 위
        1.0f, 1.0f, 1.0f,   // 오른쪽 위

        // 오른쪽 면
        1.0f, -1.0f, 1.0f,  // 왼쪽 아래
        1.0f, -1.0f, -1.0f, // 오른쪽 아래
        1.0f, 1.0f, 1.0f,   // 왼쪽 위
        1.0f, 1.0f, -1.0f,  // 오른쪽 위

        // 뒷면
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,

        // 왼쪽면
        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        -1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,

        // 아래쪽 면
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,

        // 위쪽면
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
};

short indexes[] = {
        0,1,3, 0,3,2,           //앞면을 구성하는 2개의 3각형
        4,5,7, 4,7,6,           //오른쪽면
        8,9,11, 8,11,10,        //...
        12,13,15, 12,15,14,
        16,17,19, 16,19,18,
        20,21,23, 20,23,22,
};

float texture[] = {
        //6개의 면에 매핑될 텍스쳐 좌표 24개를  선언한다
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,

        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,

        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,

        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,

        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,

        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f,
};

#endif //GLES_3D_BASE_HPP

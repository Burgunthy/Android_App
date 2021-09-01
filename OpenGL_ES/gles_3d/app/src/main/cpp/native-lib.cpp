#include <jni.h>
#include <string>

#include "base.hpp"

GLuint mProgram;
GLuint maPositionHandle, maTextureHandle, muPMatrixHandle, muMMatrixHandle;
GLuint mTextureID = 0;

void initHandle() {

    maPositionHandle = static_cast<GLuint>(glGetAttribLocation(mProgram, "aPosition"));
    maTextureHandle = static_cast<GLuint>(glGetAttribLocation(mProgram, "aTextureCoord"));
    muPMatrixHandle = static_cast<GLuint>(glGetUniformLocation(mProgram, "uPMatrix"));
    muMMatrixHandle = static_cast<GLuint>(glGetUniformLocation(mProgram, "uMMatrix"));

}

unsigned char *pColor;

void initTexture() {

    glGenTextures(1, &mTextureID);
    glBindTexture(GL_TEXTURE_2D, mTextureID);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 640, 480, 0, GL_RGB, GL_UNSIGNED_BYTE, img[0].data);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

}

void init() {
    glClearColor(0.f, 0.f, 0.f, 1.f);

    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT_OES, GL_FASTEST);

    glFrontFace(GL_CCW);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);

    mProgram = createProgram(gVertexShader, gFragmentShader);
    initHandle();
    initTexture();
}

int COORDS_PER_VERTEX = 3;
int vertexCount = 12 / COORDS_PER_VERTEX;
int vertexStride = COORDS_PER_VERTEX * sizeof(float);

float a = 0.f;

void draw_model() {

    if(a == 360.f) a = 0.f;
    a += 1.f;

    mPMatrix = glm::mat4(1.0f);         // ProjectionView
    mMMatrix = glm::mat4(1.0f);         // ModelView

    // ProjectionView의 perspective 선언
    mPMatrix = glm::perspective(45.0f, (float)w /(float)h, 1.0f, 500.0f);

    // ModelView -10만큼 z 방향으로 이동
    mMMatrix = glm::translate(mMMatrix, glm::vec3(0.f, 0.f, -10.f));
    // ModelView -45도 회전
    mMMatrix = glm::rotate(mMMatrix, glm::radians(-45.f), glm::vec3(0.0f, 1.0f, 0.f));
    // ModelView a도 만큼 회전
    mMMatrix = glm::rotate(mMMatrix, glm::radians(a), glm::vec3(1.0f, 0.0f, -1.f));

    glUseProgram(mProgram);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, mTextureID);   // Fragment Shader로 텍스쳐를 전달한다(sample2D)

    // projection, modelview 행렬 shader 연결
    glUniformMatrix4fv(muPMatrixHandle, 1, GL_FALSE, glm::value_ptr(mPMatrix));
    glUniformMatrix4fv(muMMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMMatrix));

    // vertex buffer 연결
    glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX, GL_FLOAT, GL_FALSE, 12, vertices);
    glEnableVertexAttribArray(maPositionHandle);

    // texture 지정
    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, GL_FALSE, 8, texture);
    glEnableVertexAttribArray(maTextureHandle);

    for(int i = 0; i < 6; i++) {
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indexes + 2 * 3 * i);
    }
}

void draw_model2() {

    mPMatrix = glm::mat4(1.0f);
    mMMatrix = glm::mat4(1.0f);

    mPMatrix = glm::perspective(45.0f, (float)w /(float)h, 1.0f, 500.0f);

    mMMatrix = glm::translate(mMMatrix, glm::vec3(3.f, 0.f, -20.f));
    mMMatrix = glm::rotate(mMMatrix, glm::radians(a), glm::vec3(0.0f, 1.0f, 0.f));

    glUseProgram(mProgram);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, mTextureID);   // Fragment Shader로 텍스쳐를 전달한다(sample2D)

    glUniformMatrix4fv(muPMatrixHandle, 1, GL_FALSE, glm::value_ptr(mPMatrix));
    glUniformMatrix4fv(muMMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMMatrix));

    glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX, GL_FLOAT, GL_FALSE, 12, vertices);
    glEnableVertexAttribArray(maPositionHandle);

    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, GL_FALSE, 8, texture);
    glEnableVertexAttribArray(maTextureHandle);

    for(int i = 0; i < 6; i++) {
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indexes + 2 * 3 * i);
    }
}

void draw() {

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    draw_model();
    draw_model2();

    glFlush();
}


extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_13d_GL2JNILib_init(
        JNIEnv* env,
        jobject /* this */) {

    init();

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_13d_GL2JNILib_resize(
        JNIEnv* env,
        jobject instance,
        jint width, jint height) {

    w = width;
    h = height;

    glViewport(0, 0, w, h);

    LOGD("width : %d, height : %d", w, h);

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_13d_GL2JNILib_draw(
        JNIEnv* env,
        jobject /* this */) {

    draw();
}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_13d_GL2JNILib_setImageAddr(
        JNIEnv* env,
        jobject instance,
        jlong imgAddr) {

    Mat &input_image = *(Mat *)imgAddr;

    img.push_back(input_image);
}
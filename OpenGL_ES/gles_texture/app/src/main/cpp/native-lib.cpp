// OpenGL ES 2.0 code

#include "base.hpp"

GLuint mProgram;
GLuint maPositionHandle, maTextureHandle, muPMatrixHandle, muMMatrixHandle;
GLuint mTextureID = 0;

std::vector<Mat> img;

unsigned char *pColor;

void initHandle() {

    maPositionHandle = static_cast<GLuint>(glGetAttribLocation(mProgram, "aPosition"));
    maTextureHandle = static_cast<GLuint>(glGetAttribLocation(mProgram, "aTextureCoord"));
    muPMatrixHandle = static_cast<GLuint>(glGetUniformLocation(mProgram, "uPMatrix"));
    muMMatrixHandle = static_cast<GLuint>(glGetUniformLocation(mProgram, "uMMatrix"));

}

void initTexture() {

    glGenTextures(1, &mTextureID);
    glBindTexture(GL_TEXTURE_2D, mTextureID);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 640, 480, 0, GL_RGBA, GL_UNSIGNED_BYTE, img[0].data);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

}

void init() {

    glClearColor(0.f, 0.f, 0.f, 1.f);

    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    //glEnable(GL_CULL_FACE);

    //glCullFace(GL_BACK);

    mProgram = createProgram(gVertexShader, gFragmentShader);
    initHandle();
    initTexture();
}

int COORDS_PER_VERTEX = 3;
int vertexCount = 12 / COORDS_PER_VERTEX;
int vertexStride = COORDS_PER_VERTEX * sizeof(float);

void draw_img() {

    //glFrontFace(GL_CW);

    myOrthof(0.f, (float)w, 0.f, (float)h, -1.f, 1.f);

    glUseProgram(mProgram);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, mTextureID);   // Fragment Shader로 텍스쳐를 전달한다(sample2D)

    glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX, GL_FLOAT, GL_FALSE, 12, vertices1);
    glEnableVertexAttribArray(maPositionHandle);

    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, GL_FALSE, 8, texture);
    glEnableVertexAttribArray(maTextureHandle);

    glUniformMatrix4fv(muPMatrixHandle, 1, GL_FALSE, (float*)mPMatrix.data);
    glUniformMatrix4fv(muMMatrixHandle, 1, GL_FALSE, (float*)mMMatrix.data);

    glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

}

void draw_model() {

    myOrthof(0.f, (float)w, 0.f, (float)h, -1.f, 1.f);

    glUseProgram(mProgram);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, mTextureID);   // Fragment Shader로 텍스쳐를 전달한다(sample2D)

    glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX, GL_FLOAT, GL_FALSE, 12, vertices1);
    glEnableVertexAttribArray(maPositionHandle);

    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, GL_FALSE, 8, texture);
    glEnableVertexAttribArray(maTextureHandle);

    glUniformMatrix4fv(muPMatrixHandle, 1, GL_FALSE, (float*)mPMatrix.data);
    glUniformMatrix4fv(muMMatrixHandle, 1, GL_FALSE, (float*)mMMatrix.data);

    glDrawArrays(GL_TRIANGLE_FAN, 0, vertexCount);

}

void draw() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    draw_img();

    glFlush();

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_1texture_GL2JNILib_init(
        JNIEnv* env,
        jobject /* this */) {

    init();

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_1texture_GL2JNILib_resize(
        JNIEnv* env,
        jobject instance,
        jint width, jint height) {

    w = width;
    h = height;

    LOGD("width : %d, height : %d", w, h);

    vertices1[3] = (float)w;
    vertices1[6] = (float)w;

    vertices1[7] = (float)h;
    vertices1[10] = (float)h;

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_1texture_GL2JNILib_draw(
        JNIEnv* env,
        jobject /* this */) {

    draw();
}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_gles_1texture_GL2JNILib_setImageAddr(
        JNIEnv* env,
        jobject instance,
        jlong imgAddr) {

    Mat &input_image = *(Mat *)imgAddr;
    //input_image.convertTo(input_image, CV_8UC3);

    img.push_back(input_image);

    //pColor = input_image.data;
}
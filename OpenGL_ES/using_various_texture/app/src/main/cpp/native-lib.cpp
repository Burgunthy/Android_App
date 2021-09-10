#include "base.h"

void initHandle() {

    maPositionHandle = static_cast<GLuint>(glGetAttribLocation(mProgram, "aPosition"));
    maTextureHandle = static_cast<GLuint>(glGetAttribLocation(mProgram, "aTextureCoord"));
    muPMatrixHandle = static_cast<GLuint>(glGetUniformLocation(mProgram, "uPMatrix"));
    muMMatrixHandle = static_cast<GLuint>(glGetUniformLocation(mProgram, "uMMatrix"));

    maPositionHandle2 = static_cast<GLuint>(glGetAttribLocation(mProgram2, "aPosition"));
    maTextureHandle2 = static_cast<GLuint>(glGetAttribLocation(mProgram2, "aTextureCoord"));
    muPMatrixHandle2 = static_cast<GLuint>(glGetUniformLocation(mProgram2, "uPMatrix"));
    muMMatrixHandle2 = static_cast<GLuint>(glGetUniformLocation(mProgram2, "uMMatrix"));

}

void initTexture() {

    glGenTextures(1, &colorTexture);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, colorTexture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 640, 480, 0, GL_RGB, GL_UNSIGNED_BYTE, img[0].data);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glGenTextures(1, &depthTexture);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, depthTexture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 640, 480, 0, GL_RGB, GL_UNSIGNED_BYTE, depth[0].data);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

}

void init() {

    glClearColor(0.f, 0.f, 0.f, 1.f);

    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    glHint(GL_FRAGMENT_SHADER_DERIVATIVE_HINT, GL_FASTEST);

    mProgram = createProgram(gVertexShader, gFragmentShader);
    mProgram2 = createProgram(gVertexShader2, gFragmentShader2);

    initHandle();
    initTexture();
}

float rot = 0.f;
glm::vec3 model_pose = glm::vec3(0.0f, 0.0f, -10.f);
glm::vec3 model_scale = glm::vec3(1.0f, 1.0f, 1.0f);

float ver_test_1[] = {
        -1.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f,
};
float ver_test_2[] = {
        0.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
};
float texture_test[] = {
        1.0f, 1.0f,
        0.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
};

void draw_plane_1() {

    mPMatrix = glm::mat4(1.0f);         // ProjectionView
    mMMatrix = glm::mat4(1.0f);         // ModelView

    glUseProgram(mProgram);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, colorTexture);   // Fragment Shader로 텍스쳐를 전달한다(sample2D)
    glUniform1i(glGetUniformLocation(mProgram, "sTexture"), 0);

    // projection, modelview 행렬 shader 연결
    glUniformMatrix4fv(muPMatrixHandle, 1, GL_FALSE, glm::value_ptr(mPMatrix));
    glUniformMatrix4fv(muMMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMMatrix));

    // texture 지정
    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, GL_FALSE, 8, texture_test);
    glEnableVertexAttribArray(maTextureHandle);

    // vertex buffer 연결
    glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, GL_FALSE, 12, ver_test_1);
    glEnableVertexAttribArray(maPositionHandle);

    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, index_plane);
}

void draw_plane_2() {

    mPMatrix = glm::mat4(1.0f);         // ProjectionView
    mMMatrix = glm::mat4(1.0f);         // ModelView

    glUseProgram(mProgram);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, depthTexture);   // Fragment Shader로 텍스쳐를 전달한다(sample2D)
    glUniform1i(glGetUniformLocation(mProgram, "sTexture"), 1);

    // projection, modelview 행렬 shader 연결
    glUniformMatrix4fv(muPMatrixHandle, 1, GL_FALSE, glm::value_ptr(mPMatrix));
    glUniformMatrix4fv(muMMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMMatrix));

    // texture 지정
    glVertexAttribPointer(maTextureHandle, 2, GL_FLOAT, GL_FALSE, 8, texture_test);
    glEnableVertexAttribArray(maTextureHandle);

    // vertex buffer 연결
    glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, GL_FALSE, 12, ver_test_2);
    glEnableVertexAttribArray(maPositionHandle);

    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, index_plane);

}

void draw_model() {

    if(rot == 360.f) rot = 0.f;
    rot += 1.0f;

    mPMatrix = glm::mat4(1.0f);         // ProjectionView
    mMMatrix = glm::mat4(1.0f);         // ModelView

    // ProjectionView의 perspective 선언
    mPMatrix = glm::perspective(45.0f, (float)w /(float)h, 1.0f, 100.0f);

    // ModelView -10만큼 z 방향으로 이동
    mMMatrix = glm::translate(mMMatrix, model_pose);
    mMMatrix = glm::scale(mMMatrix, model_scale);
    // ModelView -45도 회전
    mMMatrix = glm::rotate(mMMatrix, glm::radians(-45.f), glm::vec3(0.0f, 1.0f, 0.f));
    // ModelView a도 만큼 회전
    mMMatrix = glm::rotate(mMMatrix, glm::radians(rot), glm::vec3(1.0f, 0.0f, -1.f));

    glUseProgram(mProgram2);

    // projection, modelview 행렬 shader 연결
    glUniformMatrix4fv(muPMatrixHandle2, 1, GL_FALSE, glm::value_ptr(mPMatrix));
    glUniformMatrix4fv(muMMatrixHandle2, 1, GL_FALSE, glm::value_ptr(mMMatrix));

    // vertex buffer 연결
    glVertexAttribPointer(maPositionHandle2, 3, GL_FLOAT, GL_FALSE, 12, vertices);
    glEnableVertexAttribArray(maPositionHandle2);

    for(int i = 0; i < 6; i++) {
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, indexes + 2 * 3 * i);
    }
}

void draw() {

    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    draw_plane_1();
    draw_plane_2();

    draw_model();

    glFlush();

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_shader_1texture_GL2JNILib_init(
        JNIEnv* env,
        jobject /* this */) {

    init();

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_shader_1texture_GL2JNILib_resize(
        JNIEnv* env,
        jobject instance,
        jint width, jint height) {

    w = width;
    h = height;

    glViewport(0, 0, w, h);

    LOGD("width : %d, height : %d", w, h);

    vertices_plane[3] = (float)w;
    vertices_plane[6] = (float)w;
    vertices_plane[7] = (float)h;
    vertices_plane[10] = (float)h;

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_shader_1texture_GL2JNILib_draw(
        JNIEnv* env,
        jobject /* this */) {

    draw();

}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_shader_1texture_GL2JNILib_setImageAddr(
        JNIEnv* env,
        jobject instance,
        jlong imgAddr) {

    Mat &input_img = *(Mat *)imgAddr;
    img.push_back(input_img);

    LOGD("img - w : %d, h : %d", img[0].cols, img[0].rows);
}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_shader_1texture_GL2JNILib_setDepthAddr(
        JNIEnv* env,
        jobject instance,
        jlong depthAddr) {

    Mat &input_depth = *(Mat *)depthAddr;
    depth.push_back(input_depth);

    LOGD("depth - w : %d, h : %d", depth[0].cols, depth[0].rows);
}

extern "C" JNIEXPORT void JNICALL
Java_edu_android_project_shader_1texture_GL2JNILib_changePose(
        JNIEnv* env,
        jobject instance,
        jint idx) {

    if(idx == 0) {
        model_pose.x += 1.0f;
    }
    else if(idx == 1) {
        model_pose.x -= 1.0f;
    }
    else if(idx == 2) {
        model_pose.z -= 5.0f;
    }
    else if(idx == 3) {
        model_scale += 1.0f;
    }

    LOGD("x : %d, y : %d, z : %d", (int)model_pose.x, (int)model_pose.y, (int)model_pose.z);
}
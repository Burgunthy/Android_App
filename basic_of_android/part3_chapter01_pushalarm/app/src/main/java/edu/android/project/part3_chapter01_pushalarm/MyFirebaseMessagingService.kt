package edu.android.project.part3_chapter01_pushalarm

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("Registered")
class MyFirebaseMessagingService: FirebaseMessagingService() {

    // 현재 토큰을 불러왔는데 이게 자주 바뀔 수 있다.
    // 새 기기 앱 복원 / 앱 삭제 재설치 / 데이터 변경
    // 갱신 때 마다 업데이트 해야한다
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}
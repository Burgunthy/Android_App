# Chapter01 - BMI 계산기

## activity_main
- Layout
    - LinearLayout
        - activity main에서 수직, 수평으로 요소를 입력하고 싶을 때 사용
        - 수직, 수평을 정해주는 “android:orientation” 선언 필수
        - padding, margin과 같은 길이 계수는 dp를 이용해 표현
        - LinearLayout 내 새로운 Layout 선언 가능
        - gravity로 위치 지정 가능
        - tools 사용 시, 옆 preview 에 입력하며 확인할 수 있다
    <LinearLayout
            xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:orientation="vertical"
            android:padding="16dp"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            tools:context=".MainActivity">
            //android:gravity="center"
    
            <LinearLayout ,,,



    - TextView
        - 화면 내 Text 입력
        - textSize는 dp가 아닌 sp로 사용
        - text, color와 같은 constant value는 “res → values → string/colors” 에 저장하여 사용
    <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
    
            android:text="높이"
            android:textColor="#000000"
    
            android:text="@string/height"
            android:textColor="@color/costom_black"
    
            android:textSize="20sp"
            android:textStyle="bold"
    />



    - EditText
        - Text 입력 받을 때 사용
        - “inputType”을 통해 number, 정수, 문자 등 입력 받을 타입 설정 가능
    <EditText
            android:id="@+id/heightEditText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:inputType="number"
    />


    - Button
        - 실행을 위한 버튼 역할 진행


- Activity
    - val과 var ( 성질 )
        - val → 불변 타입
            - 초기에 할당 받은 값을 나중에 변경할 수 없으며, 변경 시 컴파일 에러 발생
            - 따라서 findViewById와 같은 불변 변수로 사용
        - var → 가변 타입
            - 초기화 후 값 변경이 가능
            - 물론 다른 타입의 값 input은 불가능
    - val과 var ( 사용 )
        - 두 변수 모두 type을 선언하여 사용한다
    val heightEditText: EditText = findViewById(R.id.heightEditText)
    val weightEditText = findViewById<EditText>(R.id.weightEditText)
        - : int   /   <int>    : 와 같이 input type 설정 후 사용 가능


    - setOnClickListener
        - setOnClickListener
            - Button 클릭 시의 상황 설정을 위해 사용
            - when ( switch ) 를 통해 button 입력 시 결과를 나눌 수 있음
        - Toast
            - 아래에 작게 나오는 메세지! → 길이 설정 가능
        - Intend
            - 다른 Activity 파일과 변수를 주고 받을 때 사용한다
            - 변수 타입을 생각하며 파일 변환 과정 필요
    /// Main
    
    resultButton.setOnClickListener {
        Log.d("MainActivity", "ResultButton 실행")        // cout이 아닌 log함수를 통해 확인
    
        // 값이 비었을 때 에러 설정
        if(heightEditText.text.isEmpty() || weightEditText.text.isEmpty()) {
            // 토스트 메세지 : 아래에 작게 나와서 알려주는거
            // 시간 설정 필요
            // return 위치 설정
            Toast.makeText(this, "빈 값이 있습니다", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }
    
        // text에서 int를 불러올 때, string -> int의 변환이 필요하다
        val height: Int = heightEditText.text.toString().toInt()
        val weight: Int = weightEditText.text.toString().toInt()
    
        Log.d("MainActivity", "height : $height weight : $weight")
    
        // ResultActivity 파일을 불러온다
        val intent = Intent(this, ResultActivity::class.java)
    
        intent.putExtra("height", height)
        intent.putExtra("weight", weight)
    
        startActivity(intent)
    }


    /// Result
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
    
        val height = intent.getIntExtra("height", 0)     // 불러온다
        val weight = intent.getIntExtra("weight", 0)
    
        Log.d("ResultActivity", "height $height weight $weight")
    
        // double은 1.0식으로 나눠주면 자동 치환
        val bmi = weight / (height / 100.0).pow(2.0)
    
        val resultText = when {
            bmi >= 35.0 -> "고도 비만"
            bmi >= 30.0 -> "중정도 비만"
            bmi >= 25.0 -> "경도 비만"
            bmi >= 23.0 -> "과체중"
            bmi >= 18.5 -> "정상 체중"
            else -> "저체중"
        }
    
        val resultValueTextVeiw = findViewById<TextView>(R.id.bmiResultTextView)
        val resultStringTextView = findViewById<TextView>(R.id.resultTextView)
    
        resultValueTextVeiw.text = bmi.toString()
        resultStringTextView.text = resultText.toString()
    }


- AndroidManifest
    - menifest에 Activity를 추가해주어야 실행된다




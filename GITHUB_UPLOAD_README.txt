Character Core 0.3.1 수정본 적용 방법

기준: 이전 CharacterCore_v0_3_GitHubReady.zip. GitHub 현재 브랜치와는 비교하지 못했습니다.

Android Studio에서 먼저 확인 (케이블 없이 APK 빌드 가능)
1. 이 ZIP을 새 폴더에 압축 해제합니다.
2. Android Studio > Open에서 settings.gradle.kts가 있는 폴더를 엽니다.
3. Gradle JDK는 17로 지정하고, 필요한 Android SDK 35 설치 안내를 완료합니다.
4. Terminal에서 다음 명령을 실행합니다 (Windows PowerShell):
   .\gradlew.bat :app:assembleVelketDebug :app:assembleStarcatDebug
5. APK 위치:
   app/build/outputs/apk/velket/debug/app-velket-debug.apk
   app/build/outputs/apk/starcat/debug/app-starcat-debug.apk
6. APK를 휴대폰으로 옮겨 설치 후 앱의 '라이브 배경화면 설정'을 누릅니다.

GitHub 반영
1. 저장소를 백업하거나 수정용 브랜치를 만듭니다.
2. 이 폴더 안의 파일과 폴더를 저장소 최상위의 같은 경로에 덮어씁니다.
   CharacterCore_v0_3_1_Fixed 폴더 자체를 한 단계 아래에 올리지 마세요.
3. .github/workflows/build-apk.yml도 반드시 새 파일로 교체합니다.
4. gradlew, gradlew.bat, gradle/wrapper/gradle-wrapper.jar와 properties도 포함합니다.
5. local.properties, .idea, .gradle, build 폴더는 업로드하지 않습니다.
6. Actions > Build Android APK > Run workflow를 실행합니다.
7. 성공한 실행의 Artifacts에서 Velket/StarCat ZIP을 받아 압축을 풀면 APK가 있습니다.

확인할 동작
- 앱 실행 → 배경화면 미리보기 → 적용.
- 홈 화면 복귀, 화면 껐다 켜기, 회전 뒤에도 캐릭터 표시.
- 충전기 연결/해제 시 상태 변화.
- 두 앱이 각각 설치되는지 확인.

검증 범위
- XML 파싱, 문자열/이미지 참조, Wrapper JAR 무결성 및 셸 문법 검사 통과.
- 이 작업 환경에서는 SDK 부재 및 다운로드 제한으로 APK 컴파일과 실기기 검증 미실시.
- GitHub 저장소는 접근 오류(404)로 수정하지 못했으며, 업로드된 ZIP을 기반으로 수정했습니다.

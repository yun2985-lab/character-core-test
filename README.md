# Character Core 0.3.1 — 빌드 및 배경화면 안정화

벨켓/스타캣 라이브 배경화면 프로토타입입니다. 설치 후 앱의 배경화면 설정 버튼으로 적용합니다.
일반 AI 대화 앱이나 다른 앱 위에 떠 있는 오버레이는 현재 구현되어 있지 않습니다.
WORKING/MUSIC/SLEEP 상태의 렌더링 코드는 있지만 실제 작업·음악·수면 감지 연결은 아직 없습니다.
자동 동작은 대기/걷기 및 배터리·충전 상태입니다.

## 수정 내용
- Manifest가 참조하던 누락 문자열 wallpaper_name 추가. 기존 ZIP의 실제 리소스 오류입니다.
- 공식 Gradle Wrapper 스크립트와 JAR 추가. 과거 ./gradlew 안내와 ZIP 구성 불일치를 해소했습니다.
- AGP 8.9.2 / Gradle 8.11.1 / Kotlin 2.2.10 / Java 17 / SDK 35로 빌드 환경 고정.
  이전 AGP 9.4.0 / Gradle 9.6.0 / SDK 37 설정의 실제 설치 가능 여부는 단정하지 않습니다.
- AGP 8 계열에 필요한 Kotlin Android 플러그인을 명시하고 사용하지 않는 AndroidX 의존성 제거.
- GitHub Actions에서 SDK 설치, 두 flavor 빌드 및 lint를 실행하도록 통일.
- 배경화면 surface 생성/변경/파괴와 가시성을 함께 관리하여 재생 루프를 재시작.
- surface 교체 시 Canvas 예외 및 전력 로그 실패가 배경화면을 종료시키지 않도록 처리.
- 애니메이션 경과 시간에 단조 증가 시계 사용.
- 작업 진행바를 그린 뒤 bitmap Paint 투명도가 다음 프레임에 남는 현상 수정.
- 배경화면 설정 화면이 없는 기기에서 대체 선택 화면/안내 제공.
- 앱 버전 표시 수정 및 스크롤·시스템 바 여백 처리.

## 검증
기존 소스의 누락 문자열 wallpaper_name을 재현했고 수정본에서는 모든 문자열 및 이미지 참조가 해소되었습니다.
XML, Gradle Wrapper JAR 내부/CRC, gradlew 셸 문법 검사를 통과했습니다.
APK 빌드와 Android lint 및 실기기 실행은 이 환경에서 수행하지 못했습니다.
포함된 CI는 다음 실행 시 이를 검사하도록 구성한 것이며 성공한 실행 결과는 아닙니다.
GitHub 현재 소스/실패 로그에는 접근하지 못했습니다.

자세한 적용 순서는 GITHUB_UPLOAD_README.txt를 확인하세요.

## 도구 호환성 근거
- https://developer.android.com/build/releases/agp-8-9-0-release-notes
- https://kotlinlang.org/docs/gradle-configure-project.html
- Wrapper 원본: gradle/gradle 저장소의 v8.11.1 태그.
  gradle-wrapper.properties의 배포 대상은 8.11.1로 고정했습니다.

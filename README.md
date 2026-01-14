# 어디 — 메뉴 기반 배달 검색 앱

<p align="center">
  <img src="https://github.com/user-attachments/assets/e4ab2d6e-b665-4f67-b2c2-a396a6e8682e" alt="어디 배너" width="900" height="220" />
</p>

`어디`는 사용자가 메뉴(예: 치킨, 피자, 떡볶이)을 입력해 해당 메뉴를 제공하는 배달 음식점을 검색하고, 정렬·필터·상세 정보를 확인할 수 있는 Android 앱입니다. 이 README는 개발자를 위한 설정·빌드·구성 안내서입니다.

<p align="right">
  <img src="https://github.com/user-attachments/assets/8a2b07ca-7306-4f0c-9a6d-900b9d2c59d5" alt="어디 로고" width="160" height="160" />
</p>

## 핵심 요약

- 앱 이름: **어디**
- 플랫폼: Android (Kotlin)
- 주요 기능: 메뉴 검색, 정렬(평점/가격/거리), 즐겨찾기, 상세보기, Google 인증 기반 서버 토큰 취득, 위치 기반 정렬

## 주요 기능 상세

- 메뉴 검색: 사용자가 텍스트로 메뉴를 입력하면 해당 메뉴를 제공하는 음식점 목록을 반환합니다.
- 정렬 옵션: 평점 순, 가격 오름/내림차순, 사용자 위치 기반 거리 정렬
- 상세보기: 메뉴/음식점 상세 정보, 즐겨찾기 토글, 외부 지도 또는 웹 링크 열기
- 인증: Google 인증(웹 클라이언트 ID)을 통해 서버에서 토큰을 발급받아 API 호출에 사용
- 권한: 위치(정렬용), 네트워크 액세스

## 화면 구성 (개략)

- 검색/카테고리 화면: 카테고리 불러오기 및 메뉴 검색 입력
- 목록 화면: 메뉴 목록 표시, 정렬 버튼, 위치 권한 요청 처리
- 상세 화면: 메뉴/음식점 상세 정보, 즐겨찾기 버튼, 외부 링크
- 인증 화면: Google 로그인 흐름

## 기술 스택

- Kotlin, Android SDK
- Retrofit2 (네트워크)
- Coroutine / Flow (비동기)
- Jetpack 컴포넌트(액티비티, ViewModel 등)

## 코드 구조 (주요 파일)

- 네트워크
  - `app/src/main/java/com/data/remote/RetrofitClient.kt` — Retrofit 설정 (BASE_URL 확인/수정 필요)
  - `app/src/main/java/com/data/remote/ApiService.kt` — API 엔드포인트 정의
- 레포지토리
  - `app/src/main/java/com/data/repository/UserRepository.kt` — API 호출 래퍼
- UI
  - `app/src/main/java/com/ui/MainActivity.kt` — 검색/카테고리 화면
  - `app/src/main/java/com/ui/GalleryActivity.kt` — 메뉴 목록 및 정렬 (위치 처리 포함)
  - `app/src/main/java/com/ui/InfoActivity.kt` — 상세 화면, 즐겨찾기 처리
  - `app/src/main/java/com/ui/AuthActivity.kt` — Google 인증 흐름
- 데이터/DTO
  - `app/src/main/java/com/data/remote/dto/` — 요청/응답 DTO 정의

## 설정 및 환경 변수

1. BASE_URL
   - API 서버 주소는 `RetrofitClient.kt`에서 설정됩니다: `app/src/main/java/com/data/remote/RetrofitClient.kt`.
   - 개발 시 에뮬레이터를 사용한다면 로컬 서버는 `10.0.2.2`(Android 에뮬레이터) 또는 호스트 네트워크 IP로 설정하세요.
2. Google Web Client ID
   - Google 인증을 사용하려면 `AuthActivity.kt`에 Web client ID를 설정해야 합니다.
3. 토큰/저장소
   - 발급된 서버 토큰은 앱 내부 저장소(예: SharedPreferences)를 통해 관리됩니다. 관련 클래스는 `app/src/main/java/com/data/remote/dto/` 또는 `repository`에 있습니다.

## 권한

- AndroidManifest에 다음 권한이 필요합니다:
  - `ACCESS_FINE_LOCATION` 또는 `ACCESS_COARSE_LOCATION` (거리 정렬)
  - `INTERNET`

## 빌드 및 실행

1. 프로젝트 루트에서 빌드:

```bash
./gradlew assembleDebug
```

2. 디바이스 또는 에뮬레이터에 설치/실행:

```bash
./gradlew installDebug
```

## 개발시 체크리스트

- API가 로컬에서 동작할 경우, 디바이스에서 접근 가능한 주소로 `BASE_URL`을 변경하세요.
- Google 인증을 테스트하려면 OAuth 동의화면 및 Web client ID가 정상 설정돼 있어야 합니다.
- 위치 기반 정렬을 사용하려면 런타임 권한 처리를 확인하세요 (`GalleryActivity.kt`에 구현되어 있음).

## 테스트

- 유닛/계측 테스트는 `app/src/test` 및 `app/src/androidTest` 디렉터리를 확인하세요.

## 배포 & 릴리스

- 릴리스 빌드를 만들려면 `./gradlew assembleRelease`를 사용하고, 서명 구성(signingConfigs)을 설정하세요.

## 기여 방법

- 이 저장소에서 수정한 내용은 Pull Request로 제출하세요. 이슈 또는 기능 제안은 Issue로 남겨주세요.

## 자주 묻는 질문

- Q: BASE_URL을 어디서 바꾸나요?
  - A: `app/src/main/java/com/data/remote/RetrofitClient.kt`에서 수정하세요.
- Q: Google 인증 Web client ID는 어디에 넣나요?
  - A: `app/src/main/java/com/ui/AuthActivity.kt`에서 설정합니다.

## 참고 파일

- 매니페스트: `app/src/main/AndroidManifest.xml`
- 주요 레포지토리: `app/src/main/java/com/data/repository/UserRepository.kt`

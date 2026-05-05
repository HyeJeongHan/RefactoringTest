# CLAUDE.md

## 빌드 & 실행
- 빌드: `./gradlew assembleDebug`
- 테스트: `./gradlew testDebugUnitTest`
- 린트: `./gradlew lint detekt`

## 규칙
- 자세한 아키텍처/코딩 규칙은 AGENTS.md 참고
- 새 코드 작성 전 기존 코드 검색 필수
- ViewModel은 Repository를 통해서만 데이터 접근
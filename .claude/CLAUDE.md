# CLAUDE.md

## Commands
- 빌드: `./gradlew assembleDebug`
- 테스트: `./gradlew testDebugUnitTest`
- 린트: `./gradlew lint detekt`
- 클린: `./gradlew clean`

## 제약사항
- minSdk: 24
- 시크릿: 코드에 포함 금지, Harness Secret 사용

## 상세 규칙
- 코드 패턴 → `.claude/rules/android.md`
- 코드 규칙 → `AGENTS.md`
- 리뷰 기준 → `.claude/rules/review-criteria.md`

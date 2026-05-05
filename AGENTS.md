# AGENTS.md

> AI 에이전트가 이 프로젝트를 작업할 때 따라야 할 규칙입니다.
> 상세 규칙은 `.claude/rules/` 를 참고하세요.

---

## 핵심 원칙

- 코드 작성 전 **기존 코드를 먼저 검색**한다. 중복 생성 금지.
- 요청한 파일 외에는 **수정하지 않는다**.
- 요구사항이 불명확하면 **먼저 확인**을 요청한다. 추측으로 구현하지 않는다.
- PR 하나에 **하나의 목적**만 담는다.

## 프로젝트 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: Clean Architecture + MVI
- **DI**: Hilt
- **CI/CD**: Harness

## 프로젝트 구조

```
app/src/main/java/com/<package>/
├── data/         # Repository Impl, DataSource, DTO
├── domain/       # UseCase, Repository 인터페이스, 도메인 모델
├── presentation/ # ViewModel, Screen, Composable
│   └── <feature>/
├── di/           # Hilt 모듈
└── util/         # 공통 유틸
```

## 코드 규칙

- 새 코드 전 `util/`, `di/`, `presentation/components/` 먼저 확인
- ViewModel → UseCase → Repository 레이어를 반드시 지킨다
- 문자열은 `strings.xml`, 색상·테마는 `Theme.kt` 사용
- 하드코딩 금지 (문자열, 색상, 사이즈)
- `@Composable` 내부에 비즈니스 로직 작성 금지
- 새 비즈니스 로직에는 반드시 테스트를 작성한다

## Git

- 브랜치: `feature/`, `fix/`, `chore/`
- 커밋: Conventional Commits (`feat:`, `fix:`, `refactor:`, `chore:`, `test:`)

## CI (Harness)

- 파이프라인 순서: Lint → Unit Test → Build → (UI Test) → Deploy
- 시크릿은 코드에 포함하지 않는다. Harness Secret Manager 사용.

## PR 체크리스트

- [ ] 기존 코드 재사용 확인
- [ ] `./gradlew lint` 통과
- [ ] `./gradlew testDebugUnitTest` 통과
- [ ] 하드코딩 없음
- [ ] Preview Composable 작성
- [ ] 시크릿/키 미포함
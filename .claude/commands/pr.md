# PR 생성

아래 순서대로 PR을 생성한다.

## 1. 사전 검증
다음을 순서대로 실행하고, 실패 시 PR 생성을 중단하고 오류를 알려준다.
```
./gradlew lint --quiet
./gradlew testDebugUnitTest --quiet
```

## 2. 변경사항 파악
`git diff main...HEAD` 와 `git log main...HEAD --oneline` 으로 변경사항을 파악한다.

## 3. PR 제목/본문 자동 생성
변경사항을 바탕으로 아래 형식으로 PR을 작성한다.

**제목 형식:** `feat: 로그인 화면 추가` (Conventional Commits 형식)

**본문 형식:**
```
## 변경 사항
- 변경한 내용을 bullet point로 요약

## 테스트
- [x] 단위 테스트 통과
- [x] lint 통과
- [ ] 직접 동작 확인

## 스크린샷
(해당 시 첨부)
```

## 4. PR 생성
$ARGUMENTS 가 있으면 제목으로 사용하고, 없으면 자동 생성한 제목을 사용한다.

```bash
gh pr create \
  --title "<생성한 제목>" \
  --body "<생성한 본문>" \
  --base main
```

생성 후 PR URL을 출력한다.
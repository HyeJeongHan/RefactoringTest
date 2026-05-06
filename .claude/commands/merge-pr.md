# PR 머지

$ARGUMENTS 에 PR 번호를 입력받아 조건을 확인한 뒤 머지한다.

사용 예시:
- `/merge-pr 123`         → squash 머지 (기본)
- `/merge-pr 123 squash`  → squash 머지
- `/merge-pr 123 merge`   → 일반 머지
- `/merge-pr 123 rebase`  → rebase 머지

---

## STEP 1. 머지 가능 여부 확인 (자동)

```bash
gh pr view $ARGUMENTS --json state,mergeable,reviewDecision,statusCheckRollup,title,author,baseRefName
```

아래 조건을 모두 체크한다.

| 조건 | 확인 방법 |
|---|---|
| PR 상태가 OPEN | `state == "OPEN"` |
| 충돌 없음 | `mergeable == "MERGEABLE"` |
| 리뷰 승인 | `reviewDecision == "APPROVED"` |
| CI 통과 | `statusCheckRollup` 전부 SUCCESS |

조건 미충족 시 즉시 중단하고 이유를 출력한다:

```
## 머지 불가

❌ CI 실패 — Unit Test (failed)
❌ 리뷰 미승인 — 아직 승인된 리뷰가 없습니다

→ 위 항목을 해결한 뒤 다시 시도하세요.
```

---

## STEP 2. 머지 전 최종 확인 (사람 확인 필수)

조건을 모두 통과하면 아래를 출력하고 반드시 사람의 확인을 기다린다.

```
## 머지 준비 완료

PR #123: feat: 로그인 화면 추가
작성자: @username
베이스 브랜치: main

✅ 충돌 없음
✅ 리뷰 승인 완료
✅ CI 전부 통과

머지 방식: squash
머지 후 브랜치 삭제: YES

━━━━━━━━━━━━━━━━━━━━━━━━
머지할까요? (yes/no)
━━━━━━━━━━━━━━━━━━━━━━━━
```

**사람이 yes 라고 답하기 전까지 절대 머지하지 않는다.**

---

## STEP 3. 머지 실행 (승인 후 자동)

입력한 머지 방식에 따라 실행한다. 기본값은 squash.

```bash
# squash (기본 — 커밋 하나로 정리)
gh pr merge $ARGUMENTS --squash --delete-branch

# 일반 merge
gh pr merge $ARGUMENTS --merge --delete-branch

# rebase
gh pr merge $ARGUMENTS --rebase --delete-branch
```

> `--delete-branch` 로 머지 후 원격 브랜치를 자동 삭제한다.

---

## STEP 4. 로컬 정리 (자동)

```bash
git checkout main
git pull origin main
git remote prune origin
```

머지된 브랜치가 로컬에 남아 있으면 삭제 여부를 묻는다:

```
로컬 브랜치 feature/login 을 삭제할까요? (yes/no)
```

---

## STEP 5. 완료 출력

```
## 머지 완료 ✅

PR #123 → main 머지됨
커밋: abc1234 feat: 로그인 화면 추가
원격 브랜치: 삭제됨
로컬 브랜치: 삭제됨

현재 브랜치: main (최신)
```
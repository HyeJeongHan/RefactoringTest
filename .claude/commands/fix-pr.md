# PR 코멘트 기반 수정 및 재커밋

$ARGUMENTS 에 PR 번호를 입력받아 리뷰 코멘트를 분석하고 코드를 수정한 뒤 커밋 및 푸시한다.

사용 예시: `/fix-pr 123`

## 1. PR 코멘트 가져오기

```bash
# 전체 리뷰 코멘트
gh pr reviews $ARGUMENTS --json body,state,submittedAt

# 라인별 코멘트
gh pr view $ARGUMENTS --json reviewRequests,comments
gh api repos/{owner}/{repo}/pulls/$ARGUMENTS/comments
```

가져온 코멘트를 아래 기준으로 분류한다.

- **수정 필요** (CHANGES_REQUESTED) → 반드시 처리
- **제안** (COMMENT) → 검토 후 처리 여부 판단
- **승인** (APPROVED) → 처리 불필요

## 2. 수정 계획 출력 (코드 수정 전)

코드를 수정하기 전에 반드시 아래 형식으로 수정 계획을 먼저 출력하고 진행 여부를 확인한다.

```
## 수정 계획

### 처리할 코멘트 (N개)
1. [파일명:라인] 코멘트 내용
   → 수정 방향: ...

2. [파일명:라인] 코멘트 내용
   → 수정 방향: ...

### 건너뛸 코멘트 (N개)
- [파일명] 이유: 이미 반영됨 / 제안사항으로 미적용 등

진행할까요? (yes/no)
```

**반드시 사용자 확인 후 코드 수정을 시작한다.**

## 3. 코드 수정

확인 후 코멘트 순서대로 코드를 수정한다.

- 수정 시 AGENTS.md 규칙을 준수한다
- 요청한 범위 외의 코드는 건드리지 않는다
- 수정 완료된 코멘트는 체크 표시로 추적한다

## 4. 수정 결과 확인

```bash
git diff
```

수정된 내용을 출력하고 커밋 진행 여부를 다시 확인한다.

```
## 수정 완료

### 변경된 파일
- app/src/.../LoginViewModel.kt
- app/src/.../LoginScreen.kt

### 처리된 코멘트
- [x] [LoginViewModel.kt:32] Repository 직접 호출 → UseCase로 변경
- [x] [LoginScreen.kt:15] 하드코딩 문자열 → strings.xml로 이동

커밋 메시지: "fix: PR 리뷰 반영 - 레이어 수정 및 하드코딩 제거"

커밋 및 푸시할까요? (yes/no)
```

## 5. 커밋 및 푸시

사용자가 승인하면 수정된 파일만 스테이징하고 커밋한다.

```bash
git add <수정된 파일들>
git commit -m "fix: PR #$ARGUMENTS 리뷰 반영

- [코멘트 요약 1]
- [코멘트 요약 2]"

git push origin HEAD
```

푸시 완료 후 아래를 출력한다:
- 처리된 코멘트 수
- 커밋 메시지
- PR URL
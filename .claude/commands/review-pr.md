# 타인 PR 코드리뷰

$ARGUMENTS 에 PR 번호를 입력받아 해당 PR을 리뷰하고 GitHub에 코멘트를 등록한다.

사용 예시: `/review-pr 123`

## 1. PR 정보 가져오기

```bash
gh pr view $ARGUMENTS
gh pr diff $ARGUMENTS
```

## 2. 리뷰 기준

`.claude/rules/review-criteria.md` 의 항목을 체크한다. 문제가 있는 경우 파일명과 라인 번호를 명시한다.

## 3. 리뷰 코멘트 GitHub에 등록

**문제가 없을 경우 → APPROVE**
```bash
gh pr review $ARGUMENTS \
  --approve \
  --body "코드 리뷰 완료했습니다. 문제없이 잘 작성되었습니다 ✅"
```

**문제가 있을 경우 → REQUEST_CHANGES**
```bash
gh pr review $ARGUMENTS \
  --request-changes \
  --body "<아래 형식으로 작성한 리뷰 본문>"
```

리뷰 본문 형식:
```
## 코드 리뷰

### ❌ 수정 필요
- [파일명:라인] 문제 설명
  → 개선 제안

### 💡 제안 (선택사항)
- [파일명:라인] 제안 내용

### ✅ 잘된 점
- ...
```

## 4. 완료 후 출력

- PR 제목
- 리뷰 결과 (APPROVED / CHANGES_REQUESTED)
- GitHub PR URL

# 푸시

현재 브랜치를 원격 저장소에 푸시한다.

사용 예시:
- `/push`        → 현재 브랜치 푸시
- `/push -f`     → 강제 푸시 (rebase 후 등)

## 1. 현재 상태 확인

```bash
git status
git log origin/HEAD..HEAD --oneline
```

푸시되지 않은 커밋 목록을 출력한다.

## 2. 푸시 전 확인

```
## 푸시 예정

브랜치: feature/login → origin/feature/login

푸시할 커밋 (2개):
- abc1234 feat: 로그인 화면 추가
- def5678 test: LoginViewModel 테스트 추가

$ARGUMENTS 가 -f 인 경우 강제 푸시 경고를 표시한다:
⚠️ 강제 푸시는 원격 히스토리를 덮어씁니다. 계속할까요?

푸시할까요? (yes/no)
```

## 3. 푸시 실행

승인 후 실행한다.

```bash
# 일반 푸시
git push origin HEAD

# 강제 푸시 ($ARGUMENTS 가 -f 인 경우)
git push origin HEAD --force-with-lease
```

> `--force-with-lease` 를 사용해 다른 사람의 커밋을 덮어쓰는 사고를 방지한다.

푸시 완료 후 원격 브랜치 URL을 출력한다.
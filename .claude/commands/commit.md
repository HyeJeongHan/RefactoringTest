# 커밋

변경사항을 분석해 Conventional Commits 형식으로 커밋한다.
md 파일은 커밋하지 않는다.
.claude 내부 파일은 커밋하지 않는다.

사용 예시:
- `/commit`              → 커밋 메시지 자동 생성
- `/commit 로그인 버그 수정` → 입력한 내용 기반으로 메시지 생성

## 1. 브랜치 확인

```bash
git branch --show-current
```

현재 브랜치가 `main`이면 커밋 전에 새 로컬 브랜치를 만들고 이동한다.

```
⚠️ 현재 브랜치가 main입니다.
새 브랜치 이름을 입력하세요 (예: feature/login, fix/crash):
```

입력받은 이름으로 브랜치를 생성하고 checkout한다.

```bash
git checkout -b <브랜치명>
```

브랜치가 `main`이 아니면 이 단계를 건너뛴다.

## 2. 변경사항 파악

```bash
git status
git diff
```

## 3. 커밋 메시지 자동 생성

$ARGUMENTS 가 있으면 참고해서 생성하고, 없으면 diff 기반으로 자동 생성한다.

**형식:**
```
<type>: <제목>

- 변경 내용 요약 1
- 변경 내용 요약 2
```

**type 선택 기준:**
- `feat` : 새 기능
- `fix` : 버그 수정
- `refactor` : 동작 변경 없는 코드 개선
- `test` : 테스트 추가/수정
- `chore` : 의존성, 설정 변경
- `style` : 포맷, 공백 등 코드 의미 없는 변경

## 4. 커밋 전 확인

커밋 전 아래를 출력하고 확인을 받는다.

```
## 커밋 예정

git add 대상:
- 파일1
- 파일2

커밋 메시지:
feat: 로그인 화면 추가

- LoginScreen, LoginViewModel 추가
- Hilt 의존성 주입 연결

커밋할까요? (yes/no)
```

## 5. 커밋 실행

승인 후 확인 단계에서 나열한 파일만 스테이징하고 커밋한다.

```bash
git add <파일1> <파일2> ...
git commit -m "<생성한 커밋 메시지>"
```

커밋 완료 후 커밋 해시와 메시지를 출력한다.
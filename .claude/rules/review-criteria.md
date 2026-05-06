# 리뷰 기준

`review.md`, `review-pr.md` 공통 체크리스트.

## 아키텍처
- Clean Architecture 레이어 위반 여부 (ViewModel → Repository 직접 호출 등)
- `@Composable` 내부 비즈니스 로직 여부
- UseCase 단일 책임 여부

## 코드 품질
- 기존 코드와 중복되는 함수/컴포넌트 여부
- 하드코딩된 문자열, 색상, 사이즈 여부
- `TODO` / `FIXME` 방치 여부

## Compose
- Screen → Content 분리 여부
- Preview Composable 작성 여부
- Nullable 처리 안전 여부

## 테스트
- 새 비즈니스 로직에 단위 테스트 포함 여부

## 보안
- 시크릿/API 키 하드코딩 여부

# Android 패턴

## ViewModel
@HiltViewModel class XxxViewModel @Inject constructor(...) 패턴 사용
StateFlow<UiState> 노출, onIntent()로 이벤트 수신

## Composable
- Screen → Content 분리 필수
- Preview는 Content 단위로 작성
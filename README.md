# 명언 앱 ⑦ 파일 영속성·빌드 (9~10단계)

명언 앱 ⑥(레이어드 + 검색·페이징)에서 **리포지터리 구현체만 파일 저장소로 갈아 끼운** 버전입니다.
컨트롤러·서비스는 파일을 직접 다루지 않습니다.

## 저장 방식

| 파일 | 내용 |
| --- | --- |
| `db/wiseSaying/{번호}.json` | 명언 하나 = 파일 하나. 등록·수정 시 갱신, 삭제 시 파일 삭제 |
| `db/wiseSaying/lastId.txt` | 마지막으로 만든 번호. 재시작 후에도 이어지고 삭제된 번호는 재사용하지 않음 |
| `db/wiseSaying/data.json` | `빌드` 명령으로 만드는 전체 명언 배열 |

```json
{
  "id": 1,
  "content": "명언 1",
  "author": "작가 1"
}
```

JSON 라이브러리 없이 `JsonUtil` 에서 직접 문자열을 만들고 읽습니다.

## 구조

```
App → WiseSayingController → WiseSayingService → WiseSayingRepository (interface)
                                                   ├─ WiseSayingMemoryRepository
                                                   └─ WiseSayingFileRepository  ← AppContext 에서 이것으로 교체
```

- 파일 입출력은 `WiseSayingFileRepository`(와 그것이 쓰는 `standard.util.JsonUtil`)에만 있습니다.
- `빌드` → `WiseSayingController.actionBuild()` → `WiseSayingService.build()` → `WiseSayingRepository.build()`

## 명령어

`등록`, `목록`, `목록?page=2`, `목록?keywordType=content&keyword=과거`, `삭제?id=N`, `수정?id=N`, `빌드`, `종료`

빌드 시 `data.json 파일의 내용이 갱신되었습니다.` 를 출력합니다.

## 테스트

```
./gradlew test
```

- 테스트는 `AppConfig.setModeToTest()` 로 실제 `db/` 가 아닌 **`db_test/wiseSaying`** 폴더를 쓰고, 매 테스트 전에 비웁니다.
- `WiseSayingControllerTest` : 종료·등록·목록·삭제·수정, 없는 번호
- `WiseSayingSearchPagingTest` : 검색·페이징
- `WiseSayingFileRepositoryTest` : 파일 생성, 재시작 후 목록 유지, 수정/삭제 반영, 삭제 번호 미재사용, `data.json` 빌드

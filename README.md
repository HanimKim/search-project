# 검색 어플리케이션
*****

#### 어플리케이션 빌드 파일(.jar) 다운로드 링크

[search-project-1.0-SNAPSHOT.jar](https://github.com/HanimKim/search-project-bootjar)

**sample**

```
java -jar search-project-1.0-SNAPSHOT.jar
```

## 1. API 명세
*****

## A. 블로그 검색 API

### 기본 정보
질의어로 게시물을 검색합니다. 원하는 검색어와 함께 결과 형식 파라미터를 선택적으로 추가할 수 있습니다. 
응답 바디는 page, documents로 구성된 JSON 객체입니다.

### Request
**parameter**

|  Name   |  Type   | Description | Required  |
|:-------:|:-------:|:------------|:---------:|
|  query  | String  | 검색을 원하는 질의어 |     O     |
|  sort   | String  | 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy       |     X     |
|  page   | Integer | 결과 페이지 번호, 1~50 사이의 값, 기본 값 1        |     X     |
|  size   |  Integer   | 한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10       |     X     |

**필수값이 아닌 값들은 범위 밖의 값을 입력 할 경우, 자동으로 기본 값으로 검색 돤다.**

### Response

**page**

|  Name   |  Type   | Description |
|:-------:|:-------:|:------------|
|  total_count  | Integer  | 검색된 문서 수 |
|  pageable_count   | Integer  | total_count 중 노출 가능 문서 수       |
|  is_end   | Boolean | 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음        |
|  total_page   | Integer | 총 페이지 개수       |
|  current_page   | Integer | 현재 페이지      |
|  size   | Integer | 한 페이지에 보여질 문서 수     |

**documents**

|  Name   |  Type   | Description                                                   |
|:-------:|:-------:|:--------------------------------------------------------------|
|  title  | String  | 블로그 글 제목                                                      |
|  contents   | String  | 블로그 글 요약                                                      |
|  url   | String | 블로그 글 URL                                                     |
|  blogname   | String | 블로그의 이름                                                       |
|  thumbnail   | String | 검색 시스템에서 추출한 대표 미리보기 이미지 URL                          |
|  datetime   | String | 블로그 글 작성시간, ISO 8601 [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].000+[tz] |

### Sample

**Request**

```
curl -v -X GET "http://localhost:8080/v1/search/blog" \
--data-urlencode 'query=노랑' "
```

**Response**

```
{
  "documents": [
    {
      "blogname": "렌즈로 보는 아름다움들",
      "contents": "<b>노랑</b>망태버섯 버섯의 여왕이라고도 하는 화려한 버섯으로 서양에서는 신부의 드레스 같다 하여 드레스버섯이라고도 한다. 망태처럼 얽혀 있고 옛날의 대학생들이 입던 망토와 닮았다고 하여 붙여진 이름이다. <b>노랑</b>망태버섯과 같은 말뚝버섯 종류는 악취가 많이 나는 것이 특징인데, 머리 부분의 점액질 물질에는 다량의...",
      "datetime": "2022-07-27T21:56:05.000+09:00",
      "thumbnail": "https://search2.kakaocdn.net/argon/130x130_85_c/544VlWfMtBc",
      "title": "<b>노랑</b>망태버섯",
      "url": "http://kes8206.tistory.com/16889621"
    },
    ...
  ],
  "page": {
    "total_count": 3114897,
    "pageable_count": 799,
    "is_end": false,
    "total_page": 80,
    "current_page": 1,
    "size": 10
  }
}
```

## B.인기 검색어 목록

### 기본 정보

사용자가 검색한 검색어 목록을 제공한다.
검색어와 검색 횟수를 제공하며, 검색 횟수가 높은 순으로 정렬되어 제공된다.
최대 10개의 검색어까지 노출 된다.

### Request

**X**

### Response

|  Name   |  Type   | Description |
|:-------:|:-------:|:------------|
|  sentence  | String  | 검색어         |
|  count   | Integer | 검색 횟수       |

### Sample

**Request**

```
curl -v -X GET "http://localhost:8080/v1/search/popularity/sentences"
```

**Response**

```
[
  {
    "sentence": "빨강",
    "count": 5
  },
  {
    "sentence": "주황",
    "count": 4
  },
  ...
]
```

## 2. 구조
*****

### 기술 스펙

* JAVA 11
* Spring Boot 사용
* Gradle 기반의 프로젝트
* DB는 인메모리 DB(h2)를 사용하며 DB 컨트롤은 JPA로 구현
  * DB 검색 조건에 대해서는 QueryDsl 사용
  * 접속 정보
    * console url : http://localhost:8080/test_db
    * jdbc url : jdbc:h2:mem:~/test_db
    * user name : sa
* Swagger 사용
  * 접속 정보 : http://localhost:8080/swagger-ui.html
* 멀티 모듈 구성
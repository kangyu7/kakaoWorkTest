# 카카오페이 사전과제 - 결제시스템 API 개발
## 목차
- [개발 환경](#개발-환경)
- [빌드 및 실행하기](#빌드-및-실행하기)
- [개발 제약사항](#개발-제약사항)
- [해결방법](#해결방법)

---

## 개발 환경
- 기본 환경
    - IDE: ecplise sts4
    - OS: Windows 10
    - GIT
- Server
    - Java8
    - Spring Boot 2.3.1
    - JPA
    - H2
    - Gradle
- Test
    - Postman
    - Junit5
    
    ![pay테스트](https://user-images.githubusercontent.com/5583680/87118803-46273600-c2b7-11ea-8b40-41e84653e351.png)


## 빌드 및 실행하기
### 터미널 환경
- Git, Java 는 설치되어 있다고 가정한다.

```
$ git clone https://github.com/kangyu7/kakaoWorkTest.git
$ cd kakaoWorkTest
$ ./gradlew clean build
$ java -jar build/libs/kakaopayTest-0.0.1-SNAPSHOT.jar
```

- 접속 Base URI: `http://localhost:8080

## 테이블 설계
- 테이블을 별도로 분할하지 않고 테이블1개로 처리하기로 결정
- primary 키는 long 형식의 auto increasement 로 사용하기로 하고 관리번호(20)로 각 결제의 연결성을 관리
- 카드 정보, 결제 금액등 처음에는 별도 컬럼으로 관리코자 하였으나 카드사 전송데이터를 통으로 저장하므로 별도 컬럼을 추가하지 않고
  해당 데이터를 이용하기로 결정
  
  CARD_DATA {
    ID LONG,
    INSP_NO VARCHAR2(20),
    CARD_STRING VARCHAR2(450)
  }



## 해결방법

- 요청과 응답은 json 형식으로 하기 위해 @RestController 및 @RequestBody 어노테이션을 사용한다.
- 모든 요청은 DTO 클래스를 사용하고 @Valid 어노테이션으로 유효성 검증을 해준다. 
- 결제/결제취소 등 비지니스 로직은 @Service 클래스에서 처리한다. 

### 1. 결제
- Request

```
http://localhost:8080/pay
```

```
POST /pay HTTP/1.1
```
```json
{
    "cardNo": "",
    "exprYm": "0423",
    "cvc": "563",
    "loanYmd" : "0",
    "payAmt" : 11000
}
```

- Response

```json
{
    "inspNo": "1",
    "cardString": "tttttt"
}
```

- 
  - 요청이 들어올경우 - UUID 클래스 사용 20자리로 잘라서 이용하여 관리번호(유니크 ID)  생성 
  - 생성한 관리번호가 DB 에 있는지 조회하고 있으면 새로 생성한다. 
  - 관리번호의 중복을 방어하기 위해서 먼저 인서트 처리를 하고 시작한다. 
  - 카드데이터 암호화는 AES256 을 사용하여 암호화 하였다. 
  - 마지막으로 결제데이터에 대해 카드사 전송(DB) 저장을 해준다.

### 2. 결제취소
- Request

```
http://localhost:8080/cancel
```

```
put /cancel HTTP/1.1
```
```json
{
    "inspNo": "1",
    "cancelAmt" : 1000
}
```

- Response

```json
{
    "inspNo": "1",
    "cardString": "tttttt"
}
```


- 
  - 먼저 해당 관리번호에 해당 하는 데이터를 전부 셀렉트 하여 결제 금액과 취소금액의 합을 구해준다
  - 그리고 취소 금액과 부가가치세등 금액 체크 로직을 거친다. 
  - 마지막으로 취소 데이터에 대해 카드사 전송(DB) 저장을 해준다.

### 3. 데이터조회
- Request

```
http://localhost:8080/select
```

```
GET /select HTTP/1.1
```

```json
{
    "inspNo": "1"
}
```


- Response

```json

{
    "inspNo": "1",
    "cardNo": "",
    "exprYm": "0423",
    "cvc": "563",
    "type" : "PAYMENMT",
    "payAmt" : 11000,
    "vatAmt"  : 1000  
}

```

- 
   - 데이터 조회는 관리번호로 하며 관리번호 조회시에 1건만 조회한다는 요구사항으로 인해 단건처리를 기본으로 하였다.
   - 카드번호에 대한 마스킹 처리는 빠지지 않고 해준다.

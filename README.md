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
    
    ![pay테스트](https://user-images.githubusercontent.com/5583680/87119535-4d4f4380-c2b9-11ea-812b-3c68cdde8124.png)


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

- 인코딩 및 H2 DB 설정
```
    #Encoding 
    server.servlet.encoding.charset=UTF-8
    server.servlet.encoding.enabled=true
    server.servlet.encoding.force=true
    
    #h2(DB)
    spring.datasource.url=jdbc:h2:~/kakaotest
    spring.datasource.username=user
    spring.datasource.password=pass
```

- 요청과 응답은 json 형식으로 하기 위해 @RestController 및 @RequestBody 어노테이션을 사용
- 모든 요청은 DTO 클래스를 사용하고 @Valid 어노테이션으로 유효성 검증
- 결제/결제취소 등 비지니스 로직은 @Service 클래스에서 처리
- 에러처리는 @RestControllerAdvice 를 이용하여 KakaopayControllerAdvice 클래스를 생성하여 처리
- 카드사에 보내는 패킷에 대한 생성 및 처리는 CardPacket 클래스를 생성하여 공백 및 패킷 스트링을 만들어주는 일을 처리

- Multi Thread 동시성 문제 해결
  - DB로 해결하기로 하고 String 으로 된 키를 가지는 테이블 설정 
  - 결제(카드번호), 취소(관리번호) 가 요청 들어오는 즉시 해당 데이터에 키가 존재하는지 확인하고 없으면 인서트 있으면 에러 발생
  - jpa repository save 시에 발생하는 에러도 캐치하여 커스텀 메시지로 처리
  - junit5 multi thread 설정하여 테스트 진행 KakaopayConcurrentTests 클래스로 테스트
  - junit-platform.properties 파일을 생성해주고 아래 설정 추가
```
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = same_thread
junit.jupiter.execution.parallel.mode.classes.default = concurrent
```
  -- test Class 에도 @Execution(ExecutionMode.CONCURRENT) 설정 추가하여 테스트 진행
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
    "cardNo": "5388321411654321",
    "exprYm": "0423",
    "cvc": "123",
    "loanYmd" : "0",
    "payAmt" : 11000
}
```

- Response

```json
{
    "inspNo": "daa3849515a34362879e",
    "cardString": " 446PAYMENT   daa3849515a34362879e5388321411654321    000423123     200000000000909                    7+jgQ9LOauVZ7CGqye/Tnsu6dam87a+DccPjgYpQhc0=,UDtnCWJmyWFA26/So8nruA==,1CP2miRnidxfQhlnXY5CSA==                                                                                                                                                                                                                                                             "
}
```

- 
  - 요청이 들어올경우 UUID 클래스를 사용하여 랜덤키를 생성하고 20자리로 잘라서 관리번호(유니크 ID) 생성 
  - 생성한 관리번호가 DB 에 있는지 조회하고 있으면 새로 생성 
  - 관리번호의 중복을 방어하기 위해서 먼저 인서트 처리
  - 카드데이터 암호화는 AES256 을 사용하여 암호화
  - 카드데이터 암호화 구분자는 "," 로 하였다.
  - 마지막으로 결제데이터에 대해 카드사 전송(DB 저장) 
 
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
    "inspNo": "daa3849515a34362879e",
    "cancelAmt" : 1000
}
```

- Response

```json
{
    "inspNo": "daa3849515a34362879e",
    "cardString": " 446CANCEL    daa3849515a34362879e5388321411654321    000423123       1000000000009daa3849515a34362879e7+jgQ9LOauVZ7CGqye/Tnsu6dam87a+DccPjgYpQhc0=,UDtnCWJmyWFA26/So8nruA==,1CP2miRnidxfQhlnXY5CSA==                                                                                                                                                                                                                                                             "
}
```


- 
  - 먼저 해당 관리번호에 해당 하는 데이터를 전부 셀렉트 하여 결제 금액과 취소금액의 합을 구해준다
  - 그리고 취소 금액과 부가가치세등 금액 체크 로직을 거친다. 
  - 마지막으로 취소 데이터에 대해 카드사 전송(DB 저장)

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
    "inspNo": "18b8c843f515492ea8db"
}
```


- Response

```json
{
    "exprYm": "0423",
    "cvc": "123",
    "inspNo": "18b8c843f515492ea8db",
    "vatAmt": "909",
    "type": "PAYMENT",
    "cardNo": "538832*******321",
    "payAmt": "20000"
}
```

- 
   - 데이터 조회는 관리번호로 하며 관리번호 조회시에 1건만 조회한다는 요구사항으로 인해 단건처리를 기본으로 하였다.
   - 카드번호에 대한 마스킹 처리는 빠지지 않고 해준다.

# 자바 스프링 강의 3주차
### 목차
* [데이터 영속화](#데이터-영속화)
  + [ORM (Objective Relationship Mapping)]
  + [JPA (Java Persistence API)]
  + [H2 Database]
  + [데이터 영속화하기]
* [CRUD API 만들기](#crud-api-만들기)
  + [오브 패턴]
* [예외처리](#예외처리)
  + [예외 발생시키기]
  + [예외 처리하기]
  + [Exception 유연성 부여]
  + [예외 처리 최적화]
---
* 지난주 만들었던 코드를 이어서 활용  
git clone https://github.com/kidongYun/spring_lecture_week2
## 데이터 영속화
### ORM (Objective Relationship Mapping)
* 애플리케이션 Class와 RDB(Relational DataBase)의 테이블을 매핑하는 것
  * 어플리케이션의 객체를 RDB 테이블에 자동으로 영속화 해주는 것
  * *영속화 - 어떤 일이 중도에 끊기거나 바뀌지 않고 지속됨*
* 네이티브 쿼리를 작성하지 않고도 영속화가 가능
* 벤더에 독립적인 영속화가 가능
  * *vendor database: Oracle, DB2, SQL Server etc.*
### JPA (Java Persistence API)
* 자바 진영에서 ORM 기술 표준으로 사용되는 인터페이스 모음
### H2 Database
* 메모리 데이터베이스로 예제 프로젝트에서 주로 사용됨
* console 설정을 하면 웹 기반으로 DBMS 화면도 볼 수 있음
```
// application.properties
// h2 데이터베이스 설정
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
```
### 데이터 영속화하기
* JPA, H2 의존성 추가
```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'com.h2database:h2'

```
* JPA의 관리 아래에 넣기 위해 Article 도메인을 엔티티로 변경
```java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Article {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String title;
  String content;

  public void update(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
```
* JPA 쿼리를 사용하기 위해 Repository 객체 생성(Interface)
* CrudRepository<T, ID> 에서 T는 엔티티 객체 타입, ID는 T 타입의 ID 타입을 넣는다.
```java
public interface ArticleRepository extends CrudRepository<Article, Long> { }
```
* ArticleService 코드를 ArticleRepository 객체와 연계될 수 있도록 변경
```java
@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public Long save(Article request) {
        return articleRepository.save(request).getId();
    }

    public Article findById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    @Transactional
    public Article update(Article request) {
        Article article = this.findById(request.getId());
        //Article에 update 함수 생성
        article.update(request.getTitle(),request.getContent());

        return article;
    }
    public void delete(Long id) {
        articleRepository.deleteById(id);
    }
}
```
* Update 함수 코드를 보면 영속화의 징검다리인 Repository 클래스를 활용하지 않고 domain 클래스에 바로 접근해 업데이트한다
* JPA는 Persistance Context 라는 논리적 공간에서 엔티티들을 캐싱하고 관리한다
* jpa가 데이터를 바꿔주는 시기 : 하나의 트랜잭션이 끝날때
  * *트랜잭션 : 원자성이 보장된 업무단위*
  * `@Transactional`  
    * 지정된 함수가 트랜젹션임을 명시  
    * 함수가 끝나면 트랜잭션이 끝난거니까 도메인이 수정된 내용을 확인하여 데이터베이스로 넣는다
## CRUD API 만들기
* ArticleController 객체에 PUT, DELETE API 추가
```java
class ArticleController {
    // ...
  @PutMapping("/{id}")
  public Response<ArticleDto.Res> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    Article article = Article.builder()
            .id(id)
            .title(request.getTitle())
            .content(request.getContent())
            .build();

    Article articleResponse = articleService.update(article);

    ArticleDto.Res response = ArticleDto.Res.builder()
            .id(String.valueOf(articleResponse.getId()))
            .title(articleResponse.getTitle())
            .content(articleResponse.getContent())
            .build();

    return Response.<ArticleDto.Res>builder().code(ApiCode.SUCCESS).data(response).build();
  }

  @DeleteMapping("/{id}")
  public Response<Void> delete(@PathVariable Long id) {
    articleService.delete(id);
    return Response.<Void>builder().code(ApiCode.SUCCESS).build();
  }
}
```
### 오브 패턴
* 정적 팩토리 메서드 패턴이라고도 불림
* 특정 객체를 생성하는 코드들이 Controller에 상당히 중복되는 것을 개선할 수 있음
* 객체지향 스타일로 코딩 가능 : 객체를 생성하는 일을 그 객체에 위임
```java
class Article {
    // ...
  public static Article of(ArticleDto.ReqPost from) {
    return Article.builder()
            .title(from.getTitle())
            .content(from.getContent())
            .build();
  }

  public static Article of(ArticleDto.ReqPut from, Long id) {
    return Article.builder()
            .id(id)
            .title(from.getTitle())
            .content(from.getContent())
            .build();
  }
}
```
```java
class ArticleDto {
  // ...
  public static class Res {
    // ...
    public static Res of(Article from) {
      return Res.builder()
              .id(String.valueOf(from.getId()))
              .title(from.getTitle())
              .content(from.getContent())
              .build();
    }
}
}
```
```java
public class Response<T> {
    //...
    public static Response<Void> ok() {
        return Response.<Void>builder().code(ApiCode.SUCCESS).build();
    }
    public static <T> Response<T> ok(T data) {
        return Response.<T>builder().code(ApiCode.SUCCESS).data(data).build();
    }
}
```
```java
class ArticleController {
    //...
  @PostMapping
  public Response<Long> post(@RequestBody ArticleDto.ReqPost request) {
    return Response.ok(articleService.save(Article.of(request)));
  }

  @GetMapping("/{id}")
  public Response<ArticleDto.Res> get(@PathVariable Long id) {
    return Response.ok(ArticleDto.Res.of(articleService.findById(id)));
  }

  @PutMapping("/{id}")
  public Response<ArticleDto.Res> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
  }

  @DeleteMapping("/{id}")
  public Response<Void> delete(@PathVariable Long id) {
    articleService.delete(id);
    return Response.ok();
  }
}
```
---
## 예외처리
### 예외 발생시키기
```java
class ArticleSertive {
    // ...
  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());

    if(Objects.isNull(article)) {
      throw new RuntimeException("article value is not existed.");
    }

    //...
  }
}
```
* `NullPointerExcepotion`이 발생할 수도 있는 영역에 Null이 발생했을때 예외를 일으키는 코드 추가
* API 테스트 시, 예외에 대한 내용이 API에 담기지는 않는 것을 확인
* 따로 일으킨 예외를 잡아서 핸들링하는 부분이 없기 때문에 자바 언어 수준에서 제공하는 예외 핸들링이 적용된것
### 예외 처리하기
* API 에서 예외에 대한 정보를 내려주기 위해 Controller를 수정
```java
public enum ApiCode {
  /* COMMON */
  SUCCESS("CM0000", "정상입니다"),
  DATA_IS_NOT_FOUND("CM0001", "데이터가 존재하지 않습니다")
  ;
  //...
}
```
```java
class ArticleController {
  //...
  @PutMapping("/{id}")
  public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    try {
      return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
    } catch (RuntimeException e) {
      return Response.builder().code(ApiCode.DATA_IS_NOT_FOUND).data(e.getMessage()).build();
    }
  }
}
```
* 문제점
  * 위 예외처리는 어떠한 오류가 발생하든 다 `DATA_IS_NOT_FOUND` 오류만을 반환하게 된다
  * 오류에 대한 정보가 Controller 영역에서 Catch 할때 오류 정보를 주는 방법이 message 밖에 없기 때문에 ApiCode 부분이 고정적이다
### Exception 유연성 부여
* `RuntimeExption`은 범용적으로 사용되기위해 만들어진것이라 커스텀한 요소를 알지못하는 것을 개선하기위해 만듦
  * 즉, 이 시스템에서 사용할 exption 객체를 만들었다
```java
@Getter
public class ApiException extends RuntimeException {
    private final ApiCode code;

    public ApiException(ApiCode code) {
        this.code = code;
    }

    public ApiException(ApiCode code, String msg) {
        super(msg);
        this.code = code;
    }
}
```
```java
class ArticleService {
    //...
  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());

    if (Objects.isNull(article)) {
      throw new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article value is not existed.");
    }

    article.update(request.getTitle(), request.getContent());

    return article;
  }
  //...
}
```
```java
class ArticleController {
  //...
  @PutMapping("/{id}")
  public Response<Object> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    try {
      return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
    } catch (ApiException e) {
      return Response.builder().code(e.getCode()).data(e.getMessage()).build();
    }
  }
  //...
}
```
* ApiException은 Api 관련된 예외처리의 목적으로 만들었기 때문에 모든 컨트롤러 영역에서 사용이 필요하다
* 예외를 핸들링하는 try-catch 코드가 중복된다
### 예외 처리 최적화
* `ControllerAdvice` 활용
  * 중복되는 try-catch 코드를 없애준다
```java
@RestControllerAdvice
public class ContollerExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Response<String> apiException(ApiException e) {
        return Response.<String>builder().code(e.getCode()).data(e.getMessage()).build();
    }
}
```
`@RestControllerAdvice` : controller 다음에도 이 객체의 내용이 호출  
`@ExceptionHandler` : 예외 처리하는 것을 핸들링
```java
class ArticleController {
  //...
  @PutMapping("/{id}")
  public Response<ArticleDto.Res> put(@PathVariable Long id, @RequestBody ArticleDto.ReqPut request) {
    return Response.ok(ArticleDto.Res.of(articleService.update(Article.of(request, id))));
  }
  //...
}
```
* Assert 객체 생성
  * if문을 줄여서 예외처리를 하여 가독성을 높인다
```java
public class Asserts {
    public static void isNull(@Nullable Object obj, ApiCode code, String msg) {
        if(Objects.isNull(obj)) {
            throw new ApiException(code, msg);
        }
    }
}
```
```java
public class ArticleService {
    //...
  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());
    Asserts.isNull(article, ApiCode.DATA_IS_NOT_FOUND, "article value is not existed.");
    //...
  }
}

```
* Optional 객체 사용
  * 비용이 비쌈으로 정말 중요한 비즈니스 로직에 사용하기를 권장
  * 단순한 형태의 null처리를 하는거라면 Assert 사용하기
```java
class ArticleService {
    // ...
  public Article findById(Long id) {
    return articleRepository.findById(id)
            .orElseThrow(() -> new ApiException(ApiCode.DATA_IS_NOT_FOUND, "article value is not existed."));
  }

  @Transactional
  public Article update(Article request) {
    Article article = this.findById(request.getId());
    article.update(request.getTitle(), request.getContent());

    return article;
  }
}
```
---
### 참고
https://github.com/kidongYun/spring_lecture_week3
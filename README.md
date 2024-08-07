# Java WAS

2024 우아한 테크캠프 프로젝트 WAS
![image](https://github.com/user-attachments/assets/595a0b4c-5a7f-4813-b613-3d9122b19e07)

# Cafe
- 사용되는 컴포넌트의 생명주기 관리 및 의존관계 주입을 관리하는 프레임워크 

### CoffeeShop
- 컴포넌트를 자동으로 생성하고 의존관계를 주입해주는 DI 컨테이너
  - 그로인해 비즈니스 로직에 더 집중할 수 있었고, DI & IOC로 인해 확장성 및 테스트코드에 용이한 구조를 얻어갈 수 있었습니다.
  - 빈의 의존관계 순서를 지키며 생성 및 주입하기 위해 `위상정렬` 알고리즘을 사용했습니다.

### ArgumentResolver
- 메서드의 argument를 분석하여 필요한 타입으로 argument를 주입해주는 클래스
- `재귀`를 이용해 내부 객체까지 재귀적으로 매핑하여 필요한 값을 주입

### @Controller & @RequestMapping
- RequestHandler로 감싸줄 어노테이션
- 스프링부트와 같이 @Controller와 @RequestMapping 어노테이션을 이용해서 필요한 메서드를 파라미터로 받아 처리한 뒤, 공통적으로 처리하도록 구현
  - 기존 RequestHandler는 HttpRequest와 HttpResponse만을 받아 비즈니스 로직을 처리하도록 되어있어 테스트코드 작성과 비즈니스 로직 작성이 번거로웠습니다.
  - `@Controller`와 `@RequestHandler` 도입 후, `ArgumentResolver`와 함께 필요한 파라미터만 주입받아 비즈니스 로직을 작성하도록 구현했습니다.
  - `@ResponseBody` 가 추가적으로 붙은 메서드는 `ResponseBodyHandler`로 매핑, 안붙은 경우는 `MVCHandler`로 매핑 하도록 구현했습니다.

### RegistHandler
- 모든 핸들러의 매핑이 끝나면, HandlerMapper에 주입한 뒤 서버를 실행하도록 구현했습니다.

# XSS 방어
- 태그를 입력받을 때 HTML Entity로 변환하여 스크립트가 실행되는것을 방지했습니다.
- Request 객체를 XssRequestWrapper로 감싸는 `프록시 패턴`을 채택했습니다.
```java
public class XssRequestWrapper implements Request {
    private final Request request;
    public XssRequestWrapper(Request request) {
        this.request = request;
    }
    private String parseHtmlEscapeChar(String text){
        return text == null ? null : text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
    @Override
    public String getQueryString(String parameter) {
        return parseHtmlEscapeChar(request.getQueryString(parameter));
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public String getUri() {
        return parseHtmlEscapeChar(request.getUri());
    }

    @Override
    public String getHttpVersion() {
        return request.getHttpVersion();
    }

    @Override
    public List<String> getHeader(String key) {
        return request.getHeader(key);
    }

    @Override
    public byte[] getBody() {
        return request.getBody();
    }

    @Override
    public HttpFile getFile(String key) {
        return request.getFile(key);
    }

    @Override
    public List<Cookie> getCookies() {
        return request.getCookies();
    }

    @Override
    public Session getSession() {
        return request.getSession();
    }

    @Override
    public Session getSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public boolean isNewSession() {
        return request.isNewSession();
    }
}

```

# CSRF 방어
- 페이지를 조회할 때 세션과 html에 csrf토큰을 주입한 뒤, 게시글 등록 시점에 세션과 form에서 함께 넘어온 csrf토큰을 검증하도록 로직을 작성함


# Password Encrypt
- 비밀번호를 `Bcrypt` 알고리즘을 이용해서 encrypt하고, match 하여 회원가입 및 로그인을 진행했습니다.
```java
@Coffee
public class BcryptPasswordEncoder implements PasswordEncoder{
    private final Logger logger = LoggerFactory.getLogger(BcryptPasswordEncoder.class);

    @Override
    public String encode(String str) {
        String salt = generateSalt();
        return "$"+salt+"$"+encodeInner(str,salt);
    }

    private String encodeInner(String str,String salt){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(str.getBytes());
            md.update(salt.getBytes());

            byte[] digest = md.digest();
            return byteArrayToHex(digest);
        }catch(Exception e){
            logger.warn(e.getMessage());
            throw new HttpInternalServerErrorException("some internal error");
        }
    }

    @Override
    public boolean match(String str, String encoded) {
        int start = encoded.indexOf("$");
        int end = encoded.lastIndexOf("$");
        if(start == -1 || end == -1 || start == end) return false;

        String salt = encoded.substring(start+1,end);
        String encodedHash = encoded.substring(end+1);
        String compare = encodeInner(str,salt);
        return compare.equals(encodedHash);
    }

    private String byteArrayToHex(byte[] bytes){
        if(bytes == null || bytes.length == 0) return null;

        StringBuilder sb = new StringBuilder(bytes.length*2);
        String hexNumber;
        for(int x =0;x<bytes.length;x++){
            hexNumber = "0"+Integer.toHexString(bytes[x]&0xff);
            sb.append(hexNumber.substring(hexNumber.length()-2));
        }
        return sb.toString();
    }

    private String generateSalt(){
        return UUID.randomUUID().toString();
    }
}

```
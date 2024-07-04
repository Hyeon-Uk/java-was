package codesquad.http.message.response;

import codesquad.http.message.vo.HttpBody;
import codesquad.http.message.vo.HttpHeader;
import codesquad.utils.Timer;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class HttpResponseMaker {
    private final Timer timer;
    public HttpResponseMaker(Timer timer){
        this.timer = timer;
    }
    public HttpResponseMessage build(HttpStatus status, HttpHeader header, HttpBody body){
        return build("HTTP/1.1",status,header,body);
    }
    public HttpResponseMessage build(String httpVersion, HttpStatus status, HttpHeader header, HttpBody body){
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion,status);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        header.setHeader("Date",dateFormat.format(timer.getCurrentTime().toString()));
        return new HttpResponseMessage(startLine,header,body);
    }
}

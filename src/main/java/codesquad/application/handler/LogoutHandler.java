package codesquad.application.handler;

import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;

public class LogoutHandler implements RequestHandler{
    @Override
    public void postHandle(HttpRequest req, HttpResponse res) {
        Session session = req.getSession(false);
        session.invalidate();
        Cookie sid = new Cookie("SID", "");
        sid.setMaxAge(0);
        res.addCookie(sid);
        res.sendRedirect("/");
    }
}

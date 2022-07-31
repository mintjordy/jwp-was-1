package webserver.http.domain.response;

import webserver.http.domain.Cookie;
import webserver.http.domain.Cookies;
import webserver.http.domain.Headers;
import webserver.http.domain.Protocol;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class Response {
    private final Status status;
    private final Headers headers;
    private final Cookies addedCookies;
    private byte[] body;

    public Response(Status status) {
        this(status, new Headers(new LinkedHashMap<>()), new Cookies(new LinkedHashMap<>()), null);
    }

    public Response(Status status, Headers headers, Cookies addedCookies, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.addedCookies = addedCookies;
        this.body = body;
    }

    public static Response ok() {
        return new Response(new Status(Protocol.http1Point1(), StatusCode.OK));
    }

    public static Response okWithBody(String body) {
        Response response = ok();
        response.addBody(body);
        return response;
    }

    public static Response sendRedirect(String location) {
        Response response = new Response(new Status(Protocol.http1Point1(), StatusCode.FOUND));
        response.headers.add("Location", location);
        return response;
    }

    public static Response from(StatusCode statusCode) {
        return new Response(new Status(Protocol.http1Point1(), statusCode));
    }

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void addBody(String body) {
        this.addBody(body.getBytes());
    }

    public void addBody(byte[] body) {
        this.body = body;
        this.headers.add("Content-Length", String.valueOf(body.length));
    }

    public void addCookie(Cookie cookie) {
        addedCookies.addCookie(cookie);
    }

    public Status getStatus() {
        return status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Cookies getAddedCookies() {
        return addedCookies;
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", headers=" + headers +
                ", addedCookies=" + addedCookies +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}

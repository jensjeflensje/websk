package dev.jensderuiter.websk.skript.type;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {

    public int id;
    public Map<String, String> params;
    public Map<String, String> formParams;
    public Map<String, String> cookies = new HashMap<>();
    public String ip;
    public String method;

    public Request(String queryParams, String ip, String cookies, String method, String body) {
        this.id = Objects.hash(cookies, queryParams, ip);
        this.params = queryToMap(queryParams);
        this.ip = ip;
        this.method = method;
        this.formParams = queryToMap(body);
        for (String cookie : cookies.split(";")) {
            cookie = cookie.trim();
            if (cookie.length() <= 2) continue;
            String[] kv = cookie.split("=");
            this.cookies.put(kv[0], kv[1]);
        }
    }

    private Map<String, String> queryToMap(String query) {
        if (query == null) {
            return new HashMap<>();
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

}

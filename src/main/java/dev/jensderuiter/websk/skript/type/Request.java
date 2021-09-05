package dev.jensderuiter.websk.skript.type;


import java.util.HashMap;
import java.util.Map;

public class Request {

    public int id;
    public Map<String, String> params;
    public Map<String, String> cookies = new HashMap<>();
    public String ip;

    public Request(int id, String queryParams, String ip, String cookies) {
        this.id = id;
        this.params = queryToMap(queryParams);
        this.ip = ip;
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

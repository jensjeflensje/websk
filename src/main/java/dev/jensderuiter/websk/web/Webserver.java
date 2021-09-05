package dev.jensderuiter.websk.web;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import dev.jensderuiter.websk.skript.type.Request;
import org.bukkit.event.Event;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class Webserver extends Thread {

    public HttpServer innerServer;

    public Webserver(int port) throws IOException {
        innerServer = HttpServer.create(new InetSocketAddress(port), 0);
    }

    @Override
    public void run() {
        innerServer.start();
    }

    public void shutdown() {
        innerServer.stop(0);
    }

    public void setStringContext(String path, SkriptParser.ParseResult parser, Event event) {
        try {
            innerServer.removeContext(path);
        } catch (IllegalArgumentException ignored) {
        }

        innerServer.createContext(path, httpExchange -> {
            String cookies = "";
            String queryParams;
            String ip;
            int id;
            if (httpExchange.getRequestHeaders().containsKey("Cookie")) {
                List<String> cookieHeader = httpExchange.getRequestHeaders().get("Cookie");
                if (cookieHeader.size() > 0) {
                    cookies = cookieHeader.get(0);
                }
            }
            queryParams = httpExchange.getRequestURI().getQuery();
            ip = httpExchange.getRemoteAddress().getAddress().getHostAddress();
            id = Objects.hash(cookies, queryParams, ip);
            Request request = new Request(
                    id,
                    queryParams,
                    ip,
                    cookies
            );

            Function skriptFunction = Functions.getFunction(parser.regexes.get(0).group(0));
            if (skriptFunction == null) {
                Skript.error("The webserver wasn't given a valid function!");
                return;
            }

            Object[] returned = skriptFunction.execute(new Object[][]{new Object[]{request}});
            String responseString = (String) returned[0];

            byte[] response;
            if (responseString != null) {
                response = responseString.getBytes(StandardCharsets.UTF_8);
            } else {
                response = "".getBytes(StandardCharsets.UTF_8);
            }
            Headers respHeaders = httpExchange.getResponseHeaders();
            List<String> cookieValues = RequestData.futureCookies.get(id);
            if (cookieValues != null) {
                respHeaders.put("Set-Cookie", cookieValues);
            }
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream out = httpExchange.getResponseBody();
            out.write(response);
            out.close();
        });
    }

}

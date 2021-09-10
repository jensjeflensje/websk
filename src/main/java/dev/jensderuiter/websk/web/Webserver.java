package dev.jensderuiter.websk.web;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import dev.jensderuiter.websk.Main;
import dev.jensderuiter.websk.skript.type.Request;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class Webserver extends Thread {

    private HttpServer innerServer;

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

    public void setStringContext(String path, String function) {
        try {
            innerServer.removeContext(path);
        } catch (IllegalArgumentException ignored) {
        }

        innerServer.createContext(path, httpExchange -> {
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                String cookies = "";
                if (httpExchange.getRequestHeaders().containsKey("Cookie")) {
                    List<String> cookieHeader = httpExchange.getRequestHeaders().get("Cookie");
                    if (cookieHeader.size() > 0) {
                        cookies = cookieHeader.get(0);
                    }
                }
                String queryParams = httpExchange.getRequestURI().getQuery();
                String ip = httpExchange.getRemoteAddress().getAddress().getHostAddress();
                String method = httpExchange.getRequestMethod();
                InputStream body = httpExchange.getRequestBody();
                StringBuilder bodyString = new StringBuilder();
                int i;
                while (true) {
                    try {
                        if (!((i = body.read()) != -1)) break;
                        bodyString.append((char) i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Request request = new Request(
                        queryParams,
                        ip,
                        cookies,
                        method,
                        bodyString.toString()
                );

                Function skriptFunction = Functions.getFunction(function);
                if (skriptFunction == null) {
                    Skript.error("The webserver wasn't given a valid function!");
                    return;
                }

                Object[] returned = skriptFunction.execute(new Object[][]{new Object[]{request}});
                if (returned.length == 0) {
                    Skript.error("The webserver function didn't return a valid response. It should be a string.");
                    return;
                }
                String responseString = (String) returned[0];

                byte[] response = (responseString != null ? responseString : "").getBytes(StandardCharsets.UTF_8);
                Headers respHeaders = httpExchange.getResponseHeaders();
                List<String> cookieValues = RequestData.futureCookies.get(request.id);
                if (cookieValues != null) {
                    respHeaders.put("Set-Cookie", cookieValues);
                }
                try {
                    httpExchange.sendResponseHeaders(200, response.length);
                    OutputStream out = httpExchange.getResponseBody();
                    out.write(response);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        });
    }

}

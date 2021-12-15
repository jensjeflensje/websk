package dev.jensderuiter.websk.web;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.TriggerItem;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import dev.jensderuiter.websk.Main;
import dev.jensderuiter.websk.skript.effect.EffReturn;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.skript.factory.ServerObject;
import dev.jensderuiter.websk.skript.type.Header;
import dev.jensderuiter.websk.skript.type.Request;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public void setStringContext(final ServerObject object) {
        try {
            innerServer.removeContext("/");
        } catch (IllegalArgumentException ignored) {
        }
        
        innerServer.createContext("/files/", httpExchange -> {
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                String responseString = "";
                String fileName = httpExchange.getRequestURI().toString();
                fileName =  fileName.replaceFirst("/files/", "");
                if(!Files.exists(Paths.get("plugins", "Skript", "templates", fileName))){
                    Skript.warning("[WEBSK] File '" + fileName + "' doesn't exist!");
                    return;
                }
                try {
                    responseString = new String(Files.readAllBytes(Paths.get("plugins", "Skript", "templates", fileName)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Number code = 200;
                byte[] response = responseString.getBytes(StandardCharsets.UTF_8);
                try {
                    httpExchange.sendResponseHeaders(code.intValue(), response.length);
                    OutputStream out = httpExchange.getResponseBody();
                    out.write(response);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        innerServer.createContext("/", httpExchange -> {
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
                        if ((i = body.read()) == -1) break;
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


                EffReturn.value = null; // We clear the previous value, if set or not
                EffReturn.headers = null; // We clear the previous value, if set or not
                EffReturn.code = null; // We clear the previous value, if set or not
                TriggerItem.walk(object.getOnRequest().get(0), new ServerEvent(this, request, httpExchange));
                String responseString = EffReturn.value;
                Number code = EffReturn.code;
                if (code == null)
                    code = 200;
                Header[] customHeaders = EffReturn.headers;
                if (responseString == null)
                    Skript.warning("You are not returning anything from a web server request!");

                byte[] response = (responseString != null ? responseString : "").getBytes(StandardCharsets.UTF_8);
                Headers respHeaders = httpExchange.getResponseHeaders();
                respHeaders.clear();
                List<String> cookieValues = RequestData.futureCookies.get(request.id);
                if (cookieValues != null) {
                    respHeaders.put("Set-Cookie", cookieValues);
                }
                if (customHeaders != null) {
                    for (Header header : customHeaders)
                        respHeaders.add(header.getKey(), header.getValue());
                }
                try {
                    httpExchange.sendResponseHeaders(code.intValue(), response.length);
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

package dev.jensderuiter.websk.skript.factory;

import com.sun.net.httpserver.HttpExchange;
import dev.jensderuiter.websk.skript.type.Request;
import dev.jensderuiter.websk.web.Webserver;
import org.bukkit.event.HandlerList;

public class ServerEvent extends BaseEvent {

    private final Webserver webserver;
    private final Request request;
    private final HttpExchange httpExchange;

    private final HandlerList handlerList = new HandlerList();

    public ServerEvent(Webserver webserver, Request request, HttpExchange httpExchange) {
        this.webserver = webserver;
        this.request = request;
        this.httpExchange = httpExchange;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public Webserver getWebserver() {
        return webserver;
    }

    public Request getRequest() {
        return request;
    }
}

package dev.jensderuiter.websk.skript.factory;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.validate.SectionValidator;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.log.SkriptLogger;
import dev.jensderuiter.websk.Main;
import dev.jensderuiter.websk.web.Webserver;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerFactory {

    public static final SectionValidator webValidator = new SectionValidator()
            .addEntry("port", false)
            .addSection("on request", false)
            .addSection("on error", true);

    public ServerObject parse(SectionNode node) {

        node.convertToEntries(0);
        if (!webValidator.validate(node))
            return null;

        final Integer port;
        SkriptLogger.setNode(node.get("port")); // Keep the error on the right node
        try {
            port = Integer.parseInt(node.get("port", "null"));
        } catch (final NumberFormatException ex) {
            Skript.error("Unexpected value for port, should be a valid integer bug got: " + node.get("port", "null"));
            return null;
        }

        final SectionNode onRequest = (SectionNode) node.get("on request");
        final @Nullable SectionNode onError = (SectionNode) node.get("on error");

        // This should never happen btw, but it's to avoid NPE
        if (onRequest == null)
            return null;

        final @Nullable ServerObject obj = new ServerObject(port,
                ScriptLoader.loadItems(onRequest), (onError == null ? null : ScriptLoader.loadItems(onError))
        );

        try {
            try {
                Main.webserver.shutdown();
            } catch (Exception ignored) {}
            Main.webserver = new Webserver(port);
            Main.webserver.start();
            Main.webserver.setStringContext(obj);
        } catch (IOException ignored) {
            Skript.error("Error while starting the web server. Is something already listening on that port?");
        }

        return obj;
    }

    private static final ServerFactory instance = new ServerFactory();

    public static ServerFactory getInstance() {
        return instance;
    }
}

package dev.jensderuiter.websk.skript.factory;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import dev.jensderuiter.websk.skript.effect.EffReturn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerObject {

    public static @Nullable ServerObject CURRENT_SERVER = null;

    private final int port;
    private final List<TriggerItem> onRequest;
    private final List<TriggerItem> onError;

    public ServerObject(int port, List<TriggerItem> onRequest, List<TriggerItem> onError) {
        if (CURRENT_SERVER != null)
            throw new IllegalStateException("You cannot create ServerObject if another instance is already enabled.");
        this.port = port;
        this.onRequest = onRequest;
        this.onError = onError == null ? new ArrayList<>() : onError;
        CURRENT_SERVER = this;
    }

    public static void clear() {
        CURRENT_SERVER = null;
    }

    public int getPort() {
        return port;
    }

    public List<TriggerItem> getOnRequest() {
        return onRequest;
    }

    public List<TriggerItem> getOnError() {
        return onError;
    }
}

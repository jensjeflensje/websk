package dev.jensderuiter.websk.skript.factory;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class BaseEvent extends Event {

    private final HandlerList handlerList = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}

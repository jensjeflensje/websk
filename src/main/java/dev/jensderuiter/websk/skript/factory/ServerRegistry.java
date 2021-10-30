package dev.jensderuiter.websk.skript.factory;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import dev.jensderuiter.websk.Main;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerRegistry extends SelfRegisteringSkriptEvent {

    private static SectionNode instanceRegistered = null;

    static {
        Skript.registerEvent("define website", ServerRegistry.class, ServerEvent.class, "define web(server|site)");
    }

    @Override
    public void register(@NotNull Trigger trigger) { }

    @Override
    public void unregister(@NotNull Trigger trigger) {
        instanceRegistered = null;
        stopServer();
    }

    public void stopServer() {
        Main.webserver.shutdown();
        ServerObject.clear();
    }

    @Override
    public void unregisterAll() {
        unregister(null);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        if (instanceRegistered != null) {
            Skript.error("You are already defining the webserver in " + instanceRegistered.getConfig().getFileName() + " at line " + instanceRegistered.getLine() + "!");
            return false;
        }
        SectionNode sectionNode = (SectionNode) SkriptLogger.getNode();

        String originalName = ScriptLoader.getCurrentEventName();
        Class<? extends Event>[] originalEvents = SkriptAdapter.getInstance().getCurrentEvents();
        Kleenean originalDelay = SkriptAdapter.getInstance().getHasDelayedBefore();
        SkriptAdapter.getInstance().setCurrentEvent("discord command", ServerEvent.class);

        assert sectionNode != null;
        ServerObject obj = ServerFactory.getInstance().parse(sectionNode);

        assert originalName != null;
        SkriptAdapter.getInstance().setCurrentEvent(originalName, originalEvents);
        SkriptAdapter.getInstance().setHasDelayedBefore(originalDelay);
        nukeSectionNode(sectionNode);

        if (obj != null)
            instanceRegistered = sectionNode;
        return obj != null;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "define webserver";
    }

    public void nukeSectionNode(SectionNode sectionNode) {
        List<Node> nodes = new ArrayList<>();
        for (Node node : sectionNode) {
            nodes.add(node);
        }
        for (Node n : nodes) {
            sectionNode.remove(n);
        }
    }
}

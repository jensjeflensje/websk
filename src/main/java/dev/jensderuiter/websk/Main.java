package dev.jensderuiter.websk;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import dev.jensderuiter.websk.skript.PluginUpdater;
import dev.jensderuiter.websk.utils.ReflectionUtils;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;
import dev.jensderuiter.websk.utils.adapter.SkriptV2_3;
import dev.jensderuiter.websk.utils.adapter.SkriptV2_6;
import dev.jensderuiter.websk.web.Webserver;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Main extends JavaPlugin {

    private SkriptAddon addon;
    private static SkriptAdapter skriptAdapter;
    public static Webserver webserver = null;
    public static boolean use26;

    @Override
    public void onEnable() {
        addon = Skript.registerAddon(this);
        try {
            addon.loadClasses("dev.jensderuiter.websk", "skript");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final PluginUpdater updater = PluginUpdater.create(this, "jensjeflensje", "websk");
        final PluginUpdater.UpdateState state = updater.check();
        switch (state) {
            case LOWER:
                getLogger().warning("You are using an outdated WebSK version!");
                getLogger().warning("Latest is " + updater.getLatest() + ", but are are on " + getDescription().getVersion() + "!");
                getLogger().warning("Update it now: https://github.com/jensjeflensje/websk/releases/latest" + updater.getLatest());
                break;
            case EQUAL:
                getLogger().fine("You are on the latest WebSK version! Well done!");
                break;
            case GREATER:
                getLogger().warning("Detected a test version of WebSK. Please report bugs on our GitHub.");
                break;
        }

        // This class is from 2.6-alpha1 and +
        final boolean use26 = ReflectionUtils.classExist("ch.njol.skript.conditions.CondIsPluginEnabled");
        skriptAdapter = use26 ? new SkriptV2_6() : new SkriptV2_3();
        Main.use26 = use26;
    }

    public static SkriptAdapter getSkriptAdapter() {
        return skriptAdapter;
    }

    @Override
    public void onDisable() {
        if (webserver != null) {
            webserver.shutdown();
        }
    }
}

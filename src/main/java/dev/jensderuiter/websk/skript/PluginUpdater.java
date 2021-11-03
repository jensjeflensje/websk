package dev.jensderuiter.websk.skript;


import ch.njol.skript.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Function;

/**
 * Class to handle updates of plugins.
 * It works on either SpigotMC or GitHub repository, to check the latest version available.
 * <br> It however use {@link Version} from Skript plugin to compare them.
 * <br> You need org.json library too, in order to use GitHub's repository system.
 * @author Sky
 */
public class PluginUpdater {

    // ######## Builder Methods ######## //

    /**
     * Create a new {@link PluginUpdater} as {@link UpdateSite#GITHUB} type.
     * @param plugin The instance of your plugin
     * @param repoAuthor The repository author
     * @param repoName The repository name
     * @return New instance of {@link PluginUpdater} to use on your plugin.
     * @throws IllegalArgumentException If plugin, repoAuthor or repoName is null
     */
    public static PluginUpdater create(final JavaPlugin plugin, final String repoAuthor, final String repoName) {
        if (plugin == null || repoAuthor == null || repoName == null)
            throw new IllegalArgumentException("The plugin instance, repo author of name cannot be null.");
        return new PluginUpdater(plugin, UpdateSite.GITHUB, repoAuthor, repoName);
    }


    /**
     * Create a new {@link PluginUpdater} as {@link UpdateSite#SPIGOTMC} type.
     * @param plugin The instance of your plugin
     * @param resourceID The resource ID of your SpigotMC resource.
     * @return New instance of {@link PluginUpdater} to use on your plugin.
     * @throws IllegalArgumentException If plugin or resourceID is null
     */
    public static PluginUpdater create(final JavaPlugin plugin, final Long resourceID) {
        if (plugin == null || resourceID == null)
            throw new IllegalArgumentException("The plugin instance, repo author of name cannot be null.");
        return new PluginUpdater(plugin, UpdateSite.SPIGOTMC, Long.toUnsignedString(resourceID));
    }

    // ######## Instance Itself ######## //

    private final JavaPlugin plugin;
    private final UpdateSite site;
    private final String[] arguments;

    /**
     * This should never be used, except if you wanna use reflection to implement yourself your own {@link UpdateSite}.
     * <br>Use either {@link PluginUpdater#create(JavaPlugin, String, String)} for GitHub instance or {@link PluginUpdater#create(JavaPlugin, Long)} for SpigotMC instance
     */
    private PluginUpdater(JavaPlugin plugin, UpdateSite site, String... arguments) {
        this.plugin = plugin;
        this.site = site;
        this.arguments = arguments;
    }

    /**
     * Get the latest version according to the {@link UpdateSite} desired in the instance creation.
     */
    public @NotNull Version getLatest() {
        return getSite().getVersion(getArguments());
    }

    /**
     * Check the version between the current plugin instance and the latest version configured on the instance creation.
     * @return The {@link UpdateState} which match both versions
     */
    public @NotNull UpdateState check() {
        final Version currentVersion = new Version(getPlugin().getDescription().getVersion());
        final Version latestVersion = getSite().getVersion(getArguments());
        final UpdateState state;
        if (currentVersion.isSmallerThan(latestVersion)) {
            state = UpdateState.LOWER;
        } else if (currentVersion.isLargerThan(latestVersion)) {
            state = UpdateState.GREATER;
        } else {
            state = UpdateState.EQUAL;
        }
        return state;
    }

    /**
     * Get the Java Plugin linked to this plugin updater.
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get the website chosen linked to this plugin update.
     */
    public UpdateSite getSite() {
        return site;
    }

    /**
     * Get the arguments we'll use in the {@link UpdateSite#getVersion(Object...)} for the final version getter.
     */
    public String[] getArguments() {
        return arguments;
    }

    /**
     * Get the content of a web site URL
     * @param input The web URL
     * @return the content of the page
     * @throws IOException If the URL is not valid or if they were an exception while scanning the lines
     */
    private static String getURLContent(String input) throws IOException {
        final URL url = new URL(input);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        final StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line);
        reader.close();
        return builder.toString();
    }

    public enum UpdateState {
        /**
         * Mean the current plugin's version is lower than the latest version
         */
        LOWER,
        /**
         * Mean the current plugin's version is greater than the latest version.
         * <br> Could be caused by wrong versioning or beta / alpha build.
         */
        GREATER,
        /**
         * Mean both current plugin's version and latest version are equal
         */
        EQUAL,
        ;
    }

    public enum UpdateSite {
        GITHUB("https://api.github.com/repos/%s/%s/releases/latest", content -> {
            final JSONObject json = new JSONObject(content);
            return new Version(json.getString("tag_name"));
        }),
        SPIGOTMC("https://api.spigotmc.org/legacy/update.php?resource=%s", Version::new),
        ;

        private final String apiRequestURL;
        private final Function<String, Version> versionGetter;

        UpdateSite(String apiRequestURL, Function<String, Version> versionGetter) {
            this.apiRequestURL = apiRequestURL;
            this.versionGetter = versionGetter;
        }

        public Version getVersion(Object... arguments) {
            final String url = String.format(getApiRequestURL(), arguments);
            final String content;
            try {
                content = getURLContent(url);
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Unable to get content of the URL " + url + ", got " + e.getMessage() + " error.");
                return new Version(0, 0);
            }
            return getVersionGetter().apply(content);
        }

        String getApiRequestURL() {
            return apiRequestURL;
        }

        Function<String, Version> getVersionGetter() {
            return versionGetter;
        }
    }

}

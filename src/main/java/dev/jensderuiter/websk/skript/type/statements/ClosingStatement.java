package dev.jensderuiter.websk.skript.type.statements;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClosingStatement extends WebStatement{
    @Override
    public @NotNull LoadingResult init(ParseResult parseResult, @NotNull Event event) {
        return new LoadingResult(parseResult.getRawContent().startsWith("/"), true);
    }

    @Override
    public @Nullable String convert(@NotNull Event event) {
        return null;
    }
}

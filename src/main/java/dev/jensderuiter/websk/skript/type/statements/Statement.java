package dev.jensderuiter.websk.skript.type.statements;

import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Statement {

    @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser);

    @Nullable String parse(@NotNull Event event, @Nullable String codeBetween);

}

package dev.jensderuiter.websk.skript.type.statements.blocks;

import dev.jensderuiter.websk.skript.type.statements.ParsingResult;
import dev.jensderuiter.websk.skript.type.statements.Statement;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowBlock implements Statement {

    private final static Pattern showBlockPattern = Pattern.compile("display( the)?( block)? (.[^ ]+)( with( the)?( argument(s)?)? (.+))?");

    private ParserFactory parser;
    private String blockName;
    private String[] args;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser, @Nullable String preCodeBetween) {
        this.parser = parser;
        final Matcher matcher = showBlockPattern.matcher(code);
        if (!matcher.matches())
            return ParsingResult.UNKNOWN;
        blockName = matcher.group(3);
        args = matcher.group(8) == null ? new String[0] : matcher.group(8).split("\\s*,\\s*|\\s+(and|or|, )\\s+");
        if (!parser.hasBlock(blockName))
            return new ParsingResult(null, "Block '" + blockName + "' does not exist");
        return ParsingResult.SUCCESS;
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        return parser.getBlock(blockName).parse(event, args);
    }
}

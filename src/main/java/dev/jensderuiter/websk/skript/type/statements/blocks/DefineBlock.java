package dev.jensderuiter.websk.skript.type.statements.blocks;

import dev.jensderuiter.websk.skript.type.statements.ParsingResult;
import dev.jensderuiter.websk.skript.type.statements.Statement;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefineBlock implements Statement {

    private final static Pattern defineBlockPattern = Pattern.compile("(define|create)( the)?( block)? (.+)");

    private ParserFactory parser;
    private String blockName;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser, @Nullable String preCodeBetween) {
        this.parser = parser;
        final Matcher matcher = defineBlockPattern.matcher(code);
        if (!matcher.matches())
            return ParsingResult.UNKNOWN;
        blockName = matcher.group(4);
        if (parser.hasBlock(blockName))
            return new ParsingResult(null, "Block already exists: " + blockName);
        return new ParsingResult(blockName);
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        parser.addBlock(blockName, new Block(blockName, codeBetween));
        return null;
    }
}

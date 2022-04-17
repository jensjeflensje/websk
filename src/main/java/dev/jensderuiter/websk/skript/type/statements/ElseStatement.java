package dev.jensderuiter.websk.skript.type.statements;

import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElseStatement implements Statement {

    private final static Pattern elsePattern = Pattern.compile("else");

    private String codeBetween;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser, @Nullable String preCodeBetween) {
        final Matcher matcher = elsePattern.matcher(code);
        if (!matcher.matches())
            return null;
        final ConditionStatement last = parser.getLastConditionAndClear();
        if (last == null)
            return new ParsingResult(null, "No condition found before else statement");
        last.setLinkedElseStatement(this);
        codeBetween = new ParserFactory().parse(preCodeBetween, event).getSecond();
        return new ParsingResult(getDefaultEndSectionName());
    }

    @Override
    public @Nullable String getDefaultEndSectionName() {
        return "else";
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        return "";
    }

    public String getCodeBetween() {
        return codeBetween;
    }
}

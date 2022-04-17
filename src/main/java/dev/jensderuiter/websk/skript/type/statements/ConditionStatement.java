package dev.jensderuiter.websk.skript.type.statements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.util.NonNullPair;
import dev.jensderuiter.websk.utils.SkriptUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionStatement implements Statement {

    private final static Pattern conditionPattern = Pattern.compile("if (.[^->]+)( -> (.+))?");

    private Condition condition;
    private ParserFactory parser;
    private @Nullable ElseStatement linkedElseStatement;

    @Override
    public @Nullable ParsingResult init(String code, Event event, @NotNull ParserFactory parser, @Nullable String preCodeBetween) {
        final Matcher matcher = conditionPattern.matcher(code);
        if (!matcher.matches())
            return null;
        final String rawCondition = matcher.group(1);
        final String conditionName = matcher.group(3) == null ? "condition" : matcher.group(3);
        this.parser = parser;

        condition = SkriptUtils.parseExpression(rawCondition, Skript.getConditions().iterator(), null, event);
        if (condition == null)
            return new ParsingResult(null, "Can't understand this condition: " + rawCondition);

        return new ParsingResult(conditionName);
    }

    @Override
    public @Nullable String parse(@NotNull Event event, @Nullable String codeBetween) {
        if (codeBetween == null)
            throw new UnsupportedOperationException("Code between is null in a section statement");
        final NonNullPair<List<String>, String> parsed = new ParserFactory().parse(codeBetween, event);
        final ElseStatement elseStatement = parser.getLastElseAndClear();
        return condition.check(event) ? parsed.getSecond() : (
                elseStatement == null ? null : elseStatement.getCodeBetween()
        );
    }

    public void setLinkedElseStatement(@Nullable ElseStatement linkedElseStatement) {
        this.linkedElseStatement = linkedElseStatement;
    }

    public @Nullable ElseStatement getLinkedElseStatement() {
        return linkedElseStatement;
    }
}

package dev.jensderuiter.websk.skript.type.statements.blocks;

import ch.njol.skript.lang.Expression;
import dev.jensderuiter.websk.utils.SkriptUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Block {

    private final String name;
    private final String rawContent;

    public Block(String name, String rawContent) {
        this.name = name;
        this.rawContent = rawContent;
    }

    public String getName() {
        return name;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String parse(Event event, String... arguments) {
        String parsed = rawContent;
        int i = 0;
        for (String argument : arguments) {
            i++;
            if (argument.startsWith("\\")) {
                parsed = parsed.replace("%" + i, argument.substring(1));
            } else {
                final Expression<?> expression = SkriptUtils.parseExpression(argument, null, event);
                final String realValue = expression == null ? argument : expression.getSingle(event).toString();
                parsed = parsed.replace("%" + i, realValue);
            }
        }
        final Matcher remainingMatcher = Pattern.compile("%\\d+").matcher(parsed);
        while (remainingMatcher.find())
            parsed = parsed.replace(remainingMatcher.group(), "<undefined argument>");
        return new ParserFactory().parse(parsed, event).getSecond();
    }

}

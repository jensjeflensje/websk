package dev.jensderuiter.websk.utils.parser;

import ch.njol.util.NonNullPair;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.skript.type.statements.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sky
 */
public class ParserFactory {

    private static final List<Class<? extends Statement>> registeredStatements;

    private final static Pattern codePattern = Pattern.compile("\\{\\{([^}]+)}}", Pattern.DOTALL);
    private static final ParserFactory instance = new ParserFactory();

    static {
        registeredStatements = new ArrayList<>();
        registeredStatements.add(ShowStatement.class);
        registeredStatements.add(ConditionStatement.class);
        registeredStatements.add(LoopStatement.class);
        registeredStatements.add(CommentStatement.class);
        registeredStatements.add(ExecuteStatement.class);
    }

    public static ParserFactory get() {
        return instance;
    }

    public String content;
    public List<String> errors;
    public Matcher codeMatcher;

    public NonNullPair<List<String>, String> parse(String raw, Event event) {
        content = raw;
        errors = new ArrayList<>();

        if (content.isEmpty())
            return new NonNullPair<>(new ArrayList<>(), "");

        codeMatcher = codePattern.matcher(content);

        core: while (codeMatcher.find()) {
            final String data = codeMatcher.group();
            final String code = codeMatcher.group(1);
            for (Class<? extends Statement> cStatement : registeredStatements) {

                final Statement statement = construct(cStatement);
                if (statement == null)
                    throw new UnsupportedOperationException("One of the registered statement (" + cStatement.getName() + ") does not have a valid constructor.");

                final ParsingResult parsingResult = statement.init(code, event, this);
                if (parsingResult == null)
                    continue;
                if (!parsingResult.isSuccess()) {
                    errors.addAll(parsingResult.getErrors());
                    continue core;
                }

                final String codeBetween;
                final Matcher endSectionMatcher;
                if (parsingResult.isSectionStatement()) {
                    final String endSectionName = parsingResult.getEndSectionName();
                    if (endSectionName == null)
                        throw new UnsupportedOperationException("One of the registered statement (" + cStatement.getName() + ") does not have a valid end section name.");
                    final Pattern endSectionPattern = Pattern.compile("\\{\\{/("+endSectionName+"|/)}}", Pattern.DOTALL);
                    endSectionMatcher = endSectionPattern.matcher(content);
                    if (!endSectionMatcher.find()) {
                        errors.add("Could not find end section for section statement: " + endSectionName);
                        continue core;
                    }
                    try {
                        final String temp = content.split(Pattern.quote(data))[1];
                        codeBetween = temp.substring(0, temp.lastIndexOf("{{/" + endSectionMatcher.group(1) + "}}"));
                    } catch (Exception ex) {
                        errors.add("Could not find end section for section statement: " + endSectionName);
                        continue core;
                    }
                } else {
                    codeBetween = null;
                    endSectionMatcher = null;
                }
                final String result = statement.parse(event, codeBetween);
                final String replacement;
                if (parsingResult.isSectionStatement() && endSectionMatcher != null)
                    replacement = data + codeBetween + "{{/" + endSectionMatcher.group(1) + "}}";
                else
                    replacement = data;
                content = content.replace(replacement, result == null ? "" : result);
                codeMatcher = codePattern.matcher(content);
                continue core;
            }
            errors.add("Unknown WebSK Statement: " + code);
        }

        return new NonNullPair<>(errors, content);
    }

    private static @Nullable Statement construct(Class<? extends Statement> c) {
        try {
            return c.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}

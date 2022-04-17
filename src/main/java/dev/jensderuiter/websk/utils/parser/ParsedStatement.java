package dev.jensderuiter.websk.utils.parser;

import dev.jensderuiter.websk.skript.type.statements.ParsingResult;
import dev.jensderuiter.websk.skript.type.statements.Statement;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class ParsedStatement {

    private final Statement statement;
    private final @Nullable String codeBetween;
    private final ParsingResult parsingResult;
    private final Matcher endSectionMatcher;
    private final String data;

    public ParsedStatement(Statement statement, @Nullable String codeBetween, ParsingResult parsingResult, Matcher endSectionMatcher, String data) {
        this.statement = statement;
        this.codeBetween = codeBetween;
        this.parsingResult = parsingResult;
        this.endSectionMatcher = endSectionMatcher;
        this.data = data;
    }

    public Statement getStatement() {
        return statement;
    }

    public @Nullable String getCodeBetween() {
        return codeBetween;
    }

    public ParsingResult getParsingResult() {
        return parsingResult;
    }

    public Matcher getEndSectionMatcher() {
        return endSectionMatcher;
    }

    public String getData() {
        return data;
    }
}

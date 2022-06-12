package dev.jensderuiter.websk.skript.type.statements;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParsingResult {

    public static final ParsingResult SUCCESS = new ParsingResult();
    public static final ParsingResult UNKNOWN = null;

    private final List<String> errors;
    private final boolean success;
    private final boolean sectionStatement;
    private final @Nullable String endSectionName;

    public ParsingResult(@Nullable String endSectionName, String... errors) {
        this.errors = new ArrayList<>(Arrays.asList(errors));
        this.success = errors.length == 0;
        this.endSectionName = endSectionName;
        this.sectionStatement = endSectionName != null;
    }

    public ParsingResult() {
        this(null);
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isSectionStatement() {
        return sectionStatement;
    }

    public @Nullable String getEndSectionName() {
        return endSectionName;
    }
}

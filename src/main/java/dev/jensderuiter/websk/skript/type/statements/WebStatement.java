package dev.jensderuiter.websk.skript.type.statements;

import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class WebStatement {

    public abstract @NotNull LoadingResult init(ParseResult parseResult, @NotNull Event event);

    public abstract @Nullable String convert(@NotNull Event event);

    protected static boolean hasEndPattern(String content, String name) {
        final Matcher matcher = Pattern.compile("\\{\\{\\/([^}/]+)}}").matcher(content);
        while (matcher.find()) {
            final String foundName = matcher.group(1);
            if (foundName.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public ParserFactory getParser() {
        return ParserFactory.get();
    }

    public static final class ExceptionResult extends LoadingResult {
        public ExceptionResult(Exception ex) {
            super(false, "An internal error occurred while parsing a statement: "+ ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static final class UnknownPattern extends LoadingResult {
        public UnknownPattern() {
            super(false);
        }
    }

    public static class LoadingResult {

        private final boolean success;
        private final String[] errors;
        private boolean disableReplace;

        public LoadingResult(boolean success, String... errors) {
            this.success = success;
            this.errors = errors;
            this.disableReplace = false;
        }

        public LoadingResult(boolean success, boolean disableReplace, String... errors) {
            this.success = success;
            this.errors = errors;
            this.disableReplace = disableReplace;
        }

        public boolean disableReplace() {
            return disableReplace;
        }

        public boolean success() {
            return success;
        }

        public String[] getErrors() {
            return errors;
        }

        @Override
        public String toString() {
            return ""+success();
        }

        public boolean hasErrors() {
            return errors.length > 0;
        }
    }

    public static class ParseResult {

        private final List<Tag> tags;
        private final String rawContent;
        private final String content;
        private final String data;
        private ParserFactory parser;

        public ParseResult(String rawContent, String content, String data, Tag... tags) {
            this.data = data;
            this.tags = new ArrayList<>(Arrays.asList(tags));
            this.rawContent = rawContent;
            this.content = content;
        }

        public ParseResult setParserInstance(ParserFactory parser) {
            this.parser = parser;
            return this;
        }

        public ParserFactory getParser() {
            return parser;
        }

        public List<Tag> getTags() {
            return tags;
        }

        public String getData() {
            return data;
        }

        public String getContent() {
            return content;
        }

        public String getRawContent() {
            return rawContent;
        }
    }

    public enum Tag {
        CLOSING(),
        MULTI_LINE(),
        ;
    }

}

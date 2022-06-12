package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.NonNullPair;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import dev.jensderuiter.websk.utils.ReflectionUtils;
import dev.jensderuiter.websk.utils.parser.ParserFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExprLoadFile extends SimpleExpression<String> {

    final Pattern pattern = Pattern.compile("show (.+)");

    static {
        final String pattern = ReflectionUtils.classExist("info.itsthesky.SkImage.SkImage") ? "template [file]" : "[template] file";
        Skript.registerExpression(ExprLoadFile.class, String.class, ExpressionType.COMBINED, "[the] " + pattern + " %string%");
    }

    private Expression<String> fileName;

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parser) {
        fileName = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "file " + fileName.toString(event, debug);
    }

    public String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    protected String @NotNull [] get(@NotNull Event event) {
        final List<String> errors = new ArrayList<>();
        String fileNameObj = fileName.getSingle(event);
        if (fileNameObj == null) {
            Skript.error("Template file does not exist: " + fileNameObj);
            return new String[]{""};
        }
        File file = new File("plugins/WebSK/files/", fileNameObj);
        if (file.exists()) {
            final String fileContents;
            try {
                fileContents = Files.asCharSource(file, Charsets.UTF_8).read();
            } catch (IOException e) {
                errors.add(e.getMessage());
                return errorTemplate(fileNameObj, errors);
            }

            final NonNullPair<List<String>, String> result = ParserFactory.get().parse(fileContents, event);
            errors.addAll(result.getFirst());
            ParserFactory.clearBlocks();

            if (errors.isEmpty()) {
                return new String[]{result.getSecond()};
            } else {
                return errorTemplate(fileNameObj, errors);
            }
        }
        errors.add("The template file " + fileNameObj + " doesn't exist! (Should be under plugins/WebSK/files/"+fileNameObj+")");
        return errorTemplate(fileNameObj, errors);
    }

    private static String[] errorTemplate(String fileName, List<String> errors) {
        final StringBuilder pageBuilder = new StringBuilder();
        pageBuilder.append("<!DOCTYPE html>");
        pageBuilder.append("<html lang=\"en\">");
        pageBuilder.append("<head>");
        pageBuilder.append("	<meta charset=\"UTF-8\">");
        pageBuilder.append("	<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        pageBuilder.append("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        pageBuilder.append("	<title>Exception Occured</title>");
        pageBuilder.append("</head>");
        pageBuilder.append("<body style=\"--tw-bg-opacity: 1; background-color: rgba(243, 244, 246, var(--tw-bg-opacity));\">");
        pageBuilder.append("	<h1 style=\"text-align: center;\">Exception occured while parsing <code>")
                .append(fileName)
                .append("</code>:</h1>");
        pageBuilder.append("	<ul style=\"font-size: 25px;\">");

        for (String e : errors)
            pageBuilder.append("		<li>")
                    .append(e)
                    .append("</li>");

        pageBuilder.append("	</ul>");
        pageBuilder.append("</body>");
        pageBuilder.append("</html>");

        return new String[] {pageBuilder.toString()};
    }
}

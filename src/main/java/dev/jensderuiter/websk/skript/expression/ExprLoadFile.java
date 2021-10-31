package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import com.github.mustachejava.*;
import com.github.mustachejava.codes.IterableCode;
import com.github.mustachejava.codes.NotIterableCode;
import com.github.mustachejava.codes.ValueCode;
import dev.jensderuiter.websk.utils.ReflectionUtils;
import dev.jensderuiter.websk.utils.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExprLoadFile extends SimpleExpression<String> {

    final Pattern pattern = Pattern.compile("show (.+)");

    static {
        final String pattern = ReflectionUtils.classExist("info.itsthesky.SkImage.SkImage") ? "template file" : "file";
        Skript.registerExpression(ExprLoadFile.class, String.class, ExpressionType.COMBINED, "[the] "+pattern+" %string%");
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

    @Override
    protected String @NotNull [] get(@NotNull Event event) {
        final List<String> errors = new ArrayList<>();
        String fileNameObj = fileName.getSingle(event);
        if (fileNameObj == null) {
            Skript.error("Template file does not exist: " + fileNameObj);
            return new String[]{""};
        }
        File file = new File("plugins/Skript/templates/", fileNameObj);
        if (file.exists()) {
            Scanner myReader = null;
            String fileContents = "";
            try {
                myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    fileContents = fileContents + data;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                myReader.close();
            }

            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache template;
            try {
                template = mf.compile(new StringReader(fileContents), fileNameObj);
            } catch (MustacheException ex) {
                errors.add(ex.getMessage());
                return errorTemplate(fileNameObj, errors);
            }

            HashMap<String, Object> variablesUsed = new HashMap<>();

            for (Code code : template.getCodes()) {
                if (code.getName() == null) continue;
                final Matcher exprMatcher = pattern.matcher(code.getName());
                if (exprMatcher.matches()) {

                    final String inputString = exprMatcher.group(1);

                    final Expression<?> expression = SkriptUtils.parseExpression(
                            inputString,
                            Skript.getExpressions(),
                            null, event);
                    if (expression == null) {
                        errors.add("Cannot understand this expression: '" + inputString + "'");
                        continue;
                    }

                    final String content = expression.isSingle() ? expression.getSingle(event).toString() :
                            StringUtils.join(expression.getArray(event), ", ");
                    if (content.isEmpty())
                        errors.add("Expression used '"+expression.toString(null, false)+"' is not set for the current event.");
                    code.append(content);

                } else {
                    boolean shouldBeUsed = false;
                    if (code instanceof ValueCode) {
                        shouldBeUsed = true;
                    } else if (code instanceof NotIterableCode) {
                        shouldBeUsed = true;
                    } else if (code instanceof IterableCode) {
                        shouldBeUsed = true;
                    }

                    /* System.out.println(code.getName());
                    System.out.println(Variables.getVariable(code.getName(), event, true)); */
                    if (shouldBeUsed) {
                        final Object value = Variables.getVariable(code.getName(), event, true);
                        variablesUsed.put(code.getName(), value);
                    }
                }

            }

            StringWriter result = new StringWriter();

            try {
                template.execute(result, variablesUsed).flush();
            } catch (IOException e) {
                errors.add(e.getMessage());
            }

            if (errors.isEmpty()) {
                return new String[]{result.getBuffer().toString()};
            } else {
                return errorTemplate(fileNameObj, errors);
            }
        }
        errors.add("The template file " + fileNameObj + " doesn't exist! (Should be under plugins/Skript/templates/"+fileNameObj+")");
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

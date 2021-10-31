package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
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

    final Pattern pattern = Pattern.compile("show ([\\w- {.}\"]+)");

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
            Mustache template = mf.compile(new StringReader(fileContents), fileNameObj);

            HashMap<String, Object> variablesUsed = new HashMap<>();

            for (Code code : template.getCodes()) {
                if (code.getName() == null) continue;
                final Matcher exprMatcher = pattern.matcher(code.getName());
                if (exprMatcher.matches()) {

                    final String inputString = exprMatcher.group(1);

                    final Expression<?> expression = SkriptUtils.parseExpression(
                            inputString,
                            Skript.getExpressions(),
                            "Cannot understand this expression: '" + inputString + "'"
                    );
                    if (expression == null)
                        break;

                    final String content = expression.isSingle() ? expression.getSingle(event).toString() :
                            StringUtils.join(expression.getArray(event), ", ");
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
                        variablesUsed.put(code.getName(), Variables.getVariable(code.getName(), event, true));
                    }
                }

            }

            StringWriter result = new StringWriter();

            try {
                template.execute(result, variablesUsed).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return new String[]{result.getBuffer().toString()};
        }
        Skript.error("Template file does not exist: " + fileNameObj);
        return new String[]{""};
    }

}

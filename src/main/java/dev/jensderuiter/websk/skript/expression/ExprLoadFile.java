package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.github.mustachejava.Code;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.codes.IterableCode;
import com.github.mustachejava.codes.NotIterableCode;
import com.github.mustachejava.codes.ValueCode;
import org.bukkit.event.Event;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExprLoadFile extends SimpleExpression<String> {

    Pattern pattern = Pattern.compile("%[^%]*%");


    static {
        Skript.registerExpression(ExprLoadFile.class, String.class, ExpressionType.COMBINED, "[the] file %string%");
    }

    private Expression<String> fileName;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
        fileName = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "file " + fileName.toString(event, debug);
    }

    public String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    @Override
    protected String[] get(Event event) {
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

                boolean shouldBeUsed = false;
                if (code instanceof ValueCode) {
                    shouldBeUsed = true;
                } else if (code instanceof NotIterableCode) {
                    shouldBeUsed = true;
                } else if (code instanceof IterableCode) {
                    shouldBeUsed = true;
                }

                System.out.println(code.getName());
                System.out.println(Variables.getVariable(code.getName(), event, true));
                if (shouldBeUsed) {
                    variablesUsed.put(code.getName(), Variables.getVariable(code.getName(), event, true));
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

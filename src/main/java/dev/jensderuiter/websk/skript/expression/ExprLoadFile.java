package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
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

            Matcher matcher = pattern.matcher(fileContents);

            while (matcher.find()) {
                String varNameRaw = matcher.group(0);
                String varName = varNameRaw.substring(1, matcher.group(0).length() - 1).trim();
                if (varName.startsWith("for ")) {
                    String forLoopVar = varName.replaceFirst("for ", "");
                    TreeMap<String, Object> forLoopVarObj = (TreeMap<String, Object>) Variables.getVariable(forLoopVar, event, true);
                    if (forLoopVarObj == null) {
                        forLoopVarObj = new TreeMap<>();
                        forLoopVarObj.put("", "");
                    }
                    String forLoopContent = fileContents.substring(matcher.end());
                    forLoopContent = forLoopContent.split("%endfor%")[0];
                    StringBuilder forLoopContents = new StringBuilder();
                    for (Map.Entry<String, Object> loopElem : forLoopVarObj.entrySet()) {
                        forLoopContents.append(forLoopContent.replaceAll("%obj%", loopElem.getValue().toString()));
                    }
                    fileContents = replaceLast(fileContents.substring(0, matcher.end()), varName, "")
                            + forLoopContents
                            + fileContents.substring(matcher.end() + forLoopContent.length()).replaceFirst("%endfor%", "");
                } else {
                    if (!varName.contains("*")) {
                        Object var = Variables.getVariable(varName, event, true);
                        if (var == null) {
                            var = "";
                        }
                        fileContents = fileContents.replace(
                                varNameRaw,
                                var.toString()
                        );
                    } else {
                        fileContents = fileContents.replace("*", "");
                    }

                }

                matcher = pattern.matcher(fileContents);

            }


            return new String[]{fileContents};
        }
        Skript.error("Template file does not exist: " + fileNameObj);
        return new String[]{""};
    }

}

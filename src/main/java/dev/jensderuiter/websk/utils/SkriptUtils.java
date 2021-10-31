package dev.jensderuiter.websk.utils;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import dev.jensderuiter.websk.Main;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

public final class SkriptUtils {

    /**
     * Original code from SkriptLang team, I just changed how the SkriptParser work with Expression.
     * @author SkriptLang team
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends SyntaxElement> T parseExpression(String expr, Iterator<? extends SyntaxElementInfo<? extends T>> source, @Nullable String defaultError) {
        expr = "" + expr.trim();
        if (expr.isEmpty()) {
            Skript.error(defaultError);
            return null;
        } else {
            ParseLogHandler log = SkriptLogger.startParseLogHandler();

            T var5;
            try {
                final SkriptParser parser = new SkriptParser(expr);
                final T e;
                try {
                    final Method method = parser
                            .getClass()
                            .getDeclaredMethod("parse", Iterator.class);
                    method.setAccessible(true);
                    e = (T) method.invoke(parser, source);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
                if (e == null) {
                    log.printError(defaultError);
                    var5 = null;
                    return var5;
                }

                log.printLog();
                var5 = e;
            } finally {
                log.stop();
            }

            return var5;
        }
    }

}

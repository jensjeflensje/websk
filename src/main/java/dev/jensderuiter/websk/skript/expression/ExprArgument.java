package dev.jensderuiter.websk.skript.expression;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.StringUtils;
import dev.jensderuiter.websk.skript.factory.ServerEvent;
import dev.jensderuiter.websk.utils.adapter.SkriptAdapter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Name("Block Argument")
@Description({"Represent an argument inside a WebSK block definition section.",
"This can return almost anything, single or multiple values.",
"The argument's number is defined by the argument order provided in the 'display' statement, therefore:",
"<code>{{display block test with arguments 50 and \"hello\"}}</code>",
"The first argument will hold a number, here 50, and the second a string, here \"hello\"."})
public class ExprArgument extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprArgument.class, Object.class, ExpressionType.SIMPLE,
                "[][the] <(\\d*1)st|(\\d*2)nd|(\\d*3)rd|(\\d*[4-90])th> arg[ument][s]",
                "[][the] arg[ument][s]",
                "[][the] arg[ument][s](-| )<(\\d+)>"
        );
    }

    @SuppressWarnings("null")
    private int optionIndex;
    public static LinkedList<Object> currentArguments = new LinkedList<>();
    private Object arg;

    @Override
    public boolean init(final Expression<?> @NotNull [] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull SkriptParser.ParseResult parser) {
        if (!SkriptAdapter.getInstance().isCurrentEvents(ServerEvent.class))
            return false;

        if (currentArguments.size() == 0) {
            Skript.error("This block section doesn't have any arguments", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        int index;
        switch (matchedPattern) {
            case 2:
            case 0:
                index = Utils.parseInt(parser.regexes.get(0).group(1));
                if (index > currentArguments.size()) {
                    Skript.error("The block section doesn't have a " + StringUtils.fancyOrderNumber(index) + " argument", ErrorQuality.SEMANTIC_ERROR);
                    return false;
                }
                arg = currentArguments.get(index - 1);
                break;
            case 1:
                if (currentArguments.size() == 1) {
                    index = 0;
                } else {
                    Skript.error("'argument(s)' cannot be used if the block section has multiple arguments. Use 'argument 1', 'argument 2', etc. instead", ErrorQuality.SEMANTIC_ERROR);
                    return false;
                }
                arg = currentArguments.get(index - 1);
                break;
            default:
                assert false : matchedPattern;
                return false;
        }
        this.optionIndex = index - 1;
        return true;
    }

    @Override
    protected Object @NotNull [] get(final @NotNull Event e) {
        return new Object[] {arg};
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return arg == null ? Object.class : arg.getClass();
    }

    @Override
    public @NotNull String toString(final @Nullable Event e, final boolean debug) {
        if (e == null)
            return "the " + StringUtils.fancyOrderNumber(optionIndex + 1) + " argument";
        return Classes.getDebugMessage(getArray(e));
    }

    @Override
    public boolean isSingle() {
        return !(arg instanceof Object[] || arg instanceof List);
    }

    @Override
    public boolean isLoopOf(final @NotNull String s) {
        return false;
    }

}
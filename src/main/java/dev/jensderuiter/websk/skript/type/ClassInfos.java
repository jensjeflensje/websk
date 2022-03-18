package dev.jensderuiter.websk.skript.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import org.jetbrains.annotations.NotNull;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;

public class ClassInfos {

    static {
        Classes.registerClass(new ClassInfo<>(Request.class, "request")
                .user("requests?")
                .name("Request")
                .description("Represents a web request.")
                .parser(new Parser<Request>() {

                    @Override
                    public Request parse(@NotNull String input, @NotNull ParseContext context) {
                        return null;

                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toVariableNameString(Request request) {
                        return String.valueOf(request.id);
                    }

                    public @NotNull String getVariableNamePattern() {
                        return ".+";
                    }

                    @Override
                    public @NotNull String toString(@NotNull Request request, int flags) {
                        return toVariableNameString(request);
                    }
                }));

        Classes.registerClass(new ClassInfo<>(Header.class, "webheader")
                .user("webheaders?")
                .name("Header")
                .description("Represents a request header with a key and a value."));
    }

}

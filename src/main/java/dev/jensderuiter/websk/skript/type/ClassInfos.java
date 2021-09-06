package dev.jensderuiter.websk.skript.type;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;

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
                    public Request parse(String input, ParseContext context) {
                        return null;

                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toVariableNameString(Request request) {
                        return String.valueOf(request.id);
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return ".+";
                    }

                    @Override
                    public String toString(Request request, int flags) {
                        return toVariableNameString(request);
                    }
                }).serializer(new Serializer<Request>() {

                    @Override
                    public Fields serialize(Request request) throws NotSerializableException {
                        Fields fields = new Fields();
                        return fields;
                    }

                    @Override
                    public Request deserialize(Fields fields) throws StreamCorruptedException {
                        return null;
                    }

                    @Override
                    public void deserialize(Request raid, Fields fields) throws StreamCorruptedException, NotSerializableException {
                        assert false;
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }

                }));
    }

}

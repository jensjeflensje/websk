package dev.jensderuiter.websk.skript.type.blocks;

import ch.njol.skript.lang.Statement;
import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class WebStatement implements Iterator<WebStatement> {

    private Iterator<WebStatement> iterator;

    /**
     * The unique {@link Type} of this {@link WebStatement}.
     */
    public abstract @NotNull Type getType();

    /**
     * Get every {@link WebStatement}'s children if {@link WebStatement#getType()} return {@link Type#LOOP} or {@link Type#CONDITION}
     */
    public abstract @NotNull LinkedList<WebStatement> getChildren();

    /**
     * Get the possibly-null parent of this {@link WebStatement}.
     * <br> This can be used when the statement is boxed in blocks, loops or conditions.
     * @return The possibly-null parent of this {@link WebStatement}
     */
    public abstract @Nullable WebStatement getParent();

    /**
     * In case {@link WebStatement#getType()} return {@link Type#SKRIPT}, the Skript's {@link Statement} that represent this {@link WebStatement}.
     */
    public abstract @Nullable Statement getSkriptStatement();

    /**
     * Convert this {@link WebStatement} into an HTML-like string representation.
     * <br> If this return null, the statement will simply be skipped wing nothing.
     * @param event The never-null event to execute used to parse possible VariableString or other expression.
     * @return The HTML-like String representation of this {@link WebStatement}
     */
    public abstract @Nullable String asStringRepresentation(@NotNull Event event);

    /**
     * Either this {@link WebStatement} can be executed or not.
     */
    public boolean canBeExecuted() {
        return getType() != Type.BLOCK;
    }

    /**
     * Execute the given {@link WebStatement} as long as it's a {@link Type#LOOP}, {@link Type#SKRIPT} or {@link Type#CONDITION}.
     * <br> {@link Type#BLOCK} cannot be executed, and will therefore throw an exception.
     * @param event The {@link Event} used to execute the code.
     */
    public void execute(Event event) {
        if (getType() == Type.SKRIPT && getSkriptStatement() != null)
            TriggerItem.walk(getSkriptStatement(), event);
        if (getNext() != null)
            getNext().execute(event);
    }

    protected abstract @Nullable WebStatement getNext();

    @Override
    public boolean hasNext() {
        if (iterator == null)
            iterator = getChildren().iterator();
        return iterator.hasNext();
    }

    @Override
    public WebStatement next() {
        if (iterator == null)
            iterator = getChildren().iterator();
        return iterator.next();
    }

    public enum Type {
        SKRIPT,
        CONDITION,
        LOOP,
        BLOCK
    }

}

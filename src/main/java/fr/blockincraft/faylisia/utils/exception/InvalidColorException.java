package fr.blockincraft.faylisia.utils.exception;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class InvalidColorException extends Exception {
    public final String color;

    /**
     * @param color Invalid color
     */
    public InvalidColorException(@NotNull String color) {
        super(color + " isn't a valid color");
        this.color = color;
    }
}

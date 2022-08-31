package fr.blockincraft.faylisia.utils.colorsexception;

import javax.annotation.Nonnull;

public class InvalidColorException extends Exception {
    public final String color;

    /**
     * @param color Invalid color
     */
    public InvalidColorException(@Nonnull String color) {
        super(color + " isn't a valid color");
        this.color = color;
    }
}

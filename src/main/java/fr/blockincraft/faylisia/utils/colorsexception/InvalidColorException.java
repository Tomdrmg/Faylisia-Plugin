package fr.blockincraft.faylisia.utils.colorsexception;

public class InvalidColorException extends Exception {
    public final String color;

    public InvalidColorException(String color) {
        super(color + " isn't a valid color");
        this.color = color;
    }
}

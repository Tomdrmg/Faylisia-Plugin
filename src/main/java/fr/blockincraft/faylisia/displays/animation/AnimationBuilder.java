package fr.blockincraft.faylisia.displays.animation;

import fr.blockincraft.faylisia.displays.AnimatedText;

import java.util.ArrayList;
import java.util.List;

public abstract class AnimationBuilder {
    protected final List<Element> elements = new ArrayList<>();
    protected String prefix = "";
    protected String suffix = "";

    public AnimationBuilder addElement(char color, String text) {
        return this.addElement(color, text, false, false, false, false, false);
    }

    public AnimationBuilder addElement(char color, String text, boolean bold) {
        return this.addElement(color, text, bold, false, false, false, false);
    }

    public AnimationBuilder addElement(char color, String text, boolean bold, boolean underline) {
        return this.addElement(color, text, bold, underline, false, false, false);
    }

    public AnimationBuilder addElement(char color, String text, boolean bold, boolean underline, boolean strike) {
        return this.addElement(color, text, bold, underline, strike, false, false);
    }

    public AnimationBuilder addElement(char color, String text, boolean bold, boolean underline, boolean strike, boolean magic) {
        return this.addElement(color, text, bold, underline, strike, magic, false);
    }

    public AnimationBuilder addElement(char color, String text, boolean bold, boolean underline, boolean strike, boolean magic, boolean italic) {
        elements.add(new Element(color, text, bold, underline, strike, magic, italic));
        return this;
    }

    public AnimationBuilder setPrefix(String prefix) {
        if (prefix == null) this.prefix = "";
        else this.prefix = prefix;
        return this;
    }

    public AnimationBuilder setSuffix(String suffix) {
        if (suffix == null) this.suffix = "";
        else this.suffix = suffix;
        return this;
    }

    public abstract AnimatedText build();

    protected record Element(char color, String text, boolean bold, boolean underline, boolean strike, boolean magic, boolean italic) {}
}

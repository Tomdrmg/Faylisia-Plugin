package fr.blockincraft.faylisia.displays.animation;

import fr.blockincraft.faylisia.displays.AnimatedText;

import java.util.ArrayList;
import java.util.List;

public class FlashingAnimation extends AnimationBuilder {
    private final char animateColor;
    private final int flashingAmount;
    private final int flashingDelay;
    private final int delay;

    public FlashingAnimation(char animateColor, int flashingAmount, int flashingDelay, int delay) {
        this.animateColor = animateColor;
        this.flashingAmount = flashingAmount;
        this.flashingDelay = flashingDelay;
        this.delay = delay;
    }

    @Override
    public AnimatedText build() {
        List<String> frames = new ArrayList<>();

        for (int i = 0; i < flashingAmount; i++) {
            StringBuilder frame = new StringBuilder();
            StringBuilder secondFrame = new StringBuilder();
            for (Element element : elements) {
                frame.append(prefix);
                secondFrame.append(prefix);

                frame.append("&").append(animateColor).append(element.text());
                secondFrame.append("&").append(element.color());
                if (element.bold()) secondFrame.append("&l");
                if (element.underline()) secondFrame.append("&n");
                if (element.strike()) secondFrame.append("&m");
                if (element.magic()) secondFrame.append("&k");
                if (element.italic()) secondFrame.append("&o");
                secondFrame.append(element.text());

                frame.append(suffix);
                secondFrame.append(suffix);
            }

            frames.add(frame.toString());
            frames.add(secondFrame.toString());
        }

        for (int i = 0; i < delay / flashingDelay; i++) {
            StringBuilder frame = new StringBuilder();
            for (Element element : elements) {
                frame.append(prefix);

                frame.append("&").append(element.color());
                if (element.bold()) frame.append("&l");
                if (element.underline()) frame.append("&n");
                if (element.strike()) frame.append("&m");
                if (element.magic()) frame.append("&k");
                if (element.italic()) frame.append("&o");
                frame.append(element.text());

                frame.append(suffix);
            }

            frames.add(frame.toString());
        }

        return new AnimatedText(flashingDelay, false, frames.toArray(new String[0]));
    }
}

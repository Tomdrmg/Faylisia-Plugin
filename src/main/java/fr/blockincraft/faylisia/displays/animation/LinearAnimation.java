package fr.blockincraft.faylisia.displays.animation;

import fr.blockincraft.faylisia.displays.AnimatedText;

import java.util.ArrayList;
import java.util.List;

public class LinearAnimation extends AnimationBuilder {
    private final char animateColor;
    private final int delay;
    private final StartPosition start;

    public LinearAnimation(char animateColor, int delay, StartPosition start) {
        this.animateColor = animateColor;
        this.delay = delay;
        this.start = start;
    }

    @Override
    public AnimatedText build() {
        List<String> frames = new ArrayList<>();
        int length = 0;

        for (Element element : elements) {
            length += element.text().length();
        }

        if (start == StartPosition.SIDE) {
            for (int i = 0; i < length; i++) {
                StringBuilder frame = new StringBuilder();

                int elementBefore = 0;
                int styleBefore = 0;
                for (Element element : elements) {
                    if (frame.length() - (elementBefore + styleBefore) * 2 <= i) elementBefore++;

                    frame.append("&").append(element.color());
                    if (element.bold()) {
                        if (frame.length() - (elementBefore + styleBefore) * 2 <= i) styleBefore++;
                        frame.append("&l");
                    }
                    if (element.underline()) {
                        if (frame.length() - (elementBefore + styleBefore) * 2 <= i) styleBefore++;
                        frame.append("&n");
                    }
                    if (element.strike()) {
                        if (frame.length() - (elementBefore + styleBefore) * 2 <= i) styleBefore++;
                        frame.append("&m");
                    }
                    if (element.magic()) {
                        if (frame.length() - (elementBefore + styleBefore) * 2 <= i) styleBefore++;
                        frame.append("&k");
                    }
                    if (element.italic()) {
                        if (frame.length() - (elementBefore + styleBefore) * 2 <= i) styleBefore++;
                        frame.append("&o");
                    }
                    frame.append(element.text());
                }

                frame.insert(i + (elementBefore + styleBefore) * 2, "&" + animateColor);
                if (elementBefore > 0) {
                    String style = "";
                    if (elements.get(elementBefore - 1).bold()) style += "&l";
                    if (elements.get(elementBefore - 1).underline()) style += "&n";
                    if (elements.get(elementBefore - 1).strike()) style += "&m";
                    if (elements.get(elementBefore - 1).magic()) style += "&k";
                    if (elements.get(elementBefore - 1).italic()) style += "&o";
                    frame.insert(i + 3 + (elementBefore + styleBefore) * 2, "&" + elements.get(elementBefore - 1).color() + style);
                }

                frame.insert(0, prefix);
                frame.append(suffix);

                frames.add(frame.toString());
            }
        } else if (start == StartPosition.CENTER) {
            int centerSize = length % 2 == 0 ? 2 : 1;
            for (int i = 0; i < length / 2 + (2 - centerSize); i++) {
                StringBuilder frame = new StringBuilder();

                int p1 = length / 2 - i - (centerSize - 1);
                int p2 = length / 2 + i;

                int elementBefore1 = 0;
                int styleBefore1 = 0;
                int elementBefore2 = 0;
                int styleBefore2 = 0;
                for (Element element : elements) {
                    if (frame.length() - (elementBefore1 + styleBefore1) * 2 <= p1) elementBefore1++;
                    if (frame.length() - (elementBefore2 + styleBefore2) * 2 <= p2) elementBefore2++;

                    frame.append("&").append(element.color());
                    if (element.bold()) {
                        if (frame.length() - (elementBefore1 + styleBefore1) * 2 <= p1) styleBefore1++;
                        if (frame.length() - (elementBefore2 + styleBefore2) * 2 <= p2) styleBefore2++;
                        frame.append("&l");
                    }
                    if (element.underline()) {
                        if (frame.length() - (elementBefore1 + styleBefore1) * 2 <= p1) styleBefore1++;
                        if (frame.length() - (elementBefore2 + styleBefore2) * 2 <= p2) styleBefore2++;
                        frame.append("&n");
                    }
                    if (element.strike()) {
                        if (frame.length() - (elementBefore1 + styleBefore1) * 2 <= p1) styleBefore1++;
                        if (frame.length() - (elementBefore2 + styleBefore2) * 2 <= p2) styleBefore2++;
                        frame.append("&m");
                    }
                    if (element.magic()) {
                        if (frame.length() - (elementBefore1 + styleBefore1) * 2 <= p1) styleBefore1++;
                        if (frame.length() - (elementBefore2 + styleBefore2) * 2 <= p2) styleBefore2++;
                        frame.append("&k");
                    }
                    if (element.italic()) {
                        if (frame.length() - (elementBefore1 + styleBefore1) * 2 <= p1) styleBefore1++;
                        if (frame.length() - (elementBefore2 + styleBefore2) * 2 <= p2) styleBefore2++;
                        frame.append("&o");
                    }
                    frame.append(element.text());
                }

                String style1 = "";
                if (elements.get(elementBefore1 - 1).bold()) style1 += "&l";
                if (elements.get(elementBefore1 - 1).underline()) style1 += "&n";
                if (elements.get(elementBefore1 - 1).strike()) style1 += "&m";
                if (elements.get(elementBefore1 - 1).magic()) style1 += "&k";
                if (elements.get(elementBefore1 - 1).italic()) style1 += "&o";

                frame.insert(p1 + (elementBefore1 + styleBefore1) * 2, "&" + animateColor + style1);
                frame.insert(p1 + 3 + style1.length() + (elementBefore1 + styleBefore1) * 2, "&" + elements.get(elementBefore1 - 1).color() + style1);
                if (i != 0 || centerSize == 2) {
                    String style2 = "";
                    if (elements.get(elementBefore2 - 1).bold()) style2 += "&l";
                    if (elements.get(elementBefore2 - 1).underline()) style2 += "&n";
                    if (elements.get(elementBefore2 - 1).strike()) style2 += "&m";
                    if (elements.get(elementBefore2 - 1).magic()) style2 += "&k";
                    if (elements.get(elementBefore2 - 1).italic()) style2 += "&o";

                    frame.insert(p2 + 2 + (style1.length() * 2) + (elementBefore2 + styleBefore2) * 2 + (elementBefore1 > 0 ? 2 : 0), "&" + animateColor + style2);
                    frame.insert(p2 + 2 + (style1.length() * 2) + style2.length() + 3 + (elementBefore2 + styleBefore2) * 2 + (elementBefore1 > 0 ? 2 : 0), "&" + elements.get(elementBefore2 - 1).color() + style2);
                }

                frame.insert(0, prefix);
                frame.append(suffix);

                frames.add(frame.toString());
            }
        } else {
            return null;
        }

        return new AnimatedText(delay, true, frames.toArray(new String[0]));
    }

    public enum StartPosition {
        SIDE,
        CENTER
    }
}

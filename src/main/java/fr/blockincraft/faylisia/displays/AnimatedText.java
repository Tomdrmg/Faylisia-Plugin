package fr.blockincraft.faylisia.displays;

public class AnimatedText {
    private final int animationDuration;
    private final int delayBetweenPart;
    private final boolean reverse;
    private final String[] frames;
    private int animationFrame = 0;
    private int delay = 0;

    public AnimatedText(String unique) {
        this(1, false, unique);
    }

    public AnimatedText(int delayBetweenPart, boolean reverse, String... frames) {
        if (frames.length == 0) throw new RuntimeException("Invalid scoreboard text build! Need at least one frame!");

        this.animationDuration = frames.length;
        this.delayBetweenPart = Math.max(delayBetweenPart, 1);
        this.reverse = reverse;
        this.frames = frames;
    }

    public String get() {
        if (animationDuration == 1) return frames[0];

        int current = animationFrame < 0 ? animationFrame * -1 : animationFrame;

        delay++;
        if (delay >= delayBetweenPart) {
            delay = 0;
            animationFrame++;
            if (animationFrame >= animationDuration) animationFrame = reverse ? (animationDuration - 2) * -1 : 0;
        }

        return frames[current];
    }
}

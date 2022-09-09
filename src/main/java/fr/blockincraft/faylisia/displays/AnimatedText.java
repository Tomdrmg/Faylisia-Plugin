package fr.blockincraft.faylisia.displays;

import org.jetbrains.annotations.NotNull;

public class AnimatedText {
    private final int animationDuration;
    private final int delayBetweenPart;
    private final boolean reverse;
    private final String[] frames;
    private int animationFrame = 0;
    private int delay = 0;

    /**
     * Create a text without any animation and only one frame
     * @param unique text to display
     */
    public AnimatedText(@NotNull String unique) {
        this(1, false, unique);
    }

    /**
     * Create a animated text
     * @param delayBetweenPart delay between two frames
     * @param reverse if at end we do the reverse
     * @param frames all frames of the animation
     */
    public AnimatedText(int delayBetweenPart, boolean reverse, @NotNull String... frames) {
        if (frames.length == 0) throw new RuntimeException("Invalid scoreboard text build! Need at least one frame!");

        this.animationDuration = frames.length;
        this.delayBetweenPart = Math.max(delayBetweenPart, 1);
        this.reverse = reverse;
        this.frames = frames;
    }

    /**
     * Get the current frame and go to the next frame
     * @return current frame
     */
    @NotNull
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

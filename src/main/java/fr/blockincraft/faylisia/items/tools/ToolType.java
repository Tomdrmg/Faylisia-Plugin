package fr.blockincraft.faylisia.items.tools;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum ToolType {
    MINING("MINIER"),
    FORAGING("FORESTIER"),
    FARMING("AGRICOLE"),
    HAND("");

    public final String name;

    ToolType(@NotNull String name) {
        this.name = name;
    }

    public static String nameOfMultiple(@NotNull ToolType[] toolTypes, @NotNull String lastText, boolean uppercase) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < toolTypes.length; i++) {
            sb.append(uppercase ? toolTypes[i].name.toUpperCase(Locale.ROOT) : toolTypes[i].name.toLowerCase(Locale.ROOT));

            if (i + 2 < toolTypes.length) {
                sb.append(", ");
            } else if (i + 1 < toolTypes.length) {
                sb.append(" ").append(lastText).append(" ");
            }
        }

        return sb.toString();
    }
}

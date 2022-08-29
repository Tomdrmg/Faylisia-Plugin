package fr.blockincraft.faylisia.items;

public enum Rarity {
    COMMON(0, "COMMUN", '7', false),
    UNCOMMON(1, "NON COMMUN", 'a', false),
    RARE(2, "RARE", '9', false),
    EPIC(3, "EPIQUE", '5', false),
    LEGENDARY(4, "LEGENDAIRE", '6', false),
    MYSTICAL(5, "MYSTIQUE", 'd', false),
    DEUS(6, "DES DIEUX", 'f', false),
    COSMIC(7, "COSMIQUE", 'b', false),
    NOTHINGNESS(8, "DU NEANT", '8', true);

    private final int index;
    private final String name;
    private final char colorChar;
    private final boolean magicalChars;

    Rarity(int index, String name, char colorChar, boolean magicalChars) {
        this.index = index;
        this.name = name;
        this.colorChar = colorChar;
        this.magicalChars = magicalChars;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public char getColorChar() {
        return colorChar;
    }

    public boolean magicalChars() {
        return magicalChars;
    }
}

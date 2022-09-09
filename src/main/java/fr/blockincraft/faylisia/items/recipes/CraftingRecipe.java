package fr.blockincraft.faylisia.items.recipes;

import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.menu.CraftingMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CraftingRecipe implements Recipe {
    private final CustomItemStack[] pattern;
    private final int resultAmount;
    private final PatternType patternType;
    private final Direction direction;

    /**
     * Constructor for a 3x3 recipe like ore blocks
     * @param resultAmount amount of item crafted
     * @param p1 first ingredient
     * @param p2 second ingredient
     * @param p3 third ingredient
     * @param p4 fourth ingredient
     * @param p5 fifth ingredient
     * @param p6 sixth ingredient
     * @param p7 seventh ingredient
     * @param p8 eighth ingredient
     * @param p9 ninth ingredient
     */
    public CraftingRecipe(int resultAmount, @Nullable CustomItemStack p1, @Nullable CustomItemStack p2, @Nullable CustomItemStack p3,
                                            @Nullable CustomItemStack p4, @Nullable CustomItemStack p5, @Nullable CustomItemStack p6,
                                            @Nullable CustomItemStack p7, @Nullable CustomItemStack p8, @Nullable CustomItemStack p9) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3, p4, p5, p6, p7, p8, p9};
        this.patternType = PatternType.FULL;
        this.direction = null;
    }

    /**
     * Constructor for 3x2 pattern like doors
     * @param resultAmount amount of item crafted
     * @param p1 first ingredient
     * @param p2 second ingredient
     * @param p3 third ingredient
     * @param p4 fourth ingredient
     * @param p5 fifth ingredient
     * @param p6 sixth ingredient
     * @param direction direction of recipe
     */
    public CraftingRecipe(int resultAmount, @Nullable CustomItemStack p1, @Nullable CustomItemStack p2, @Nullable CustomItemStack p3,
                                            @Nullable CustomItemStack p4, @Nullable CustomItemStack p5, @Nullable CustomItemStack p6, @NotNull Direction direction) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3, p4, p5, p6};
        this.patternType = PatternType.TWO_THREE;
        this.direction = direction;
    }

    /**
     * Constructor for a 2x2 pattern
     * @param resultAmount amount of item crafted
     * @param p1 first ingredient
     * @param p2 second ingredient
     * @param p3 third ingredient
     * @param p4 fourth ingredient
     */
    public CraftingRecipe(int resultAmount, @Nullable CustomItemStack p1, @Nullable CustomItemStack p2,
                                            @Nullable CustomItemStack p3, @Nullable CustomItemStack p4) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3, p4};
        this.patternType = PatternType.TWO_TWO;
        this.direction = null;
    }

    /**
     * Constructor for 3x1 pattern like swords
     * @param resultAmount amount of item crafted
     * @param p1 first ingredient
     * @param p2 second ingredient
     * @param p3 third ingredient
     * @param direction direction of recipe
     */
    public CraftingRecipe(int resultAmount, @Nullable CustomItemStack p1, @Nullable CustomItemStack p2, @Nullable CustomItemStack p3, @NotNull Direction direction) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3};
        this.patternType = PatternType.THREE_ONE;
        this.direction = direction;
    }

    /**
     * Constructor for 2x1 pattern like stick
     * @param resultAmount amount of item crafted
     * @param p1 first ingredient
     * @param p2 second ingredient
     * @param direction direction of recipe
     */
    public CraftingRecipe(int resultAmount, @Nullable CustomItemStack p1, @Nullable CustomItemStack p2, @NotNull Direction direction) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2};
        this.patternType = PatternType.TWO_ONE;
        this.direction = direction;
    }

    /**
     * Constructor for only 1x1 pattern like planks
     * @param resultAmount amount of item crafted
     * @param p1 ingredient
     */
    public CraftingRecipe(int resultAmount, @Nullable CustomItemStack p1) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1};
        this.patternType = PatternType.ONE;
        this.direction = null;
    }

    @Override
    @NotNull
    public String getMenuId() {
        return "crafting_table";
    }

    @Override
    @NotNull
    public CustomItemStack[] getItems() {
        return pattern;
    }

    @Override
    public int getResultAmount() {
        return resultAmount;
    }

    @Override
    @Nullable
    public CustomItemStack[] matches(ItemStack[] recipe) {
        if (emptyPattern()) return null;
        if (emptyRecipe(recipe)) return null;
        if (recipe.length != 9) return null;

        if (patternType == PatternType.FULL) {
            for (int i = 0; i < 9; i++) {
                CustomItemStack patternItem = pattern[i];
                if (patternItem == null && recipe[i] == null) continue;
                if (patternItem == null) return null;

                CustomItemStack recipeItem = CustomItemStack.fromItemStack(recipe[i]);

                if (recipeItem == null) return null;

                if (!patternItem.isSimilar(recipeItem) || recipeItem.getAmount() < patternItem.getAmount()) return null;
            }

            return pattern;
        } else if (patternType == PatternType.TWO_THREE) {
            if (getEmptyAmount(recipe) < 3) return null;
            CustomItemStack[][] patterns = new CustomItemStack[0][];

            if (direction == Direction.HORIZONTAL) {
                patterns = new CustomItemStack[][]{
                        new CustomItemStack[]{
                                pattern[0], pattern[1], pattern[2],
                                pattern[3], pattern[4], pattern[5],
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                pattern[0], pattern[1], pattern[2],
                                pattern[3], pattern[4], pattern[5]
                        }
                };
            } else if (direction == Direction.VERTICAL) {
                patterns = new CustomItemStack[][]{
                        new CustomItemStack[]{
                                pattern[0], pattern[1], null,
                                pattern[2], pattern[3], null,
                                pattern[4], pattern[5], null
                        },
                        new CustomItemStack[]{
                                null,       pattern[0], pattern[1],
                                null,       pattern[2], pattern[3],
                                null,       pattern[4], pattern[5]
                        }
                };
            }

            for (CustomItemStack[] pattern : patterns) {
                if (matches(recipe, pattern)) return pattern;
            }
        } else if (patternType == PatternType.TWO_TWO) {
            if (getEmptyAmount(recipe) < 5) return null;

            CustomItemStack[][] patterns = new CustomItemStack[][]{
                    new CustomItemStack[]{
                            pattern[0], pattern[1], null,
                            pattern[2], pattern[3], null,
                            null,       null,       null
                    },
                    new CustomItemStack[]{
                            null,       pattern[0], pattern[1],
                            null,       pattern[2], pattern[3],
                            null,       null,       null
                    },
                    new CustomItemStack[]{
                            null,       null,       null,
                            pattern[0], pattern[1], null,
                            pattern[2], pattern[3], null
                    },
                    new CustomItemStack[]{
                            null,       null,       null,
                            null,       pattern[0], pattern[1],
                            null,       pattern[2], pattern[3]
                    }
            };

            for (CustomItemStack[] pattern : patterns) {
                if (matches(recipe, pattern)) return pattern;
            }
        } else if (patternType == PatternType.THREE_ONE) {
            if (getEmptyAmount(recipe) < 6) return null;
            CustomItemStack[][] patterns = new CustomItemStack[3][9];

            for (int i = 0; i < 3; i++) {
                if (direction == Direction.HORIZONTAL) {
                    patterns[i][i * 3] = pattern[2];
                    patterns[i][i * 3 + 1] = pattern[1];
                    patterns[i][i * 3 + 2] = pattern[0];
                } else if (direction == Direction.VERTICAL) {
                    patterns[i][i] = pattern[0];
                    patterns[i][i + 3] = pattern[1];
                    patterns[i][i + 6] = pattern[2];
                }
            }

            for (CustomItemStack[] pattern : patterns) {
                if (matches(recipe, pattern)) return pattern;
            }
        } else if (patternType == PatternType.TWO_ONE) {
            if (getEmptyAmount(recipe) < 7) return null;
            CustomItemStack[][] patterns = new CustomItemStack[0][];

            if (direction == Direction.HORIZONTAL) {
                patterns = new CustomItemStack[][]{
                        new CustomItemStack[]{
                                pattern[0], pattern[1], null,
                                null,       null,       null,
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       pattern[0], pattern[1],
                                null,       null,       null,
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                pattern[0], pattern[1], null,
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                null,       pattern[0], pattern[1],
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                null,       null,       null,
                                pattern[0], pattern[1], null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                null,       null,       null,
                                null,       pattern[0], pattern[1]
                        }
                };
            } else if (direction == Direction.VERTICAL) {
                patterns = new CustomItemStack[][]{
                        new CustomItemStack[]{
                                pattern[0], null,       null,
                                pattern[1], null,       null,
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                pattern[0], null,       null,
                                pattern[1], null,       null
                        },
                        new CustomItemStack[]{
                                null,       pattern[0], null,
                                null,       pattern[1], null,
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                null,       pattern[0], null,
                                null,       pattern[1], null
                        },
                        new CustomItemStack[]{
                                null,       null,       pattern[0],
                                null,       null,       pattern[1],
                                null,       null,       null
                        },
                        new CustomItemStack[]{
                                null,       null,       null,
                                null,       null,       pattern[0],
                                null,       null,       pattern[1]
                        }
                };
            }

            for (CustomItemStack[] pattern : patterns) {
                if (matches(recipe, pattern)) return pattern;
            }
        } else if (patternType == PatternType.ONE) {
            if (getEmptyAmount(recipe) < 8) return null;
            CustomItemStack[][] patterns = new CustomItemStack[9][9];

            for (int i = 0; i < 9; i++) {
                patterns[i][i] = pattern[0];
            }

            for (CustomItemStack[] pattern : patterns) {
                if (matches(recipe, pattern)) return pattern;
            }
        }

        return null;
    }

    @Override
    @NotNull
    public Map<Integer, CustomItemStack> getForDisplay() {
        Map<Integer, CustomItemStack> items = new HashMap<>();
        int[] craftingGrid = CraftingMenu.craftingGrid;
        for (int slot : craftingGrid) {
            items.put(slot, null);
        }

        switch (patternType) {
            case ONE -> {
                items.put(craftingGrid[0], pattern[0]);
            }
            case TWO_ONE -> {
                switch (Objects.requireNonNull(direction)) {
                    case HORIZONTAL -> {
                        items.put(craftingGrid[0], pattern[0]);
                        items.put(craftingGrid[1], pattern[1]);
                    }
                    case VERTICAL -> {
                        items.put(craftingGrid[0], pattern[0]);
                        items.put(craftingGrid[3], pattern[1]);
                    }
                }
            }
            case THREE_ONE -> {
                switch (Objects.requireNonNull(direction)) {
                    case HORIZONTAL -> {
                        items.put(craftingGrid[0], pattern[2]);
                        items.put(craftingGrid[1], pattern[1]);
                        items.put(craftingGrid[2], pattern[0]);
                    }
                    case VERTICAL -> {
                        items.put(craftingGrid[0], pattern[0]);
                        items.put(craftingGrid[3], pattern[1]);
                        items.put(craftingGrid[6], pattern[2]);
                    }
                }
            }
            case TWO_TWO -> {
                items.put(craftingGrid[0], pattern[0]);
                items.put(craftingGrid[1], pattern[1]);
                items.put(craftingGrid[3], pattern[2]);
                items.put(craftingGrid[4], pattern[3]);
            }
            case TWO_THREE -> {
                switch (Objects.requireNonNull(direction)) {
                    case HORIZONTAL -> {
                        items.put(craftingGrid[0], pattern[0]);
                        items.put(craftingGrid[1], pattern[1]);
                        items.put(craftingGrid[2], pattern[2]);
                        items.put(craftingGrid[3], pattern[3]);
                        items.put(craftingGrid[4], pattern[4]);
                        items.put(craftingGrid[5], pattern[5]);
                    }
                    case VERTICAL -> {
                        items.put(craftingGrid[0], pattern[0]);
                        items.put(craftingGrid[1], pattern[1]);
                        items.put(craftingGrid[3], pattern[2]);
                        items.put(craftingGrid[4], pattern[3]);
                        items.put(craftingGrid[6], pattern[4]);
                        items.put(craftingGrid[7], pattern[5]);
                    }
                }
            }
            case FULL -> {
                items.put(craftingGrid[0], pattern[0]);
                items.put(craftingGrid[1], pattern[1]);
                items.put(craftingGrid[2], pattern[2]);
                items.put(craftingGrid[3], pattern[3]);
                items.put(craftingGrid[4], pattern[4]);
                items.put(craftingGrid[5], pattern[5]);
                items.put(craftingGrid[6], pattern[6]);
                items.put(craftingGrid[7], pattern[7]);
                items.put(craftingGrid[8], pattern[8]);
            }
        }

        return items;
    }

    @Override
    public int getResultSlot() {
        return CraftingMenu.resultSlot;
    }

    @NotNull
    public PatternType getPatternType() {
        return patternType;
    }

    @Nullable
    public Direction getDirection() {
        return direction;
    }

    /**
     * Check if a list of item stack matches with a pattern <br/>
     * An {@link IndexOutOfBoundsException} can be thrown if you don't send a recipe and a pattern with 9 items
     * @param recipe list to check
     * @param pattern pattern to check
     * @return if list matches with pattern
     */
    public boolean matches(@NotNull ItemStack[] recipe, @NotNull CustomItemStack[] pattern) {
        for (int i = 0; i < 9; i++) {
            CustomItemStack patternItem = pattern[i];
            if (patternItem == null && recipe[i] == null) continue;
            if (patternItem == null) return false;

            CustomItemStack recipeItem = CustomItemStack.fromItemStack(recipe[i]);

            if (recipeItem == null) return false;

            if (!patternItem.isSimilar(recipeItem) || recipeItem.getAmount() < patternItem.getAmount()) return false;
        }

        return true;
    }

    /**
     * Check how many items are null or AIR items in a list of item stack
     * @param recipe list to check
     * @return amount of empty item stack
     */
    private int getEmptyAmount(@NotNull ItemStack[] recipe) {
        int amount = 0;

        for (ItemStack itemStack : recipe) {
            if (itemStack == null || itemStack.getType() == Material.AIR) amount++;
        }

        return amount;
    }

    /**
     * Check if list of item stack contain only null or AIR items
     * @param recipe list to check
     * @return if they are all empty
     */
    private boolean emptyRecipe(@NotNull ItemStack[] recipe) {
        for (ItemStack itemStack : recipe) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return if pattern only contain null item
     */
    private boolean emptyPattern() {
        for (CustomItemStack itemStack : pattern) {
            if (itemStack != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Pattern type used to know how to check if recipe matches, for example, a door is {@link PatternType#TWO_THREE} and direction {@link Direction#VERTICAL}
     */
    private enum PatternType {
        FULL,
        TWO_THREE,
        TWO_TWO,
        THREE_ONE,
        TWO_ONE,
        ONE
    }

    /**
     * Direction of a crafting recipe used with {@link PatternType#TWO_THREE}, {@link PatternType#THREE_ONE} and {@link PatternType#TWO_ONE}
     */
    public enum Direction {
        VERTICAL,
        HORIZONTAL
    }
}

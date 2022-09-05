package fr.blockincraft.faylisia.items.recipes;

import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.menu.CraftingMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CraftingRecipe implements Recipe {
    private final CustomItemStack[] pattern;
    private final int resultAmount;
    private final PatternType patternType;
    private final Direction direction;

    public CraftingRecipe(int resultAmount, CustomItemStack p1, CustomItemStack p2, CustomItemStack p3,
                                            CustomItemStack p4, CustomItemStack p5, CustomItemStack p6,
                                            CustomItemStack p7, CustomItemStack p8, CustomItemStack p9) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3, p4, p5, p6, p7, p8, p9};
        this.patternType = PatternType.FULL;
        this.direction = null;
    }

    public CraftingRecipe(int resultAmount, CustomItemStack p1, CustomItemStack p2, CustomItemStack p3,
                                            CustomItemStack p4, CustomItemStack p5, CustomItemStack p6, Direction direction) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3, p4, p5, p6};
        this.patternType = PatternType.TWO_THREE;
        this.direction = direction == null ? Direction.HORIZONTAL : direction;
    }

    public CraftingRecipe(int resultAmount, CustomItemStack p1, CustomItemStack p2,
                                            CustomItemStack p3, CustomItemStack p4) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3, p4};
        this.patternType = PatternType.TWO_TWO;
        this.direction = null;
    }

    public CraftingRecipe(int resultAmount, CustomItemStack p1, CustomItemStack p2, CustomItemStack p3, Direction direction) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2, p3};
        this.patternType = PatternType.THREE_ONE;
        this.direction = direction;
    }

    public CraftingRecipe(int resultAmount, CustomItemStack p1, CustomItemStack p2, Direction direction) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1, p2};
        this.patternType = PatternType.TWO_ONE;
        this.direction = direction;
    }

    public CraftingRecipe(int resultAmount, CustomItemStack p1) {
        this.resultAmount = resultAmount;
        this.pattern = new CustomItemStack[]{p1};
        this.patternType = PatternType.ONE;
        this.direction = null;
    }

    @Override
    public String getMenuId() {
        return "crafting_table";
    }

    @Override
    public CustomItemStack[] getItems() {
        return pattern;
    }

    @Override
    public int getResultAmount() {
        return resultAmount;
    }

    @Override
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
                switch (direction) {
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
                switch (direction) {
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
                switch (direction) {
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

    public PatternType getPatternType() {
        return patternType;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean matches(ItemStack[] recipe, CustomItemStack[] pattern) {
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

    private int getEmptyAmount(ItemStack[] recipe) {
        int amount = 0;

        for (ItemStack itemStack : recipe) {
            if (itemStack == null || itemStack.getType() == Material.AIR) amount++;
        }

        return amount;
    }

    private boolean emptyRecipe(ItemStack[] recipe) {
        for (ItemStack itemStack : recipe) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return false;
            }
        }

        return true;
    }

    private boolean emptyPattern() {
        for (CustomItemStack itemStack : pattern) {
            if (itemStack != null && itemStack.getItem() != null) {
                return false;
            }
        }

        return true;
    }

    private enum PatternType {
        FULL,
        TWO_THREE,
        TWO_TWO,
        THREE_ONE,
        TWO_ONE,
        ONE
    }

    public enum Direction {
        VERTICAL,
        HORIZONTAL
    }
}

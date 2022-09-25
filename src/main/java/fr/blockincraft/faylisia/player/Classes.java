package fr.blockincraft.faylisia.player;

import fr.blockincraft.faylisia.displays.Skins;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.items.event.DamageType;
import fr.blockincraft.faylisia.items.event.Handlers;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * All players class with a defined skin, they will have bonus later
 */
public enum Classes {
    EXPLORER('6',
            "Explorateur",
            new String[]{
                    "&7Réduit de &c10% &7tous les dégâts",
                    "&a+50% &7" + Stats.SPEED.name,
                    "&a+50% &7" + Stats.MAGICAL_RESERVE.name,
                    "&c-20% &7" + Stats.DEFENSE.name,
                    "&c-20% &7" + Stats.HEALTH.name,
            },
            new Handlers() {
                @Override
                public long onDamage(@NotNull Player player, @NotNull CustomEntity customEntity, @NotNull DamageType damageType, long damage, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    return (long) (damage * 0.9);
                }

                @Override
                public double getStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    switch (stat) {
                        case SPEED, MAGICAL_RESERVE -> {
                            return value * 1.5;
                        }
                        case DEFENSE, HEALTH -> {
                            return value * 0.8;
                        }
                        default -> {
                            return value;
                        }
                    }
                }
            },
            Skins.EXPLORER,
            Material.LEATHER_BOOTS,
            -1
    ),
    SWORDSMAN('b',
            "Épéiste",
            new String[]{
                    "&7Augmente de &a40% &7les dégâts de mélée",
                    "&7Réduit de &c20% &7les dégâts magiques",
                    "&a+20% &7" + Stats.STRENGTH.name,
            },
            new Handlers() {
                @Override
                public long onDamage(@NotNull Player player, @NotNull CustomEntity customEntity, @NotNull DamageType damageType, long damage, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    return (long) (damageType == DamageType.MELEE_DAMAGE ? damage * 1.4 : damage * 0.8);
                }

                @Override
                public double getStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    return stat == Stats.STRENGTH ? value * 1.2 : value;
                }
            },
            Skins.SWORDSMAN,
            Material.IRON_SWORD,
            -1
    ),
    MAGE('d',
            "Mage",
            new String[]{
                    "&7Augmente de &a40% &7les dégâts magiques",
                    "&7Réduit de &c20% &7les dégâts de mélée",
                    "&a+50% &7" + Stats.MAGICAL_RESERVE.name,
                    "&c-20% &7" + Stats.DEFENSE.name,
            },
            new Handlers() {
                @Override
                public long onDamage(@NotNull Player player, @NotNull CustomEntity customEntity, @NotNull DamageType damageType, long damage, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    return (long) (damageType == DamageType.MAGIC_DAMAGE ? damage * 1.4 : damage * 0.8);
                }

                @Override
                public double getStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    switch (stat) {
                        case MAGICAL_RESERVE -> {
                            return value * 1.5;
                        }
                        case DEFENSE -> {
                            return value * 0.8;
                        }
                        default -> {
                            return value;
                        }
                    }
                }
            },
            Skins.MAGE,
            Material.STICK,
            -1
    ),
    TANK('8',
            "Tank",
            new String[]{
                    "&7Réduit de &c20% &7les dégâts reçus",
                    "&a+20% &7" + Stats.HEALTH.name,
                    "&a+20% &7" + Stats.DEFENSE.name,
                    "&c-20% &7" + Stats.STRENGTH.name,
                    "&c-20% &7" + Stats.SPEED.name
            },
            new Handlers() {
                @Override
                public long onTakeDamage(@NotNull Player player, @NotNull CustomEntity customEntity, @NotNull DamageType damageType, long damageTaken, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    return (long) (damageTaken * 0.8);
                }

                @Override
                public double getStat(@NotNull Player player, @NotNull Stats stat, double value, boolean inHand, boolean inArmorSlot) {
                    onHandlerCall();
                    switch (stat) {
                        case STRENGTH, SPEED -> {
                            return value * 0.8;
                        }
                        case HEALTH, DEFENSE -> {
                            return value * 1.2;
                        }
                        default -> {
                            return value;
                        }
                    }
                }
            },
            Skins.TANK,
            Material.DIAMOND_CHESTPLATE,
            -1
    );

    public final char color;
    public final String name;
    public final String[] desc;
    public final Handlers handlers;
    public final Skins skin;
    public final Material material;
    public final int customModelData;

    /**
     * @param color color chat to use {@link ChatColor}
     * @param name name of the class
     * @param skin skin of players which have selected this class
     * @param material material of item stack in menus
     * @param customModelData custom model data of item stack in menus
     */
    Classes(char color, @NotNull String name, @NotNull String[] desc, @NotNull Handlers handlers, @NotNull Skins skin, @NotNull Material material, int customModelData) {
        this.color = color;
        this.name = name;
        this.desc = desc;
        this.handlers = handlers;
        this.skin = skin;
        this.material = material;
        this.customModelData = customModelData;
    }

    /**
     * This method generate and return the item stack which represent this class
     * @return item stack which represent the class
     */
    @NotNull
    public ItemStack getAsItemStack() {
        // Create the item stack
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        // Apply display name and custom model data
        meta.setDisplayName(ColorsUtils.translateAll("&" + color + "&l" + name));
        meta.setCustomModelData(customModelData < 0 ? null : customModelData);

        meta.setLore(Arrays.stream(desc).map(ColorsUtils::translateAll).toList());

        // Add item flags
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        // Apply item meta
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}

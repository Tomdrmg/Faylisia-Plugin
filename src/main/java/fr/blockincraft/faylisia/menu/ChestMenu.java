package fr.blockincraft.faylisia.menu;

import fr.blockincraft.faylisia.listeners.MenuListener;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChestMenu {
    private boolean clickable;
    private boolean emptyClickable;
    private String title;
    private Inventory inv;
    private List<ItemStack> items;
    private Map<Integer, MenuClickHandler> handlers;
    private MenuOpeningHandler open;
    private MenuCloseHandler close;
    private MenuClickHandler playerclick;
    private MenuDragHandler drag;

    /**
     * Creates a new ChestMenu with the specifiedTitle
     *
     * @param title The title of the Menu
     * @param lines The number of lines to initialize
     */
    public ChestMenu(@NotNull String title, int lines) {
        this.title = ColorsUtils.translateAll(title);
        this.clickable = false;
        this.emptyClickable = true;
        this.items = new ArrayList<>();
        this.handlers = new HashMap<>();

        this.open = p -> {};
        this.close = p -> {};
        this.playerclick = (e) -> isPlayerInventoryClickable();
        this.drag = (e) -> true;

        if (lines <= 0) lines = 1;
        if (lines > 6) lines = 6;

        for (int i = 0; i < 9 * lines; i++) {
            ItemStack bgItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

            ItemMeta meta = bgItem.getItemMeta();
            meta.setDisplayName(String.valueOf(ChatColor.COLOR_CHAR));
            bgItem.setItemMeta(meta);

            this.addItem(i, bgItem, (e) -> false);
        }
    }

    /**
     * Refresh all items in menu for example if an object was edited
     */
    public abstract void refreshMenu();

    /**
     * Toggles whether Players can access there Inventory while viewing this Menu
     *
     * @param clickable Whether the Player can access his Inventory
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu setPlayerInventoryClickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    /**
     * Returns whether the Player's Inventory is accessible while viewing this Menu
     *
     * @return Whether the Player Inventory is clickable
     */
    public boolean isPlayerInventoryClickable() {
        return clickable;
    }

    /**
     * Toggles whether Players can click the empty menu slots while viewing this Menu
     *
     * @param emptyClickable Whether the Player can click empty slots
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu setEmptySlotsClickable(boolean emptyClickable) {
        this.emptyClickable = emptyClickable;
        return this;
    }

    /**
     * Returns whether the empty menu slots are clickable while viewing this Menu
     *
     * @return Whether the empty menu slots are clickable
     */
    public boolean isEmptySlotsClickable() {
        return emptyClickable;
    }

    /**
     * Adds a ClickHandler to ALL Slots of the Player's Inventory
     *
     * @param handler The MenuClickHandler
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu addPlayerInventoryClickHandler(@NotNull MenuClickHandler handler) {
        this.playerclick = handler;
        return this;
    }

    /**
     * Adds a DragHandler
     * @param handler the drag handler
     * @return the ChestMenu instance
     */
    @NotNull
    public ChestMenu addMenuDragHandler(@Nullable MenuDragHandler handler) {
        this.drag = handler;
        return this;
    }

    /**
     * Adds an Item to the Inventory in that Slot
     *
     * @param slot The Slot in the Inventory
     * @param item The Item for that Slot
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu addItem(int slot, @Nullable ItemStack item) {
        final int size = this.items.size();
        if (size > slot)
            this.items.set(slot, item);
        else {
            for (int i = 0; i < slot - size; i++) {
                this.items.add(null);
            }
            this.items.add(item);
        }
        return this;
    }

    /**
     * Adds an Item to the Inventory in that Slot as well as a Click Handler
     *
     * @param slot The Slot in the Inventory
     * @param item The Item for that Slot
     * @param clickHandler The MenuClickHandler for that Slot
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu addItem(int slot, @Nullable ItemStack item, @Nullable MenuClickHandler clickHandler) {
        addItem(slot, item);
        addMenuClickHandler(slot, clickHandler);
        return this;
    }

    /**
     * Returns the ItemStack in that Slot
     *
     * @param slot The Slot in the Inventory
     * @return The ItemStack in that Slot
     */
    @Nullable
    public ItemStack getItemInSlot(int slot) {
        setup();
        return this.inv.getItem(slot);
    }

    /**
     * Executes a certain Action upon clicking an Item in the Menu
     *
     * @param slot The Slot in the Inventory
     * @param handler The MenuClickHandler
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu addMenuClickHandler(int slot, @Nullable MenuClickHandler handler) {
        this.handlers.put(slot, handler);
        return this;
    }

    /**
     * Executes a certain Action upon opening this Menu
     *
     * @param handler The MenuOpeningHandler
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu addMenuOpeningHandler(@Nullable MenuOpeningHandler handler) {
        this.open = handler;
        return this;
    }

    /**
     * Executes a certain Action upon closing this Menu
     *
     * @param handler The MenuCloseHandler
     * @return The ChestMenu Instance
     */
    @NotNull
    public ChestMenu addMenuCloseHandler(@NotNull MenuCloseHandler handler) {
        this.close = handler;
        return this;
    }

    /**
     * Returns an Array containing the Contents of this Inventory
     *
     * @return The Contents of this Inventory
     */
    @NotNull
    public ItemStack[] getContents() {
        setup();
        return this.inv.getContents();
    }

    private void setup() {
        if (this.inv != null)
            return;
        this.inv = Bukkit.createInventory(null, ((int) Math.ceil(this.items.size() / 9F)) * 9, title);
        for (int i = 0; i < this.items.size(); i++) {
            this.inv.setItem(i, this.items.get(i));
        }
    }

    /**
     * Modifies an ItemStack in an ALREADY OPENED ChestMenu
     *
     * @param slot The Slot of the Item which will be replaced
     * @param item The new Item
     */
    public void replaceExistingItem(int slot, @Nullable ItemStack item) {
        setup();
        this.inv.setItem(slot, item);
    }

    public void replaceExistingItem(int slot, @Nullable ItemStack item, @Nullable MenuClickHandler handler) {
        setup();
        this.inv.setItem(slot, item);
        this.addMenuClickHandler(slot, handler);
    }

    /**
     * Opens this Menu for the specified Player/s
     *
     * @param players The Players who will see this Menu
     */
    public void open(@NotNull Player... players) {
        setup();
        for (Player p : players) {
            p.openInventory(this.inv);
            MenuListener.menus.put(p.getUniqueId(), this);
            if (open != null) open.onOpen(p);
        }
    }

    /**
     * Returns the MenuClickHandler which was registered for the specified Slot
     *
     * @param slot The Slot in the Inventory
     * @return The MenuClickHandler registered for the specified Slot
     */
    @Nullable
    public MenuClickHandler getMenuClickHandler(int slot) {
        return handlers.get(slot);
    }

    /**
     * Returns the registered MenuCloseHandler
     *
     * @return The registered MenuCloseHandler
     */
    @NotNull
    public MenuCloseHandler getMenuCloseHandler() {
        return close;
    }

    /**
     * Returns the registered MenuOpeningHandler
     *
     * @return The registered MenuOpeningHandler
     */
    @Nullable
    public MenuOpeningHandler getMenuOpeningHandler() {
        return open;
    }

    /**
     * Returns the registered MenuClickHandler
     * for Player Inventories
     *
     * @return The registered MenuClickHandler
     */
    @NotNull
    public MenuClickHandler getPlayerInventoryClickHandler() {
        return playerclick;
    }

    @Nullable
    public MenuDragHandler getMenuDragHandler() {
        return drag;
    }

    /**
     * Converts this ChestMenu Instance into a
     * normal Inventory
     *
     * @return The converted Inventory
     */
    @Nullable
    public Inventory toInventory() {
        return this.inv;
    }

    @FunctionalInterface
    public interface MenuClickHandler {

        public boolean onClick(InventoryClickEvent e);
    }

    @FunctionalInterface
    public interface MenuOpeningHandler {

        public void onOpen(Player p);
    }

    @FunctionalInterface
    public interface MenuCloseHandler {

        public void onClose(Player p);
    }

    @FunctionalInterface
    public interface MenuDragHandler {

        public boolean onDrag(InventoryDragEvent e);
    }
}

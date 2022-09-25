package fr.blockincraft.faylisia.items.specificitems;

import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.event.HandlerItemModel;
import fr.blockincraft.faylisia.items.event.Handlers;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class HandlerLacrymaItem extends CustomItem implements HandlerItemModel {
    private Handlers handlers = new Handlers() {};

    public HandlerLacrymaItem(@NotNull Material material, @NotNull String id) {
        super(material, id);
    }

    /**
     * Change handlers of this item
     * @param handlers new value
     * @return this instance
     */
    @NotNull
    public HandlerLacrymaItem setHandlers(@NotNull Handlers handlers) {
        if (this.isRegistered()) throw new ChangeRegisteredItem();
        this.handlers = handlers;

        return this;
    }

    @Override
    @NotNull
    public Handlers getHandlers() {
        return handlers;
    }

    @Override
    @NotNull
    protected String getType() {
        return "LACRYMA";
    }
}

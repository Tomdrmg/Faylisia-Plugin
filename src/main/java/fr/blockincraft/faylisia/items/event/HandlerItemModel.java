package fr.blockincraft.faylisia.items.event;

import org.jetbrains.annotations.NotNull;

/**
 * An item which has handlers, for example an item which double damage on zombies
 */
public interface HandlerItemModel {
    /**
     * @return Item handlers
     */
    @NotNull
    Handlers getHandlers();
}

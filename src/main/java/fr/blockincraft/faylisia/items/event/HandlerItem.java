package fr.blockincraft.faylisia.items.event;

import fr.blockincraft.faylisia.items.event.Handlers;
import org.jetbrains.annotations.NotNull;

/**
 * An item which has handlers, for example an item which double damage on zombies
 */
public interface HandlerItem {
    /**
     * @return Item handlers
     */
    @NotNull
    Handlers getHandlers();
}

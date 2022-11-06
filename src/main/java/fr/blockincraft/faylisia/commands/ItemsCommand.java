package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.commands.base.Command;
import fr.blockincraft.faylisia.commands.base.CommandAction;
import fr.blockincraft.faylisia.commands.base.CommandParam;
import fr.blockincraft.faylisia.commands.base.ParamType;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.menu.viewer.ItemsViewerMenu;
import fr.blockincraft.faylisia.menu.viewer.RecipeViewerMenu;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ItemsCommand extends Command {
    @Override
    public @NotNull String getCommand() {
        return "items";
    }

    @CommandAction(permission = "faylisia.items.menu", onlyPlayers = true, prefixes = {"menu"})
    public void menu(Player player) {
        new ItemsViewerMenu(null).open(player);
    }

    @CommandAction(permission = "faylisia.items.recipe", onlyPlayers = true, prefixes = {"recipe"})
    public void recipe(Player player, @CommandParam(type = ParamType.CUSTOM_ITEM) CustomItem item) {
        new RecipeViewerMenu(item, null).open(player);
    }

    @CommandAction(permission = "faylisia.items.give", onlyPlayers = false, prefixes = {"give"})
    public void give(CommandSender sender, @CommandParam(type = ParamType.ONLINE_PLAYER_SUPPORT_ALL) Player[] players, @CommandParam(type = ParamType.CUSTOM_ITEM_STACK) CustomItemStack itemStack) {
        giveAmount(sender, players, itemStack, 1);
    }

    @CommandAction(permission = "faylisia.items.give", onlyPlayers = false, prefixes = {"give"})
    public void giveAmount(CommandSender sender, @CommandParam(type = ParamType.ONLINE_PLAYER_SUPPORT_ALL) Player[] players, @CommandParam(type = ParamType.CUSTOM_ITEM_STACK) CustomItemStack itemStack, @CommandParam(type = ParamType.AMOUNT) Integer amount) {
        itemStack.setAmount(amount);

        CustomPlayerDTO player = sender instanceof Player pl ? Faylisia.getInstance().getRegistry().getOrRegisterPlayer(pl.getUniqueId()) : null;

        if (players.length == 1) {
            PlayerUtils.giveOrDrop(players[0], itemStack.getAsItemStack());

            CustomPlayerDTO target = Faylisia.getInstance().getRegistry().getOrRegisterPlayer(players[0].getUniqueId());

            Map<String, String> parameters = new HashMap<>();
            parameters.put("%item%", itemStack.getItem().getName(itemStack));
            parameters.put("%amount%", String.valueOf(amount));

            if (player != null && target.getPlayer().equals(player.getPlayer())) {
                sender.sendMessage(itemStack.getAmount() == 1 ? Messages.GIVE_SELF_AN_ITEM.get(parameters) : Messages.GIVE_SELF_MULTIPLE_ITEMS.get(parameters));
            } else {
                parameters.put("%player_name%", target.getNameToUse());
                sender.sendMessage(itemStack.getAmount() == 1 ? Messages.GIVE_OTHER_AN_ITEM.get(parameters) : Messages.GIVE_OTHER_MULTIPLE_ITEMS.get(parameters));

                parameters.put("%player_name%", player == null ? "La Console" : player.getNameToUse());
                players[0].sendMessage(itemStack.getAmount() == 1 ? Messages.RECEIVE_FROM_AN_ITEM.get(parameters) : Messages.RECEIVE_FROM_MULTIPLE_ITEMS.get(parameters));
            }
        } else {
            Map<String, String> parameters = new HashMap<>();

            parameters.put("%item%", itemStack.getItem().getName(itemStack));
            parameters.put("%player_name%", player == null ? "La Console" : player.getNameToUse());
            parameters.put("%amount%", String.valueOf(amount));

            for (Player pl : players) {
                PlayerUtils.giveOrDrop(pl, itemStack.getAsItemStack());

                pl.sendMessage(itemStack.getAmount() == 1 ? Messages.RECEIVE_FROM_AN_ITEM.get(parameters) : Messages.RECEIVE_FROM_MULTIPLE_ITEMS.get(parameters));
            }

            sender.sendMessage(itemStack.getAmount() == 1 ? Messages.GIVE_ALL_AN_ITEM.get(parameters) : Messages.GIVE_ALL_MULTIPLE_ITEMS.get(parameters));
        }
    }
}

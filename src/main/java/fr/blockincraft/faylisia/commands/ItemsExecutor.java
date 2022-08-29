package fr.blockincraft.faylisia.commands;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItem;
import fr.blockincraft.faylisia.menu.viewer.ItemsViewerMenu;
import fr.blockincraft.faylisia.menu.viewer.RecipeViewerMenu;
import fr.blockincraft.faylisia.utils.PlayerUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemsExecutor implements CommandExecutor {
    private static final String command = "items";
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (sender instanceof Player player) {
            if (args.length == 1 && args[0].equalsIgnoreCase("menu")) {
                if (!player.hasPermission("faylisia.items.menu")) {
                    sendNoPermissionMessage(sender);
                    return true;
                }

                new ItemsViewerMenu(null).open(player);
                return true;
            } else if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("give")) {
                if (!player.hasPermission("faylisia.items.give")) {
                    sendNoPermissionMessage(sender);
                    return true;
                }

                if (args[1].equalsIgnoreCase("@a")) {
                    CustomItem item = getItem(args[2], sender);
                    if (item == null) return true;

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("%item%", item.getName());

                    Integer amount = null;
                    boolean multiple = false;
                    if (args.length == 4) {
                        multiple = true;
                        amount = getInteger(args[3], sender);
                        if (amount == null) return true;

                        parameters.put("%amount%", String.valueOf(amount));
                    }

                    player.sendMessage(multiple ? Messages.GIVE_ALL_MULTIPLE_ITEMS.get(parameters) : Messages.GIVE_ALL_AN_ITEM.get(parameters));

                    CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getUniqueId());
                    parameters.put("%player_name%", custom.getName());

                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (!multiple) {
                            PlayerUtils.giveOrDrop(target, item.getAsItemStack());
                            if (target != player) {
                                target.sendMessage(Messages.RECEIVE_FROM_AN_ITEM.get(parameters));
                            }
                        } else {
                            PlayerUtils.giveOrDrop(target, item.getAsItemStack(amount));
                            if (target != player) {
                                target.sendMessage(Messages.RECEIVE_FROM_MULTIPLE_ITEMS.get(parameters));
                            }
                        }
                    }
                } else {
                    Player target = getPlayer(args[1], sender);
                    if (target == null) return true;

                    CustomItem item = getItem(args[2], sender);
                    if (item == null) return true;

                    CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());
                    CustomPlayerDTO customTarget = registry.getOrRegisterPlayer(target.getUniqueId());

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("%item%", item.getName());

                    if (args.length == 3) {
                        PlayerUtils.giveOrDrop(target, item.getAsItemStack());

                        if (target == player) {
                            player.sendMessage(Messages.GIVE_SELF_AN_ITEM.get(parameters));
                        } else {
                            parameters.put("%player_name%", customTarget.getName());
                            player.sendMessage(Messages.GIVE_OTHER_AN_ITEM.get(parameters));
                            parameters.put("%player_name%", customPlayer.getName());
                            target.sendMessage(Messages.RECEIVE_FROM_AN_ITEM.get(parameters));
                        }
                    } else {
                        Integer amount = getInteger(args[3], sender);
                        if (amount == null) return true;

                        PlayerUtils.giveOrDrop(target, item.getAsItemStack(amount));

                        parameters.put("%amount%", String.valueOf(amount));
                        if (target == player) {
                            player.sendMessage(Messages.GIVE_SELF_MULTIPLE_ITEMS.get(parameters));
                        } else {
                            parameters.put("%player_name%", customTarget.getName());
                            player.sendMessage(Messages.GIVE_OTHER_MULTIPLE_ITEMS.get(parameters));
                            parameters.put("%player_name%", customPlayer.getName());
                            target.sendMessage(Messages.RECEIVE_FROM_MULTIPLE_ITEMS.get(parameters));
                        }
                    }
                }
                return true;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("recipe")) {
                if (!player.hasPermission("faylisia.items.recipe")) {
                    sendNoPermissionMessage(sender);
                    return true;
                }

                CustomItem item = getItem(args[1], sender);
                if (item == null) return true;

                new RecipeViewerMenu(item, null).open(player);
                return true;
            }
        } else {
            if ((args.length == 3 || args.length == 4) && args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("faylisia.items.give")) {
                    sendNoPermissionMessage(sender);
                    return true;
                }

                if (args[1].equalsIgnoreCase("@a")) {
                    CustomItem item = getItem(args[2], sender);
                    if (item == null) return true;

                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (args.length == 3) {
                            PlayerUtils.giveOrDrop(target, item.getAsItemStack());
                        } else {
                            Integer amount = getInteger(args[3], sender);
                            if (amount == null) return true;

                            PlayerUtils.giveOrDrop(target, item.getAsItemStack(amount));
                        }
                    }
                } else {
                    Player target = getPlayer(args[1], sender);
                    if (target == null) return true;

                    CustomItem item = getItem(args[2], sender);
                    if (item == null) return true;

                    if (args.length == 3) {
                        PlayerUtils.giveOrDrop(target, item.getAsItemStack());
                    } else {
                        Integer amount = getInteger(args[3], sender);
                        if (amount == null) return true;

                        PlayerUtils.giveOrDrop(target, item.getAsItemStack(amount));
                    }
                }
            }
        }

        sendHelpMessage(sender);
        return true;
    }

    public void sendHelpMessage(CommandSender sender) {
        Map<String, String> parameters = new HashMap<>();

        BaseComponent message = new TextComponent(Messages.HELP_MESSAGE.get(parameters));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://faylis.xyz/wiki/commands"));

        sender.spigot().sendMessage(message);
    }

    public void sendNoPermissionMessage(CommandSender sender) {
        Map<String, String> parameters = new HashMap<>();

        sender.sendMessage(Messages.NO_PERMISSION_MESSAGE.get(parameters));
    }

    public Player getPlayer(String arg, CommandSender sender) {
        if (arg.equals("@s") && sender instanceof Player player) {
            return player;
        }

        Player player = null;

        for (Player pl : Bukkit.getOnlinePlayers()) {
            CustomPlayerDTO custom = registry.getOrRegisterPlayer(pl.getUniqueId());
            if (custom.getName().equalsIgnoreCase(arg)) {
                player = pl;
                break;
            }
        }

        if (player == null) {
            sendUnknownPlayerMessage(sender, arg);
        }

        return player;
    }

    public void sendUnknownPlayerMessage(CommandSender sender, String targetName) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%target_name%", targetName);

        sender.sendMessage(Messages.UNKNOWN_PLAYER_MESSAGE.get(parameters));
    }

    public CustomItem getItem(String arg, CommandSender sender) {
        CustomItem item = Faylisia.getInstance().getRegistry().getItemsById().get(arg.toLowerCase(Locale.ROOT));

        if (item == null || !item.isRegistered()) {
            sendInvalidItemMessage(sender, arg);
        }

        return item;
    }

    public void sendInvalidItemMessage(CommandSender sender, String itemId) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%item_id%", itemId);

        sender.sendMessage(Messages.INVALID_ITEM_MESSAGE.get(parameters));
    }

    public Integer getInteger(String arg, CommandSender sender) {
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            sendInvalidNumber(sender, arg);
        }

        return null;
    }

    public void sendInvalidNumber(CommandSender sender, String number) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("%number%", number);

        sender.sendMessage(Messages.INVALID_NUMBER_MESSAGE.get(parameters));
    }
}

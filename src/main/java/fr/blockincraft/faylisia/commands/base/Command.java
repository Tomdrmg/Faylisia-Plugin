package fr.blockincraft.faylisia.commands.base;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.configurable.Messages;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public abstract class Command {
    @NotNull
    public abstract String getCommand();

    public final void register() throws CommandException {
        PluginCommand command = Faylisia.getInstance().getCommand(getCommand());
        if (command == null) return;

        List<CommandMethod> methods = new ArrayList<>();

        for (Method method : this.getClass().getMethods()) {
            if (method.isAnnotationPresent(CommandAction.class)) {
                CommandAction methodAnnotation = method.getAnnotation(CommandAction.class);

                if (Void.class.isAssignableFrom(method.getReturnType())) throw new CommandException("Command methods must return void!");
                if (!method.canAccess(this)) throw new CommandException("Method cannot be acceded!");
                if (method.getParameterCount() < 1 || !method.getParameterTypes()[0].isAssignableFrom(methodAnnotation.onlyPlayers() ? Player.class : CommandSender.class)) throw new CommandException("First parameter of the method must be a '" + (methodAnnotation.onlyPlayers() ? "Player" : "CommandSender") + "'!");

                List<ParamType> params = new ArrayList<>();

                for (int i = 1; i < method.getParameterCount(); i++) {
                    Parameter param = method.getParameters()[i];
                    if (!param.isAnnotationPresent(CommandParam.class)) throw new CommandException("Param must have the 'CommandParam' annotation!");

                    CommandParam paramAnnotation = param.getAnnotation(CommandParam.class);
                    if (!param.getType().isAssignableFrom(paramAnnotation.type().type)) throw new CommandException("Param hasn't the same type of the parser!");

                    if (paramAnnotation.type().allEnd && i != method.getParameterCount() - 1) throw new CommandException("This param can only be used as last!");

                    params.add(paramAnnotation.type());
                }

                methods.add(new CommandMethod(methodAnnotation.onlyPlayers(), methodAnnotation.prefixes(), methodAnnotation.permission(), params.toArray(new ParamType[0]), method));
            }
        }

        command.setExecutor(new TabExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
                for (CommandMethod method : methods) {
                    switch (method.isComplete(sender, args)) {
                        case COMPLETE -> {
                            if (method.onlyPlayers && !(sender instanceof Player)) {
                                sender.sendMessage(Messages.ONLY_PLAYERS_COMMAND_MESSAGE.get());
                                return true;
                            }
                            if (!method.canExecute(sender)) {
                                sender.sendMessage(Messages.NO_PERMISSION_MESSAGE.get());
                                return true;
                            }

                            try {
                                method.call(sender, args);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        case INVALID_ARG -> {
                            return true;
                        }
                    }
                }

                Map<String, String> parameters = new HashMap<>();

                BaseComponent message = new TextComponent(Messages.HELP_MESSAGE.get(parameters));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://faylisia.fr/wiki/commands"));

                sender.spigot().sendMessage(message);
                return true;
            }

            @Override
            @NotNull
            public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
                List<String> completion = new ArrayList<>();

                String[] firstArgs = new String[args.length - 1];
                System.arraycopy(args, 0, firstArgs, 0, args.length - 1);

                for (CommandMethod method : methods) {
                    if (method.onlyPlayers && !(sender instanceof Player)) continue;
                    if (!method.canExecute(sender)) continue;

                    if (method.isStart(sender, firstArgs)) {
                        completion.addAll(method.getCompletion(sender, args.length - 1, args[args.length - 1]));
                    }
                }

                return completion;
            }
        });
    }

    public class CommandMethod {
        private final boolean onlyPlayers;
        private final String[] prefixes;
        private final String permission;
        private final ParamType[] params;
        private final Method method;

        public CommandMethod(boolean onlyPlayers, @NotNull String[] prefixes, @NotNull String permission, @NotNull ParamType[] params, @NotNull Method method) {
            this.onlyPlayers = onlyPlayers;
            this.prefixes = prefixes;
            this.permission = permission;
            this.params = params;
            this.method = method;
        }

        public boolean canExecute(CommandSender sender) {
            if (permission.equalsIgnoreCase("")) return true;

            return sender.hasPermission(permission);
        }

        public boolean isStart(@NotNull CommandSender sender, @NotNull String[] params) {
            if (params.length > prefixes.length + this.params.length) return false;

            for (int i = 0; i < params.length; i++) {
                if (i < prefixes.length) {
                    if (!prefixes[i].equalsIgnoreCase(params[i])) return false;
                } else {
                    ParamType param = this.params[i - prefixes.length];
                    if (param.allEnd) {
                        StringBuilder sb = new StringBuilder(params[i]);
                        for (int l = i + 1; l < params.length; l++) {
                            sb.append(" ").append(params[l]);
                        }

                        if (param.parser.parse(sb.toString(), sender, false) == null) return false;
                        break;
                    } else {
                        if (param.parser.parse(params[i], sender, false) == null) return false;
                    }
                }
            }

            return true;
        }

        public CompletionState isComplete(@NotNull CommandSender sender, @NotNull String[] params) {
            if (params.length != prefixes.length + this.params.length) return CompletionState.INCOMPLETE;

            for (int i = 0; i < params.length; i++) {
                if (i < prefixes.length) {
                    if (!prefixes[i].equalsIgnoreCase(params[i])) return CompletionState.INCOMPLETE;
                } else {
                    ParamType param = this.params[i - prefixes.length];
                    if (param.allEnd) {
                        StringBuilder sb = new StringBuilder(params[i]);
                        for (int l = i + 1; l < params.length; l++) {
                            sb.append(" ").append(params[l]);
                        }

                        if (param.parser.parse(sb.toString(), sender, true) == null) return CompletionState.INVALID_ARG;
                        break;
                    } else {
                        if (param.parser.parse(params[i], sender, true) == null) return CompletionState.INVALID_ARG;
                    }
                }
            }

            return CompletionState.COMPLETE;
        }

        public void call(@NotNull CommandSender sender, @NotNull String[] params) throws InvocationTargetException, IllegalAccessException, InvalidArgumentsException {
            if (params.length != prefixes.length + this.params.length) throw new InvalidArgumentsException("invalid parameters amount");
            if (onlyPlayers && !(sender instanceof Player)) throw new InvalidArgumentsException("Only players can execute this command method!");

            List<Object> args = new ArrayList<>(List.of(onlyPlayers ? (Player) sender : sender));

            for (int i = prefixes.length; i < params.length; i++) {
                Object o = this.params[i - prefixes.length].parser.parse(params[i], sender, true);
                if (o == null) throw new InvalidArgumentsException("Param " + i + " (" + (i - prefixes.length) + ") is invalid!");

                args.add(o);
            }

            method.invoke(Command.this, args.toArray(new Object[0]));
        }

        @NotNull
        public List<String> getCompletion(@NotNull CommandSender sender, int index, @NotNull String currentValue) {
            if (index < prefixes.length) {
                String p = prefixes[index];

                if (p.toLowerCase(Locale.ROOT).startsWith(currentValue.toLowerCase(Locale.ROOT))) {
                    return List.of(prefixes[index]);
                } else {
                    return new ArrayList<>();
                }
            } else {
                index -= prefixes.length;
                if (index < params.length) {
                    return params[index].completer.complete(currentValue, sender);
                } else {
                    return new ArrayList<>();
                }
            }
        }

        public enum CompletionState {
            INCOMPLETE,
            INVALID_ARG,
            COMPLETE
        }

        public static class InvalidArgumentsException extends Exception {
            public InvalidArgumentsException(String ex) {
                super("Invalid arguments or command method usage: " + ex);
            }
        }
    }

    public static class CommandException extends Exception {
        public CommandException(String ex) {
            super(ex);
        }
    }
}

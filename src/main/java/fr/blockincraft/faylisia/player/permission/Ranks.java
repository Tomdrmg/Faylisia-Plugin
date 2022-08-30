package fr.blockincraft.faylisia.player.permission;

import fr.blockincraft.faylisia.Faylisia;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public enum Ranks {
    FOUNDER(0, "Fondateur",
            "&grad(Fondateur #ff3c00 #9000ff)",
            "&grad(%player_name% #ff3c00 #9000ff)",
            "&grad([Fondateur]\\_%player_name% #ff3c00 #9000ff)",
            '4',
            '4',
            new PermissionBuilder()
                    .set(".*", PermissionState.TRUE)
                    .set("minecraft.command.op", PermissionState.FALSE)
                    .set("bukkit.command.reload", PermissionState.DEV)
                    .set("minecraft.command.msg", PermissionState.FALSE)
                    .build()
    ),
    ADMINISTRATOR(1,
            "Administrateur",
            "&cAdministrateur",
            "&c%player_name%",
            "&c[Administrateur] %player_name%",
            'c',
            'c',
            new PermissionBuilder()
                    .set(".*", PermissionState.TRUE)
                    .set("minecraft.command.op", PermissionState.FALSE)
                    .set("minecraft.command.deop", PermissionState.FALSE)
                    .set("bukkit.command.reload", PermissionState.FALSE)
                    .set("minecraft.command.defaultgamemode", PermissionState.FALSE)
                    .set("minecraft.command.difficulty", PermissionState.FALSE)
                    .set("minecraft.command.effect", PermissionState.FALSE)
                    .set("minecraft.command.give", PermissionState.FALSE)
                    .set("minecraft.command.kill", PermissionState.FALSE)
                    .set("minecraft.command.stop", PermissionState.FALSE)
                    .set("minecraft.command.summon", PermissionState.FALSE)
                    .set("minecraft.command.time", PermissionState.FALSE)
                    .set("minecraft.command.msg", PermissionState.FALSE)
                    .set("minecraft.command.weather", PermissionState.FALSE)
                    .set("faylisia.command.discord", PermissionState.FALSE)
                    .set("faylisia.discord", PermissionState.FALSE)
                    .build()
    ),
    BUILDER(2,
            "Builder",
            "&aBuilder",
            "&a%player_name%",
            "&a[Builder] %player_name%",
            'a',
            'a',
            new PermissionBuilder()
                    .set("worldedit.*", PermissionState.DEV)
                    .set("minecraft.command.gamemode", PermissionState.DEV)
                    .set("faylisia.command.break", PermissionState.DEV)
                    .set("faylisia.break", PermissionState.DEV)
                    .build()
    ),
    MODERATOR(3,
            "Modérateur",
            "&9Modérateur",
            "&9%player_name%",
            "&9[Modérateur] %player_name%",
            '9',
            '9',
            new PermissionBuilder()
                    .set("minecraft.command.teleport", PermissionState.TRUE)
                    .set("faylisia.spawn.teleport_others", PermissionState.TRUE)
                    .set("faylisia.chat_color", PermissionState.TRUE)
                    .set("faylisia.chat_hex_color", PermissionState.TRUE)
                    .build()
    ),
    PLAYER(4,
            "Joueur",
            "&7Joueur",
            "&7%player_name%",
            "&7[Joueur] %player_name%",
            '7',
            '7',
            new PermissionBuilder()
                    .set("faylisia.command.class", PermissionState.TRUE)
                    .set("faylisia.command.menu", PermissionState.TRUE)
                    .set("faylisia.command.items", PermissionState.TRUE)
                    .set("faylisia.command.spawn", PermissionState.TRUE)
                    .set("faylisia.class", PermissionState.TRUE)
                    .set("faylisia.items.menu", PermissionState.TRUE)
                    .set("faylisia.items.recipe", PermissionState.TRUE)
                    .set("faylisia.spawn.teleport", PermissionState.TRUE)
                    .set("faylisia.menu.open", PermissionState.TRUE)
                    .build()
    );

    public final int index;
    public final String name;
    public final String displayName;
    public final String playerName;
    public final String chatName;
    public final char mainColor;
    public final char accentColor;
    public final Permission[] permissions;

    Ranks(int index, String name, String displayName, String playerName, String chatName, char mainColor, char accentColor, Permission... permissions) {
        this.index = index;
        this.name = name;
        this.displayName = displayName;
        this.playerName = playerName;
        this.chatName = chatName;
        this.mainColor = mainColor;
        this.accentColor = accentColor;
        this.permissions = permissions == null ? new Permission[0] : permissions;
    }

    public List<Ranks> getSubRanks() {
        List<Ranks> ranks = new ArrayList<>();

        for (Ranks rank : values()) {
            if (index < rank.index) ranks.add(rank);
        }

        return ranks;
    }

    public boolean hasPerm(String perm) {
        for (Permission permission : permissions) {
            String permIn = permission.getPerm();
            if (permIn.equals(perm)) {
                return true;
            }
        }

        for (Ranks subRank : getSubRanks()) {
            if (subRank.hasPerm(perm)) {
                return true;
            }
        }

        return false;
    }

    public PermissionState getPerm(String perm) {
        for (Permission permission : permissions) {
            String permIn = permission.getPerm();

            if (permIn.equals(perm)) {
                return permission.getState();
            }
        }

        for (Ranks subRank : getSubRanks()) {
            PermissionState state = subRank.getPerm(perm);
            if (state != null) {
                return state;
            }
        }

        return null;
    }

    public static void applyPermissions(Player player, Ranks rank) {
        PermissionAttachment attachment = player.addAttachment(Faylisia.getInstance());

        for (String perm : Permission.allPerms) {
            if (rank.hasPerm(perm)) {
                PermissionState state = rank.getPerm(perm);
                attachment.setPermission(perm, PermissionState.getValue(state));
            } else {
                attachment.setPermission(perm, false);
            }
        }

        for (String perm : Permission.otherPerms) {
            if (rank.hasPerm(perm)) {
                PermissionState state = rank.getPerm(perm);
                attachment.setPermission(perm, PermissionState.getValue(state));
            } else {
                attachment.setPermission(perm, false);
            }
        }

        player.updateCommands();
    }
}

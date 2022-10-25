package fr.blockincraft.faylisia.player.permission;

import fr.blockincraft.faylisia.Faylisia;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
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
    DEVELOPER(
            2,
            "Developer",
            "&dDéveloppeur",
            "&d%player_name%",
            "&d[Développeur] %player_name%",
            'd',
            'd'
    ),
    RESP_BUILDER(3,
            "Responsable_Builder",
            "&grad(Responsable\\_Builder #9df700 #099951)",
            "&grad(%player_name% #9df700 #099951)",
            "&grad([Responsable\\_Builder]\\_%player_name% #9df700 #099951)",
            'a',
            'a',
            new PermissionBuilder()
                    .build()
    ),
    CHEF_BUILDER(4,
            "Chef_Builder",
            "&aChef Builder",
            "&a%player_name%",
            "&a[Chef Builder] %player_name%",
            'a',
            'a',
            new PermissionBuilder()
                    .set("minecraft.command.teleport", PermissionState.DEV)
                    .set("faylisia.command.flyspeed", PermissionState.DEV)
                    .set("faylisia.flyspeed", PermissionState.DEV)
                    .build()
    ),
    BUILDER(5,
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
                    .set("faylisia.command.fly", PermissionState.DEV)
                    .set("faylisia.fly", PermissionState.DEV)
                    .set("minecraft.command.teleport", PermissionState.FALSE)
                    .set("faylisia.spawn.teleport_others", PermissionState.FALSE)
                    .set("faylisia.chat_color", PermissionState.FALSE)
                    .set("faylisia.chat_hex_color", PermissionState.FALSE)
                    .set("minecraft.command.teleport", PermissionState.FALSE)
                    .set("faylisia.command.flyspeed", PermissionState.FALSE)
                    .set("faylisia.flyspeed", PermissionState.FALSE)
                    .build()
    ),
    RESP_MODERATOR(6,
            "Responsable_Modérateur",
            "&grad(Responsable\\_Modérateur #00f7b5 #0077f7)",
            "&grad(%player_name% #00f7b5 #0077f7)",
            "&grad([Responsable\\_Modérateur]\\_%player_name% #00f7b5 #0077f7)",
            '9',
            '9',
            new PermissionBuilder()
                    .set("minecraft.command.teleport", PermissionState.DEV)
                    .set("faylisia.command.flyspeed", PermissionState.DEV)
                    .set("faylisia.flyspeed", PermissionState.DEV)
                    .set("minecraft.command.teleport", PermissionState.TRUE)
                    .set("faylisia.spawn.teleport_others", PermissionState.TRUE)
                    .set("faylisia.chat_color", PermissionState.TRUE)
                    .set("faylisia.chat_hex_color", PermissionState.TRUE)
                    .build()
    ),
    MODERATOR(7,
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
    PLAYER(8,
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
                    .set("faylisia.command.link", PermissionState.TRUE)
                    .set("faylisia.command.msg", PermissionState.TRUE)
                    .set("faylisia.class", PermissionState.TRUE)
                    .set("faylisia.items.menu", PermissionState.TRUE)
                    .set("faylisia.items.recipe", PermissionState.TRUE)
                    .set("faylisia.spawn.teleport", PermissionState.TRUE)
                    .set("faylisia.menu.open", PermissionState.TRUE)
                    .set("faylisia.link", PermissionState.TRUE)
                    .set("faylisia.msg", PermissionState.TRUE)
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

    /**
     * @param index index to order ranks in tab and for sub ranks
     * @param name name of the rank
     * @param displayName display name of the rank (with colors)
     * @param playerName player name when have this rank (to apply colors)
     * @param chatName chat name (player name + display name in other var to apply gradient on two)
     * @param mainColor main color char to use {@link ChatColor}
     * @param accentColor accent color char to use {@link ChatColor}
     * @param permissions all permissions and their state to applied of player (default = false)
     */
    Ranks(int index, @NotNull String name, @NotNull String displayName, @NotNull String playerName, @NotNull String chatName, char mainColor, char accentColor, @Nullable Permission... permissions) {
        this.index = index;
        this.name = name;
        this.displayName = displayName;
        this.playerName = playerName;
        this.chatName = chatName;
        this.mainColor = mainColor;
        this.accentColor = accentColor;
        this.permissions = permissions == null ? new Permission[0] : permissions;
    }

    /**
     * Get all ranks that are below this rank
     * @return sub ranks
     */
    @NotNull
    public List<Ranks> getSubRanks() {
        List<Ranks> ranks = new ArrayList<>();

        for (Ranks rank : values()) {
            if (index < rank.index) ranks.add(rank);
        }

        return ranks;
    }

    /**
     * Check if a rank have defined a permission
     * @param perm permission to check
     * @return if rank have permission
     */
    public boolean hasPerm(@NotNull String perm) {
        // Check for this rank
        for (Permission permission : permissions) {
            String permIn = permission.getPerm();
            if (permIn.equals(perm)) {
                return true;
            }
        }

        // Check for sub ranks
        for (Ranks subRank : getSubRanks()) {
            if (subRank.hasPerm(perm)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get state of a permission
     * @param perm permission to get
     * @return state of permission
     */
    @Nullable
    public PermissionState getPerm(@NotNull String perm) {
        // Get for this rank
        for (Permission permission : permissions) {
            String permIn = permission.getPerm();

            if (permIn.equals(perm)) {
                return permission.getState();
            }
        }

        // Get for sub ranks
        for (Ranks subRank : getSubRanks()) {
            PermissionState state = subRank.getPerm(perm);
            if (state != null) {
                return state;
            }
        }

        return null;
    }

    /**
     * This method apply rank permission to a player <br/>
     * If a permission wasn't defined, it will be set to false
     * @param player player to apply permission
     * @param rank rank of player
     */
    public static void applyPermissions(@NotNull Player player, @NotNull Ranks rank) {
        // Create an attachment
        PermissionAttachment attachment = player.addAttachment(Faylisia.getInstance());

        // Apply all perms
        for (String perm : Permission.allPerms) {
            if (rank.hasPerm(perm)) {
                PermissionState state = rank.getPerm(perm);
                attachment.setPermission(perm, PermissionState.getValue(state));
            } else {
                attachment.setPermission(perm, false);
            }
        }

        // Apply additional perms like world edit perms
        for (String perm : Permission.otherPerms) {
            if (rank.hasPerm(perm)) {
                PermissionState state = rank.getPerm(perm);
                attachment.setPermission(perm, PermissionState.getValue(state));
            } else {
                attachment.setPermission(perm, false);
            }
        }

        // Update commands in chat for player
        player.updateCommands();
    }
}

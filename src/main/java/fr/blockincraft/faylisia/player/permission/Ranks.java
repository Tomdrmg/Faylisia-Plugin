package fr.blockincraft.faylisia.player.permission;

import fr.blockincraft.faylisia.Faylisia;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public enum Ranks {
    /*
    &grad([Responsable\\_Builder]\\_%player_name% #23e809 #e89309)
    &grad([Responsable\\_Modérateur]\\_%player_name% #00f7b5 #0077f7)
    */
    PLAYER(11,
            "Joueur",
            "#6b6b6bJoueur",
            "#6b6b6b%player_name%",
            "#6b6b6b[Joueur] %player_name%",
            "#6b6b6b",
            "#6b6b6b",
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
                    .build(),
            new Ranks[0]
    ),
    COMMUNICATION(10,
            "Communication",
            "#ffd061Communication",
            "#ffd061%player_name%",
            "#ffd061[Communication] %player_name%",
            "#ffd061",
            "#ffd061",
            new PermissionBuilder()
                    .set("faylisia.chat_color", PermissionState.TRUE)
                    .set("faylisia.chat_hex_color", PermissionState.TRUE)
                    .build(),
            new Ranks[]{Ranks.PLAYER}
    ),
    RESP_COMMUNICATION(5,
            "Resp. Communication",
            "#ffb30cResp. Communication",
            "#ffb30c%player_name%",
            "#ffb30c[Resp. Communication] %player_name%",
            "#ffb30c",
            "#ffb30c",
            new PermissionBuilder()
                    .build(),
            new Ranks[]{Ranks.COMMUNICATION}
    ),
    MODERATOR(9,
            "Modérateur",
            "#7899faModérateur",
            "#7899fa%player_name%",
            "#7899fa[Modérateur] %player_name%",
            "#7899fa",
            "#7899fa",
            new PermissionBuilder()
                    .set("faylisia.chat_color", PermissionState.TRUE)
                    .set("faylisia.chat_hex_color", PermissionState.TRUE)
                    .set("faylisia.spawn.teleport_others", PermissionState.TRUE)
                    .build(),
            new Ranks[]{Ranks.PLAYER}
    ),
    RESP_MODERATOR(4,
            "Resp. Modérateur",
            "#3467ffResp. Modérateur",
            "#3467ff%player_name%",
            "#3467ff[Resp. Modérateur] %player_name%",
            "#3467ff",
            "#3467ff",
            new PermissionBuilder()
                    .build(),
            new Ranks[]{Ranks.MODERATOR}
    ),
    BUILDER(8,
            "Builder",
            "#94ff86Builder",
            "#94ff86%player_name%",
            "#94ff86[Builder] %player_name%",
            "#94ff86",
            "#94ff86",
            new PermissionBuilder()
                    .set("faylisia.chat_color", PermissionState.TRUE)
                    .set("faylisia.chat_hex_color", PermissionState.TRUE)
                    .set("worldedit.*", PermissionState.DEV)
                    .set("minecraft.command.gamemode", PermissionState.DEV)
                    .set("faylisia.command.break", PermissionState.DEV)
                    .set("faylisia.break", PermissionState.DEV)
                    .set("faylisia.command.fly", PermissionState.DEV)
                    .set("faylisia.fly", PermissionState.DEV)
                    .set("minecraft.command.teleport", PermissionState.DEV)
                    .set("faylisia.command.flyspeed", PermissionState.DEV)
                    .set("faylisia.flyspeed", PermissionState.DEV)
                    .set("fawe.voxelbrush", PermissionState.DEV)
                    .set("voxelsniper.*", PermissionState.DEV)
                    .set("gobrush.use", PermissionState.DEV)
                    .set("gopaint.use", PermissionState.DEV)
                    .set("schematicbrush.brush.use", PermissionState.DEV)
                    .set("schematicbrush.preset.save", PermissionState.DEV)
                    .set("schematicbrush.preset.info", PermissionState.DEV)
                    .set("builders.util.*", PermissionState.DEV)
                    .set("fawe.*", PermissionState.DEV)
                    .build(),
            new Ranks[]{Ranks.PLAYER}
    ),
    CHEF_BUILDER(7,
            "Chef Builder",
            "#39f500Chef Builder",
            "#39f500%player_name%",
            "#39f500[Chef Builder] %player_name%",
            "#39f500",
            "#39f500",
            new PermissionBuilder()
                    .build(),
            new Ranks[]{Ranks.BUILDER}
    ),
    RESP_BUILDER(3,
            "Resp. Builder",
            "#00aa0cResp. Builder",
            "#00aa0c%player_name%",
            "#00aa0c[Resp. Builder] %player_name%",
            "#00aa0c",
            "#00aa0c",
            new PermissionBuilder()
                    .build(),
            new Ranks[]{Ranks.CHEF_BUILDER}
    ),
    DEVELOPER(6,
            "Développeur",
            "#ff598fDéveloppeur",
            "#ff598f%player_name%",
            "#ff598f[Développeur] %player_name%",
            "#ff598f",
            "#ff598f",
            new PermissionBuilder()
                    .set("faylisia.chat_color", PermissionState.TRUE)
                    .set("faylisia.chat_hex_color", PermissionState.TRUE)
                    .build(),
            new Ranks[]{Ranks.PLAYER}
    ),
    RESP_DEVELOPER(2,
            "Resp. Développeur",
            "#ff2d76Resp. Développeur",
            "#ff2d76%player_name%",
            "#ff2d76[Resp. Développeur] %player_name%",
            "#ff2d76",
            "#ff2d76",
            new PermissionBuilder()
                    .build(),
            new Ranks[]{Ranks.DEVELOPER}
    ),
    ADMINISTRATOR(1,
            "Administrateur",
            "&grad(Administrateur #fc0356 #dbfc03)",
            "&grad(%player_name% #fc0356 #dbfc03)",
            "&grad([Administrateur]\\_%player_name% #fc0356 #dbfc03)",
            "#ff4b4b",
            "#ff4b4b",
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
                    .build(),
            new Ranks[]{Ranks.RESP_DEVELOPER, Ranks.RESP_BUILDER, Ranks.RESP_BUILDER, Ranks.RESP_MODERATOR}
    ),
    FOUNDER(0,
            "Fondateur",
            "&grad(Fondateur #ff3c00 #9000ff)",
            "&grad(%player_name% #ff3c00 #9000ff)",
            "&grad([Fondateur]\\_%player_name% #ff3c00 #9000ff)",
            "#b30002",
            "#b30002",
            new PermissionBuilder()
                    .set(".*", PermissionState.TRUE)
                    .set("minecraft.command.op", PermissionState.FALSE)
                    .set("bukkit.command.reload", PermissionState.FALSE)
                    .set("minecraft.command.msg", PermissionState.FALSE)
                    .build(),
            new Ranks[]{Ranks.ADMINISTRATOR}
    );

    public final int index;
    public final String name;
    public final String displayName;
    public final String playerName;
    public final String chatName;
    public final String mainColor;
    public final String accentColor;
    public final Permission[] permissions;
    public final Ranks[] subRanks;

    /**
     * @param index       index to order ranks in tab
     * @param name        name of the rank
     * @param displayName display name of the rank (with colors)
     * @param playerName  player name when have this rank (to apply colors)
     * @param chatName    chat name (player name + display name in other var to apply gradient on two)
     * @param mainColor   main color char to use {@link ChatColor}
     * @param accentColor accent color char to use {@link ChatColor}
     * @param permissions all permissions and their state to applied of player (default = false)
     */
    Ranks(int index, @NotNull String name, @NotNull String displayName, @NotNull String playerName, @NotNull String chatName, @NotNull String mainColor, @NotNull String accentColor, @NotNull Permission[] permissions, @NotNull Ranks[] subRanks) {
        this.index = index;
        this.name = name;
        this.displayName = displayName;
        this.playerName = playerName;
        this.chatName = chatName;
        this.mainColor = mainColor;
        this.accentColor = accentColor;
        this.permissions = permissions == null ? new Permission[0] : permissions;
        this.subRanks = subRanks;
    }

    /**
     * Check if a rank have defined a permission
     *
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
        for (Ranks subRank : subRanks) {
            if (subRank.hasPerm(perm)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get state of a permission
     *
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
        for (Ranks subRank : subRanks) {
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
     *
     * @param player player to apply permission
     * @param rank   rank of player
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
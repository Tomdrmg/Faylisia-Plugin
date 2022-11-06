package fr.blockincraft.faylisia.displays;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.blocks.CustomBlock;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.displays.animation.FlashingAnimation;
import fr.blockincraft.faylisia.displays.animation.LinearAnimation;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import fr.blockincraft.faylisia.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {
    private static final Map<UUID, AnimatedText> title = new HashMap<>();
    private static final Map<UUID, AnimatedText[]> body = new HashMap<>();
    private static final Registry registry = Faylisia.getInstance().getRegistry();

    /**
     * Create a new scoreboard for a player
     * @param player player
     */
    public void createScoreboard(@NotNull Player player) {
        if (player.getScoreboard().getObjective(player.getUniqueId().toString()) != null) {
            player.getScoreboard().getObjective(player.getUniqueId().toString()).unregister();
        } else {
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(sb);
        }


        Objective objective = player.getScoreboard().registerNewObjective(player.getUniqueId().toString(), "dummy", "Title", RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Update scoreboard for a player
     * @param player player to update her scoreboard
     */
    public void updateScoreboard(@NotNull Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(player.getUniqueId().toString());

        AnimatedText title = ScoreboardManager.title.get(player.getUniqueId());
        AnimatedText[] body = ScoreboardManager.body.get(player.getUniqueId());

        if (title == null) {
            title = new LinearAnimation('f', 5, LinearAnimation.StartPosition.CENTER)
                    .addElement('d', "<< ", true)
                    .addElement('b', "Faylisia", true)
                    .addElement('d', " >>", true)
                    .setPrefix(" ")
                    .setSuffix(" ")
                    .build();

            ScoreboardManager.title.put(player.getUniqueId(), title);
        }
        if (body == null) {
            body = new AnimatedText[]{
                    new AnimatedText(""),
                    new AnimatedText("&6&lProfile:"),
                    new AnimatedText("&e Nom: %used_name%"),
                    new AnimatedText("&e Grade: %rank_name%"),
                    new AnimatedText("&e Class: %class_name%"),
                    new AnimatedText("&e Argent: %money%$"),
                    new AnimatedText(""),
                    new AnimatedText("&8&lInformations:"),
                    new AnimatedText("&7 Lieu: %region_name%"),
                    new AnimatedText(""),
                    new LinearAnimation('f', 5, LinearAnimation.StartPosition.SIDE)
                            .addElement('d', "faylisia.fr")
                            .build()
            };

            ScoreboardManager.body.put(player.getUniqueId(), body);
        }

        assert objective != null;
        objective.setDisplayName(ColorsUtils.translateAll(title.get()));

        CustomPlayerDTO customPlayer = registry.getOrRegisterPlayer(player.getUniqueId());

        for (int i = 0; i < body.length; i++) {
            String iStr = String.valueOf(i);
            StringBuilder sb = new StringBuilder();

            for (int l = 0; l < iStr.length(); l++) {
                sb.append(ChatColor.COLOR_CHAR).append(iStr.charAt(l));
            }

            String teamName = sb.toString();

            Team lineTeam = scoreboard.getTeam(teamName);
            if (lineTeam == null) {
                lineTeam = scoreboard.registerNewTeam(teamName);
                lineTeam.addEntry(teamName);
            }

            lineTeam.setPrefix(ColorsUtils.translateAll(body[i].get()
                    .replace("%region_name%", registry.getRegionAt(player.getLocation()).getName())
                    .replace("%money%", TextUtils.valueWithCommas(customPlayer.getMoney()))
                    .replace("%chat_name%", customPlayer.getRank().chatName.replace("%player_name%", customPlayer.getNameToUse()))
                    .replace("%rank_name%", customPlayer.getRank().displayName)
                    .replace("%class_name%", customPlayer.getClasses().name)
                    .replace("%used_name%", customPlayer.getRank().playerName.replace("%player_name%", customPlayer.getNameToUse()))
            ));

            Score score = objective.getScore(teamName);
            score.setScore(body.length - 1 - i);
        }
    }

    /**
     * Check if a player already have a customizable scoreboard
     * @param player player to check
     * @return if he has
     */
    public boolean hasScoreboard(@NotNull Player player) {
        Objective objective = player.getScoreboard().getObjective(player.getUniqueId().toString());
        return objective != null && objective.isModifiable();
    }
}

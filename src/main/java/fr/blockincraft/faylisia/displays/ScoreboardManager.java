package fr.blockincraft.faylisia.displays;

import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.map.Region;
import fr.blockincraft.faylisia.displays.animation.FlashingAnimation;
import fr.blockincraft.faylisia.displays.animation.LinearAnimation;
import fr.blockincraft.faylisia.utils.ColorsUtils;
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
                    new FlashingAnimation('f', 2, 2, 40)
                            .addElement('7', String.valueOf(Region.regionChar))
                            .setSuffix(" %region_name%")
                            .build(),
                    new AnimatedText(""),
                    new LinearAnimation('f', 5, LinearAnimation.StartPosition.SIDE)
                            .addElement('d', "faylis.xyz")
                            .build()
            };

            ScoreboardManager.body.put(player.getUniqueId(), body);
        }

        assert objective != null;
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title.get()));

        for (int i = 0; i < body.length; i++) {
            String teamName = ChatColor.COLOR_CHAR + String.valueOf(i);

            Team lineTeam = scoreboard.getTeam(teamName);
            if (lineTeam == null) {
                lineTeam = scoreboard.registerNewTeam(teamName);
                lineTeam.addEntry(teamName);
            }

            lineTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', body[i].get()
                    .replace("%region_name%", ColorsUtils.translateAll(registry.getRegionAt(player.getLocation()).getName()))
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

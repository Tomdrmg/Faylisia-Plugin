package fr.blockincraft.faylisia.items;

import fr.blockincraft.faylisia.player.Stats;

import java.util.Map;

public interface StatsItem {
    boolean validStats(boolean inMainHand, boolean inArmorSlot);

    double getStat(Stats stat);

    boolean hasStat(Stats stat);

    Map<Stats, Double> getStats();
}

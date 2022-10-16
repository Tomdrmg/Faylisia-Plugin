package fr.blockincraft.faylisia.magic.wrapper;

import fr.blockincraft.faylisia.magic.Spell;
import fr.blockincraft.faylisia.magic.SpellTypes;
import fr.blockincraft.faylisia.utils.Spectrum;
import org.bukkit.entity.Player;

public class SpiralExplosionSpellWrapper extends Spell {
    public SpiralExplosionSpellWrapper(Player player, int radius, Spectrum spectrum) {
        super(SpellTypes.spiralExplosion, new SpellParam[]{
            new SpellParam<Integer>(Integer.class, () -> radius, radius),
            new SpellParam<Spectrum>(Spectrum.class, () -> spectrum, spectrum)
        }, player);
    }

    public SpiralExplosionSpellWrapper(Player player, int radius, int colorStart, int colorEnd) {
        this(player, radius, new Spectrum(0, colorStart, colorEnd));
    }

    public SpiralExplosionSpellWrapper(Player player, int radius, int color) {
        this(player, radius, new Spectrum(0, color, color));
    }

    public SpiralExplosionSpellWrapper(Player player, int radius) {
        this(player, radius, new Spectrum(0, 0x000000, 0xFFFFFF));
    }
}

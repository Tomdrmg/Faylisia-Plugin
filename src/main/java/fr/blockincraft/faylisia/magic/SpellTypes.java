package fr.blockincraft.faylisia.magic;

import fr.blockincraft.faylisia.utils.Spectrum;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class SpellTypes {
    public static final SpellType spiralExplosion = new SpellType("spiral_explosion")
            .setCooldown(10)
            .setTickDuration(200)
            .setAction((player, x, y, z, world, params) -> {
                world.createExplosion(x, y, z, 20.0f, false, false);
            })
            .setFrames((tick, x, y, z, world, player, params) -> {
                int radius = -1;
                Spectrum spectrum = null;

                try {
                    radius = (int) params.get(Integer.class).get(0);
                    spectrum = (Spectrum) params.get(Spectrum.class).get(0);
                } catch (Exception e) {
                    return true;
                }

                spectrum = new Spectrum(200, spectrum.getColorStart(), spectrum.getColorEnd());

                double finalRadius = radius - radius / 200.0 * tick;
                double displayY = y + finalRadius;
                double distX = finalRadius * Math.cos(Math.PI / 200 * tick);
                double distZ = finalRadius * Math.sin(Math.PI / 200 * tick);

                player.spawnParticle(Particle.REDSTONE, new Location(world, x + distX, displayY, z + distZ), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(spectrum.colorAt((int) tick)), 2F));
                player.spawnParticle(Particle.REDSTONE, new Location(world, x - distX, displayY, z - distZ), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(spectrum.colorAt((int) tick)), 2F));
                player.spawnParticle(Particle.REDSTONE, new Location(world, x + distZ, displayY, z - distX), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(spectrum.colorAt((int) tick)), 2F));
                player.spawnParticle(Particle.REDSTONE, new Location(world, x - distZ, displayY, z + distX), 0, 0, 0, 0, 1, new Particle.DustOptions(Color.fromRGB(spectrum.colorAt((int) tick)), 2F));

                return false;
            });
}
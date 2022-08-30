package fr.blockincraft.faylisia.core.entity;

import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.Classes;
import fr.blockincraft.faylisia.player.permission.Ranks;
import jakarta.persistence.*;

import java.util.UUID;

@NamedQuery(query = "SELECT c FROM CustomPlayer c", name = "getAllPlayers")
@Entity
public class CustomPlayer {
    @Id
    private UUID player;

    @Enumerated(value = EnumType.STRING)
    private Classes classes;
    @Enumerated(value = EnumType.STRING)
    private Ranks rank;
    @Column(name = "can_break")
    private boolean canBreak;
    private String name;

    public CustomPlayer() {

    }

    public CustomPlayer(CustomPlayerDTO dto) {
        this.player = dto.getPlayer();
        this.classes = dto.getClasses();
        this.rank = dto.getRank();
        this.canBreak = dto.getCanBreak();
        this.name = dto.getName();
    }

    public UUID getPlayer() {
        return player;
    }

    public Classes getClasses() {
        return classes;
    }

    public Ranks getRank() {
        return rank;
    }

    public boolean getCanBreak() {
        return canBreak;
    }

    public String getName() {
        return name;
    }
}

package fr.blockincraft.faylisia.core.entity;

import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.player.Classes;
import fr.blockincraft.faylisia.player.permission.Ranks;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@NamedQuery(query = "SELECT c FROM CustomPlayer c", name = "getAllPlayers")
@Entity
public class CustomPlayer {
    @Id
    private UUID player;

    @Column(nullable = false)
    @ColumnDefault(value = "EXPLORER")
    @Enumerated(value = EnumType.STRING)
    private Classes classes;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault(value = "PLAYER")
    private Ranks rank;
    @Column(name = "can_break", nullable = false)
    @ColumnDefault(value = "0")
    private boolean canBreak;
    @Column(name = "last_name", nullable = false)
    @ColumnDefault(value = "")
    private String lastName;
    @Column(name = "custom_name_enabled", nullable = false)
    @ColumnDefault(value = "0")
    private boolean customNameEnabled;
    @Column(name = "custom_name", nullable = false)
    @ColumnDefault(value = "")
    private String customName;
    @Column(name = "last_inventory_as_json", nullable = false)
    @ColumnDefault(value = "{\"owner\":\"\",\"content\":[{\"slot\":0,\"item\":null},{\"slot\":1,\"item\":null},{\"slot\":2,\"item\":null},{\"slot\":3,\"item\":null},{\"slot\":4,\"item\":null},{\"slot\":5,\"item\":null},{\"slot\":6,\"item\":null},{\"slot\":7,\"item\":null},{\"slot\":8,\"item\":null},{\"slot\":9,\"item\":null},{\"slot\":10,\"item\":null},{\"slot\":11,\"item\":null},{\"slot\":12,\"item\":null},{\"slot\":13,\"item\":null},{\"slot\":14,\"item\":null},{\"slot\":15,\"item\":null},{\"slot\":16,\"item\":null},{\"slot\":17,\"item\":null},{\"slot\":18,\"item\":null},{\"slot\":19,\"item\":null},{\"slot\":20,\"item\":null},{\"slot\":21,\"item\":null},{\"slot\":22,\"item\":null},{\"slot\":23,\"item\":null},{\"slot\":24,\"item\":null},{\"slot\":25,\"item\":null},{\"slot\":26,\"item\":null},{\"slot\":27,\"item\":null},{\"slot\":28,\"item\":null},{\"slot\":29,\"item\":null},{\"slot\":30,\"item\":null},{\"slot\":31,\"item\":null},{\"slot\":32,\"item\":null},{\"slot\":33,\"item\":null},{\"slot\":34,\"item\":null},{\"slot\":35,\"item\":null},{\"slot\":36,\"item\":null},{\"slot\":37,\"item\":null},{\"slot\":38,\"item\":null},{\"slot\":39,\"item\":null},{\"slot\":40,\"item\":null}]}")
    private String lastInventoryAsJson;
    @Column(name = "discord_user_id")
    private Long discordUserId;
    @Column(name = "last_update", nullable = false)
    @ColumnDefault(value = "0")
    private long lastUpdate;
    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private long money;
    @Column(name = "send_messages_to_discord", nullable = false)
    @ColumnDefault(value = "1")
    private boolean sendMessagesToDiscord;

    public CustomPlayer() {

    }

    public CustomPlayer(CustomPlayerDTO dto) {
        this.player = dto.getPlayer();
        this.classes = dto.getClasses();
        this.rank = dto.getRank();
        this.canBreak = dto.getCanBreak();
        this.lastName = dto.getLastName();
        this.customNameEnabled = dto.isCustomNameEnabled();
        this.customName = dto.getCustomName();
        this.lastInventoryAsJson = dto.getLastInventoryAsJson();
        this.discordUserId = dto.getDiscordUserId();
        this.lastUpdate = dto.getLastUpdate();
        this.money = dto.getMoney();
        this.sendMessagesToDiscord = dto.isSendMessagesToDiscord();
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

    public boolean isCanBreak() {
        return canBreak;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isCustomNameEnabled() {
        return customNameEnabled;
    }

    public String getCustomName() {
        return customName;
    }

    public String getLastInventoryAsJson() {
        return lastInventoryAsJson;
    }

    public Long getDiscordUserId() {
        return discordUserId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getMoney() {
        return money;
    }

    public boolean isSendMessagesToDiscord() {
        return sendMessagesToDiscord;
    }
}

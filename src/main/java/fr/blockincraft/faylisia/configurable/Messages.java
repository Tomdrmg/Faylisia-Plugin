package fr.blockincraft.faylisia.configurable;

import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public enum Messages {
    PREFIX("prefix", "&d[Faylisia] &8>>"),
    BAR("bar", "&8&l&m---------------------------------------------"),

    // No join messages
    NO_JOIN_IN_DEV("no_join_in_dev", "&dLe serveur est actuellement en développement, rejoin notre discord pour plus d'informations: &cdiscord.faylis.xyz"),
    NO_JOIN_IN_MAINTENANCE("no_join_in_maintenance", "&dLe serveur est actuellement en maintenance, rejoin notre discord pour plus d'informations: &cdiscord.faylis.xyz"),
    NO_JOIN_DURING_STARTING("no_join_during_starting", "&cLe serveur est en train de démarrer, veuillez réessayer dans quelques instants"),
    KICK_ON_DISABLE("kick_on_disable", "&cLe serveur redémarre, veuillez attendre quelques instants avant de vous reconnecter"),

    //Info messages
    PLAYER_JOIN_MESSAGE("player_join_message", "&d%player_name% &ba rejoint le serveur"),
    PLAYER_LEAVE_MESSAGE("player_leave_message", "&d%player_name% &ba quitté le serveur"),

    //Color chat messages
    AT_LEAST_TWO_COLOR_FOR_GRADIENT("at_least_two_color_for_gradient", "%prefix% &cIl faut au moins deux couleur pour faire un dégradé!"),
    INVALID_COLOR("invalid_color", "%prefix% &cCouleur invalide '%color%'"),

    //Command base messages
    HELP_MESSAGE("help_message", "%prefix% &bToutes les informations sur les commandes sont disponible sur le wiki: http://faylis.xyz/wiki/commands"),
    WIKI_MESSAGE("wiki_message", "%prefix% &dLien vers le wiki: http://faylis.xyz/wiki"),
    NO_PERMISSION_MESSAGE("no_permission_message", "%prefix% &cVous n'avez pas la permission de faire ceci!"),
    UNKNOWN_RANK_MESSAGE("unknown_rank_message", "%prefix% &cAucun grade n'existe avec le nom '%rank%'!"),
    UNKNOWN_PLAYER_MESSAGE("unknown_player_message", "%prefix% &cAucun joueur trouvé avec le nom '%target_name%'!"),
    INVALID_ITEM_MESSAGE("invalid_item_message", "%prefix% &cAucun objet existe avec l'identifiant '%item_id%'!"),
    INVALID_NUMBER_MESSAGE("invalid_number_message", "%prefix% &cNombre invalide: '%number%'!"),

    //Command break messages
    BREAK_ENABLED("break_enabled", "%prefix% &aVous pouvez maintenant casser/placer des blocks."),
    BREAK_DISABLED("break_disabled", "%prefix% &cVous ne pouvez maintenant plus casser/placer des blocks."),

    //Command ranks messages
    RANK_OF("rank_of", "%prefix% &bLe grade de &d%player_name%&b est &d%rank%&b."),
    CANNOT_SET_RANK_OF_EQUAL_OR_SUPERIOR_PLAYER("cannot_set_a_equal_or_superior_player", "%prefix% &cVous ne pouvez pas modifier le grade d'un joueur possédant un grade supérieur ou égal au votre!"),
    CANNOT_SET_A_EQUAL_OR_SUPERIOR_RANK("cannot_set_a_equal_or_superior_rank", "%prefix% &cVous ne pouvez pas donner un grade supérieur ou égal au votre!"),
    CANNOT_SET_YOUR_RANK("cannot_set_your_rank", "%prefix% &cVous ne pouvez pas modifié votre grade!"),
    BEEN_SET_PLAYER_RANK_TO("been_set_player_rank_to", "%prefix% &aVous avez bien donner le grade %rank% à &d%player_name%&a."),
    PLAYER_SET_YOUR_RANK_TO("player_set_your_rank_to", "%prefix% &d%player_name%&a vous a donner le grade %rank%."),

    //Command spawn messages
    TELEPORTED_TO_SPAWN("teleported_to_spawn", "%prefix% &aVous avez était téléporté au spawn."),
    TELEPORTED_TO_SPAWN_BY("teleported_to_spawn_by", "%prefix% &aVous avez était téléporté au spawn par &d%player_name%&a."),
    TELEPORTED_OTHER_TO_SPAWN("teleported_other_to_spawn", "%prefix% &aVous avez téléporté &d%player_name%&a au spawn."),
    TELEPORTED_ALL_TO_SPAWN("teleported_all_to_spawn", "%prefix% &aVous avez téléporté tous les joueurs en ligne au spawn."),

    //Command class messages (in this case class menu)
    YOU_SELECTED_A_CLASS("seleceted_a_class", "%prefix% &aVous êtes maintenant &d%class%&a."),

    //Command items messages
    GIVE_SELF_AN_ITEM("give_self_an_item", "%prefix% &aVous vous êtes donné un '%item%&a'."),
    GIVE_SELF_MULTIPLE_ITEMS("give_self_multiple_items", "%prefix% &aVous vous êtes donné %amount% '%item%&a'."),
    GIVE_OTHER_AN_ITEM("give_other_an_item", "%prefix% &aVous avez donné un '%item%&a' à &d%player_name%&a."),
    GIVE_OTHER_MULTIPLE_ITEMS("give_other_multiple_items", "%prefix% &aVous avez donné %amount% '%item%&a' à &d%player_name%&a."),
    GIVE_ALL_AN_ITEM("give_all_an_item", "%prefix% &aVous avez donné un '%item%&a' à tous les joueurs en ligne."),
    GIVE_ALL_MULTIPLE_ITEMS("give_all_multiple_item", "%prefix% &aVous avez donné %amount% '%item%&a' à tous les joueurs en ligne."),
    RECEIVE_FROM_AN_ITEM("receive_from_an_item", "%prefix% &d%player_name%&a vous a donné un '%item%&a'."),
    RECEIVE_FROM_MULTIPLE_ITEMS("receive_from_multiple_items", "%prefix% &d%player_name%&a vous a donné %amount% '%item%&a'."),

    //Discord messages
    MESSAGE_WAS_BEEN_SEND("message_was_been_send", "%prefix% &aLe message à bien était envoyé dans le salon &d#%channel% &a(<%channel_id%>)."),
    ERROR_WHEN_SENDING_MESSAGE("error_when_sending_message", "%prefix% &cLe message n'a pas pu être envoyé."),
    GUILD_NOT_FOUND("guild_not_found", "%prefix% &cLe serveur discord n'a pas était trouvé!"),
    CHANNEL_NOT_FOUND("channel_not_found", "%prefix% &cLe salon discord n'a pas était trouvé!"),
    ACCOUNT_UNLINKED("account_unlinked", "%prefix% &aTon compte a bien était délié du compte discord &d%account_name%#%account_tag%&a."),

    //Link command messages
    INVALID_TOKEN("invalid_token", "%prefix% &cLe token est invalide!"),
    ALREADY_LINK("already_link", "%prefix% &cVous êtes déja lié a un compte (%account_name%#%account_tag%)! Si vous pensez qu'il s'avère être une erreur, veuillez ouvrir un ticket sur le serveur discord: &ddiscord.faylis.xyz&c."),
    USER_HAS_LEAVE_THE_SERVER("player_has_leave_the_server", "%prefix% &cL'utilisateur a quitté le serveur discord!"),
    SUCCESS_LINK("success_link", "%prefix% &aVous avez bien lié votre compte à '&d%account_name%#%account_tag%&a'."),

    //State messages
    YOU_ARE_DIED("you_are_died", "%prefix% &cVous êtes mort!");

    private static FileConfiguration config = null;

    public static void setConfig(FileConfiguration config) {
        Messages.config = config;
    }

    private final String path;
    private final String defaultValue;

    Messages(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String get() {
        return ColorsUtils.translateAll((config == null ? defaultValue : config.getString(path, defaultValue))
                .replace("%prefix%", this == PREFIX || this == BAR ? "" : PREFIX.get())
                .replace("%bar%", this == PREFIX || this == BAR ? "" : BAR.get())
        );
    }

    public String get(Map<String, String> parameters) {

        String lang = config == null ? defaultValue : config.getString(path, defaultValue);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            lang = lang.replace(entry.getKey(), entry.getValue());
        }

        return ColorsUtils.translateAll(lang
                .replace("%prefix%", this == PREFIX || this == BAR ? "" : PREFIX.get())
                .replace("%bar%", this == PREFIX || this == BAR ? "" : BAR.get())
        );
    }
}

package fr.blockincraft.faylisia.configurable;

import fr.blockincraft.faylisia.utils.ColorsUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public enum Messages {
    PREFIX("prefix", "&d[Faylisia] &8>>"),
    BAR("bar", "&8&l&m---------------------------------------------"),

    // No join messages
    NO_JOIN_IN_DEV("no_join_in_dev", "&dLe serveur est actuellement en développement, rejoin notre discord pour plus d'informations: &cdiscord.faylisia.fr"),
    NO_JOIN_IN_MAINTENANCE("no_join_in_maintenance", "&dLe serveur est actuellement en maintenance, rejoin notre discord pour plus d'informations: &cdiscord.faylisia.fr"),
    NO_JOIN_DURING_STARTING("no_join_during_starting", "&cLe serveur est en train de démarrer, veuillez réessayer dans quelques instants"),
    KICK_ON_DISABLE("kick_on_disable", "&cLe serveur redémarre, veuillez attendre quelques instants avant de vous reconnecter"),

    //Info messages
    PLAYER_JOIN_MESSAGE("player_join_message", "&d%player_name% &ba rejoint le serveur"),
    PLAYER_LEAVE_MESSAGE("player_leave_message", "&d%player_name% &ba quitté le serveur"),

    //Color chat messages
    AT_LEAST_TWO_COLOR_FOR_GRADIENT("at_least_two_color_for_gradient", "%prefix% &cIl faut au moins deux couleur pour faire un dégradé!"),
    INVALID_COLOR("invalid_color", "%prefix% &cCouleur invalide '%color%'"),

    //Command base messages
    HELP_MESSAGE("help_message", "%prefix% &bToutes les informations sur les commandes sont disponible sur le wiki: http://faylisia.fr/wiki/commands"),
    WIKI_MESSAGE("wiki_message", "%prefix% &dLien vers le wiki: http://faylisia.fr/wiki"),
    NO_PERMISSION_MESSAGE("no_permission_message", "%prefix% &cVous n'avez pas la permission de faire ceci!"),
    ONLY_PLAYERS_COMMAND_MESSAGE("only_players_command_message", "%prefix% &cSeul les joueurs peuvent exécuté cette commande!"),

    //Command parser messages
    UNKNOWN_RANK_MESSAGE("unknown_rank_message", "%prefix% &cAucun grade n'existe avec le nom '%rank%'!"),
    UNKNOWN_ONLINE_PLAYER_MESSAGE("unknown_online_player_message", "%prefix% &cAucun joueur en ligne trouvé avec le nom '%target_name%'!"),
    UNKNOWN_PLAYER_MESSAGE("unknown_player_message", "%prefix% &cAucun joueur trouvé avec le nom '%target_name%'!"),
    UNKNOWN_MATERIAL_MESSAGE("unknown_material_message", "%prefix% &cAucun matériaux trouvé avec le nom '%material_name%'!"),
    NON_BLOCK_MATERIAL_MESSAGE("non_block_material_message", "%prefix% &cLe matériaux '%material_name%' n'est pas un block!"),
    INVALID_ITEM_MESSAGE("invalid_item_message", "%prefix% &cAucun objet existe avec l'identifiant '%item_id%'!"),
    INVALID_NUMBER_MESSAGE("invalid_number_message", "%prefix% &cNombre invalide: '%number%'!"),
    INVALID_BOOLEAN_MESSAGE("invalid_boolean_message", "%prefix% &cBooléen invalide: '%boolean%'!"),
    INVALID_STATE_MESSAGE("invalid_state_message", "%prefix% &cEtat invalide: '%state%'!"),
    INVALID_NAME_LENGTH("invalid_name_length", "%prefix% &cLe nom doit contenir entre 3 et 16 caractères inclus!"),
    INVALID_NAME_CONTENT("invalid_name_content", "%prefix% &cLe nom doit contenir uniquement des lettres majuscule et minuscule, des chiffres, des tiret du six et des tiret du huit."),
    INVALID_BLOCK_TYPE("invalid_block_type", "%prefix% &cAucun block type n'a comme id \"%id%\""),
    INVALID_FLY_SPEED_MESSAGE("invalid_fly_speed_message", "%prefix% &cVitesse invalide, elle doit être un nombre compris entre 1 et 10 inclus!"),

    //Command fly speed messages
    NEW_FLY_PEED_SET("new_fly_speed_set", "%prefix% &aVotre vitesse est maintenant de %speed%%."),

    //Command msg messages
    MSG_FROM_MESSAGE("msg_from_message", "&8De &d%sender_name% &8>> &d%message%"),
    MSG_TO_MESSAGE("msg_to_message", "&8À &d%target_name% &8>> &d%message%"),
    MSG_SPY_FROM_TO_MESSAGE("msg_spy_from_to_message", "&dChatSpy &8- De &d%sender_name% &8à &d%target_name% &8>> &d%message%"),
    CHAT_SPY_ENABLE_MESSAGE("chat_spy_enable_message", "%prefix% &bVous avez activé le chat spy."),
    CHAT_SPY_DISABLE_MESSAGE("chat_spy_disable_message", "%prefix% &bVous avez désactivé le chat spy."),

    //Command cblocks messages
    CBLOCKS_GENERATE_MESSAGE("cblocks_generate_message", "%prefix% &a%amount% blocs custom on était générés."),
    CBLOCKS_REMOVE_MESSAGE("cblocks_remove_message", "%prefix% &a%amount% blocs custom on était supprimés."),

    //Command nick messages
    NICK_ENABLED("nick_enabled", "%prefix% &aVotre surnom est maintenant activé."),
    NICK_DISABLED("nick_disabled", "%prefix% &aVotre surnom est maintenant désactivé."),
    ENABLED_NICK_STATE_MESSAGE("nick_state_message", "%prefix% &bLe joueur %player_name% est aussi connu en tant que %nickname%, son surnom est actif."),
    DISABLED_NICK_STATE_MESSAGE("nick_state_message", "%prefix% &bLe joueur %player_name% est aussi connu en tant que %nickname%, son surnom est inactif."),
    NICKNAME_CHANGED_MESSAGE("nickname_changed_message", "%prefix% &aVotre nouveau surnom est '%nickname%'."),

    //Command fly messages
    FLY_ENABLED("fly_enabled", "%prefix% &aVous pouvez maintenant voler."),
    FLY_DISABLED("fly_disabled", "%prefix% &cVous ne pouvez maintenant plus voler"),

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
    ALREADY_LINK("already_link", "%prefix% &cVous êtes déja lié a un compte (%account_name%#%account_tag%)! Si vous pensez qu'il s'avère être une erreur, veuillez ouvrir un ticket sur le serveur discord: &ddiscord.faylisia.fr&c."),
    USER_HAS_LEAVE_THE_SERVER("player_has_leave_the_server", "%prefix% &cL'utilisateur a quitté le serveur discord!"),
    SUCCESS_LINK("success_link", "%prefix% &aVous avez bien lié votre compte à '&d%account_name%#%account_tag%&a'."),

    //State messages
    YOU_ARE_DIED("you_are_died", "%prefix% &cVous êtes mort!"),

    //Breaking block messages
    CANT_BREAK_BLOCK_BAD_TOOL_TYPE("cant_break_block_bad_tool_type", "%prefix% &cVous ne pouvez pas casser ce bloc, il vous faut un outil &b%type%&c!"),
    CANT_BREAK_BLOCK_INSUFFISANT_BREAK_LEVEL("cant_break_block_insuffisant_break_level", "%prefix% &cVous ne pouvez pas casser ce bloc, il vous faut un outil avec un niveau de forage supérieur ou égale à &b%level%&c!");

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

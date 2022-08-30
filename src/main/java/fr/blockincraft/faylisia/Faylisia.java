package fr.blockincraft.faylisia;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import fr.blockincraft.faylisia.api.RequestHandler;
import fr.blockincraft.faylisia.commands.*;
import fr.blockincraft.faylisia.configurable.Messages;
import fr.blockincraft.faylisia.configurable.Provider;
import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.core.entity.CustomPlayer;
import fr.blockincraft.faylisia.entity.CustomEntity;
import fr.blockincraft.faylisia.entity.Entities;
import fr.blockincraft.faylisia.items.Items;
import fr.blockincraft.faylisia.listeners.ChatListeners;
import fr.blockincraft.faylisia.listeners.GameListeners;
import fr.blockincraft.faylisia.listeners.MenuListener;
import fr.blockincraft.faylisia.map.Regions;
import fr.blockincraft.faylisia.menu.ChestMenu;
import fr.blockincraft.faylisia.player.Classes;
import fr.blockincraft.faylisia.displays.ScoreboardManager;
import fr.blockincraft.faylisia.task.*;
import fr.blockincraft.faylisia.utils.ColorsUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.server.Server;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class Faylisia extends JavaPlugin {
    public static final boolean development = true;

    private static Faylisia instance;
    private static boolean initialized = false;

    private SessionFactory sessionFactory;
    private Server apiServer;
    private JDA discordBot;

    private Registry registry;
    private ScoreboardManager scoreBoardManager;
    private ProtocolManager protocolManager;

    public static Faylisia getInstance() {
        return instance;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public JDA getDiscordBot() {
        return discordBot;
    }

    public Registry getRegistry() {
        return registry;
    }

    public ScoreboardManager getScoreBoardManager() {
        return scoreBoardManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                List<PlayerInfoData> players = event.getPacket().getPlayerInfoDataLists().read(0);

                if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.UPDATE_LATENCY) {
                    for (PlayerInfoData player : players) {
                        if (player.getProfile().getName().length() > 3) {
                            CustomPlayerDTO custom = registry.getOrRegisterPlayer(player.getProfile().getUUID());
                            Classes classes = custom.getClasses();

                            WrappedGameProfile gameProfile = new WrappedGameProfile(player.getProfile().getUUID(), custom.getName());
                            gameProfile.getProperties().put("textures", WrappedSignedProperty.fromValues("textures", classes.skin.value, classes.skin.signature));

                            PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, player.getLatency(), player.getGameMode(), WrappedChatComponent.fromText(custom.getName()));

                            players.remove(player);
                            players.add(playerInfoData);
                        }
                    }
                }

                event.getPacket().getPlayerInfoDataLists().write(0, players);
            }
        });
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Status.Server.SERVER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrappedServerPing serverPing = event.getPacket().getServerPings().read(0);
                List<WrappedGameProfile> profiles = new ArrayList<>();

                int players = Bukkit.getOnlinePlayers().size();
                int maxPlayers = Bukkit.getMaxPlayers();

                int bars = 44 - (int) ((String.valueOf(players).length() + 2 + String.valueOf(maxPlayers).length()) * (2.0 + 1 / 3));
                int per = (int) Math.floor(((double) bars) / maxPlayers * players);

                if (players > 0 && per == 0) per = 1;

                StringBuilder sb = new StringBuilder("&a&l" + "|".repeat(bars));
                sb.insert(per + 4, "&8&l");

                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("&8&l&m-------------------")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("&d       Faylisia Engine")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("&d    Capacité du Serveur")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll(sb + " &d" + players + "&8/&d" + maxPlayers)));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("&dSite: faylis.xyz")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("&dDiscord: discord.faylis.xyz")));
                profiles.add(new WrappedGameProfile(UUID.randomUUID(), ColorsUtils.translateAll("&8&l&m-------------------")));

                serverPing.setPlayers(profiles);

                serverPing.setMotD(WrappedChatComponent.fromLegacyText(ColorsUtils.translateAll(Bukkit.getMotd())));
                event.getPacket().getServerPings().write(0, serverPing);
            }
        });

        //Set instance
        instance = this;

        //Try to create Hibernate session
        if (!initHibernateSession()) {
            this.getLogger().log(Level.SEVERE, "Cannot create an Hibernate Session factory, stopping.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Create of registry
        registry = new Registry();
        registry.init();
    }

    @Override
    public void onEnable() {
        //Init api web server
        initServer();
        //Try to start api web server
        if (!startServer()) {
            this.getLogger().log(Level.SEVERE, "Cannot start API server, stopping.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!initDiscordBot()) {
            this.getLogger().log(Level.SEVERE, "Cannot start Discord Bot server, stopping.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Initialize gamerules in all worlds
        initGameRules();
        //Remove all vanilla recipes
        Bukkit.clearRecipes();
        //Initialization of scoreboard manager
        scoreBoardManager = new ScoreboardManager();
        //To register all items, regions... we need to call the class
        new Items();
        new Regions();
        new Entities();

        //Register listeners
        Bukkit.getPluginManager().registerEvents(new GameListeners(), instance);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), instance);
        Bukkit.getPluginManager().registerEvents(new ChatListeners(), instance);

        //Bind command completer/executors
        PluginCommand itemCommand = Bukkit.getPluginCommand("items");
        if (itemCommand != null) {
            itemCommand.setExecutor(new ItemsExecutor());
            itemCommand.setTabCompleter(new ItemsCompleter());
        }
        PluginCommand spawnCommand = Bukkit.getPluginCommand("spawn");
        if (spawnCommand != null) {
            spawnCommand.setExecutor(new SpawnExecutor());
            spawnCommand.setTabCompleter(new SpawnCompleter());
        }
        PluginCommand breakCommand = Bukkit.getPluginCommand("break");
        if (breakCommand != null) {
            breakCommand.setExecutor(new BreakExecutor());
        }
        PluginCommand classCommand = Bukkit.getPluginCommand("class");
        if (classCommand != null) {
            classCommand.setExecutor(new ClassExecutor());
        }
        PluginCommand ranksCommand = Bukkit.getPluginCommand("ranks");
        if (ranksCommand != null) {
            ranksCommand.setExecutor(new RanksExecutor());
            ranksCommand.setTabCompleter(new RanksCompleter());
        }
        PluginCommand discordCommand = Bukkit.getPluginCommand("discord");
        if (discordCommand != null) {
            discordCommand.setExecutor(new DiscordExecutor());
            discordCommand.setTabCompleter(new DiscordCompleter());
        }

        //Start tasks
        ScoreboardRefreshTask.startTask();
        StatsRegenTask.startTask();
        ActionBarTask.startTask();
        TabHeaderFooterTask.startTask();
        EntityQuitRegionTask.startTask();

        initialized = true;
    }

    @Override
    public void onDisable() {
        //Close all inventories to prevent dupe
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChestMenu menu = MenuListener.menus.get(player.getUniqueId());
            if (menu != null) {
                menu.getMenuCloseHandler().onClose(player);
            }
            player.closeInventory();

            player.kickPlayer(Messages.KICK_ON_DISABLE.get());
        }

        //Cancel tasks because it can do error when instance = null and session closed
        Bukkit.getScheduler().cancelTasks(instance);

        //Close the session factory to prevent database bad request
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        sessionFactory = null;

        //Stop api web server
        try {
            apiServer.stop();
        } catch (Exception ignored) {

        }

        //Remove all custom entities
        for (CustomEntity entity : registry.getEntities()) {
            entity.remove();
        }

        //unregister instance and vars
        registry = null;
        instance = null;
    }

    public boolean initHibernateSession() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            configuration.addAnnotatedClass(CustomPlayer.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void initServer() {
        //Create a new server
        apiServer = new Server(11342);
        apiServer.setStopAtShutdown(true);
        //Add handler
        apiServer.setHandler(new RequestHandler());
    }

    public boolean startServer() {
        try {
            //Start api web server
            apiServer.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean initDiscordBot() {
        try {
            discordBot = JDABuilder.createDefault(Provider.token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES)
                    .setActivity(Activity.playing("Faylisia"))
                    .build();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void initGameRules() {
        //Apply gamerules on all worlds
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, true);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_TILE_DROPS, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_MOB_LOOT, false);
            world.setGameRule(GameRule.SPAWN_RADIUS, 0);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.DROWNING_DAMAGE, false);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true);
        }
    }
}

package com.gamerduck.kitpvp.api;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.gamerduck.kitpvp.api.kits.impl.KitRegistry;
import com.gamerduck.kitpvp.api.listeners.impl.EventsManager;
import com.gamerduck.kitpvp.api.permissions.impl.RankManager;
import com.gamerduck.kitpvp.api.player.impl.PlayerManager;
import com.gamerduck.kitpvp.api.regions.impl.AreaManager;
import com.gamerduck.kitpvp.api.regions.impl.FlagHandler;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.ping.ResponseData;
import net.minestom.server.utils.time.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Properties;

public class Server {
    static Server instance;

    public static Server get() {
        return instance;
    }

    public final static Logger LOGGER = LoggerFactory.getLogger(Server.class);
    @Getter
    final Properties properties;

    @Getter
    static final RankManager rankManager = new RankManager();
    @Getter
    static final PlayerManager playerManager = new PlayerManager();
    @Getter
    static final AreaManager areaManager = new AreaManager();
    @Getter
    static final EventsManager eventsManager = new EventsManager();
    @Getter
    static final KitRegistry kitRegistry = new KitRegistry();
    @Getter
    final FileConfig tomlConf;

    public Server() {
        instance = this;
        properties = loadServerProperties();
        tomlConf = loadTOMLConfig();
        tomlConf.load();
        MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.setBrandName("KitPvP - Powered By Minestom");
        MinecraftServer.setCompressionThreshold(isNumber(properties.getProperty("network-compression-threshold")) ?
                Integer.valueOf(properties.getProperty("network-compression-threshold")) : -1);

        final Component unkownCommand = properties.getProperty("unknown-command").equals("") ? Component.empty() : GsonComponentSerializer.gson().deserialize(properties.getProperty("unknown-command"));
        MinecraftServer.getCommandManager().setUnknownCommandCallback((sender, command) -> sender.sendMessage(unkownCommand));

        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
        });

        MinecraftServer.getGlobalEventHandler().addListener(ServerListPingEvent.class, (e) -> {
            final Component motd = properties.getProperty("motd").equals("") ? Component.empty() : GsonComponentSerializer.gson().deserialize(properties.getProperty("motd"));
            ResponseData responseData = e.getResponseData();
            responseData.setDescription(motd);
        });
        rankManager.register();
        playerManager.register();
        areaManager.register();

        if (!properties.getProperty("chunk-view-distance").equals(""))
            System.setProperty("minestom.chunk-view-distance", properties.getProperty("chunk-view-distance"));
        else System.setProperty("minestom.chunk-view-distance", "1");

        if (!properties.getProperty("entity-view-distance").equals(""))
            System.setProperty("minestom.entity-view-distance", properties.getProperty("entity-view-distance"));
        else System.setProperty("minestom.entity-view-distance", "1");

        MinecraftServer.getBenchmarkManager().enable(Duration.of(10, TimeUnit.SECOND));

        OptifineSupport.enable();

        if (!properties.getProperty("velocity-secret").equalsIgnoreCase(""))
            VelocityProxy.enable(properties.getProperty("velocity-secret"));

        if (properties.getProperty("online-mode").equalsIgnoreCase("true")) MojangAuth.init();

        minecraftServer.start(properties.getProperty("server-ip"), Integer.valueOf(properties.getProperty("server-port")));

        new FlagHandler();
        LOGGER.info("Player Database Info");
        LOGGER.info("Enabled? " + tomlConf.get("databases.player.enabled"));
        LOGGER.info("Auto Reconnect? " + tomlConf.get("databases.player.autoReconnect"));
        LOGGER.info("Host? " + tomlConf.get("databases.player.host"));
        LOGGER.info("Database? " + tomlConf.get("databases.player.database"));
        LOGGER.info("Username? " + tomlConf.get("databases.player.username"));
        LOGGER.info("Password? " + tomlConf.get("databases.player.password"));
        LOGGER.info("Port? " + tomlConf.get("databases.player.port"));

        LOGGER.info(" ");
        LOGGER.info(" ");

        LOGGER.info("Permissions Database Info");
        LOGGER.info("Enabled? " + tomlConf.get("databases.permissions.enabled"));
        LOGGER.info("Auto Reconnect? " + tomlConf.get("databases.permissions.autoReconnect"));
        LOGGER.info("Host? " + tomlConf.get("databases.permissions.host"));
        LOGGER.info("Database? " + tomlConf.get("databases.permissions.database"));
        LOGGER.info("Username? " + tomlConf.get("databases.permissions.username"));
        LOGGER.info("Password? " + tomlConf.get("databases.permissions.password"));
        LOGGER.info("Port? " + tomlConf.get("databases.permissions.port"));

        LOGGER.info(" ");
        LOGGER.info(" ");

        LOGGER.info("Economy Database Info");
        LOGGER.info("Enabled? " + tomlConf.get("databases.economy.enabled"));
        LOGGER.info("Auto Reconnect? " + tomlConf.get("databases.economy.autoReconnect"));
        LOGGER.info("Host? " + tomlConf.get("databases.economy.host"));
        LOGGER.info("Database? " + tomlConf.get("databases.economy.database"));
        LOGGER.info("Username? " + tomlConf.get("databases.economy.username"));
        LOGGER.info("Password? " + tomlConf.get("databases.economy.password"));
        LOGGER.info("Port? " + tomlConf.get("databases.economy.port"));
        tomlConf.close();
    }

    private Properties loadServerProperties() {
        File settings = new File("server.properties");
        if (!settings.exists())
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("server.properties")) {Files.copy(in, settings.toPath());}
            catch (IOException e) {e.printStackTrace();}
        Properties tempProperties = new Properties();
        InputStreamReader stream;
        try {
            stream = new InputStreamReader(new FileInputStream(settings), StandardCharsets.UTF_8);
            tempProperties.load(stream);
            stream.close();
        } catch (IOException e1) {e1.printStackTrace();}
        return tempProperties;
    }

    private FileConfig loadTOMLConfig()  {
        File conf = new File("config.toml");
        if (!conf.exists())
            try (InputStream in = getClass().getClassLoader().getResource("config.toml").openStream()) {Files.copy(in, conf.toPath());}
            catch (IOException e) {e.printStackTrace();}
        FileConfig tempConfig = FileConfig.of(conf);
        return tempConfig;
    }

    private boolean isNumber(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}

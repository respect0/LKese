package me.respect.kese;

import me.respect.kese.config.config;
import me.respect.kese.cmds.AltinCmd;
import me.respect.kese.cmds.KeseAdminCmd;
import me.respect.kese.cmds.KeseCmd;
import me.respect.kese.listeners.PlayerListener;
import me.respect.kese.utils.UpdateChecker;
import me.respect.kese.vault.KeseVaultEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public final class Kese extends JavaPlugin {

    private static Kese instance;
    private KeseVaultEconomy economy;
    private Database db;
    public Configuration config = getConfig();
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.db = new Database();
        new config(this);
        try {
            db.connect();
            db.setup();
        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE, ChatColor.RED + "Unhandled exception: " + ex.getMessage(), ex);
        }

        // bStats
        Metrics metrics = new Metrics(this, 13183);

        // Update checker
        UpdateChecker.check(this);
        UpdateChecker.sendToConsole(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> UpdateChecker.check(this), 1728000,
                1728000); // 1 gün

        // Vault integration
        economy = new KeseVaultEconomy();
        Bukkit.getServicesManager().register(Economy.class, economy, instance, ServicePriority.Normal);
        getCommand("kese").setExecutor(new KeseCmd(this));
        getCommand("altin").setExecutor(new AltinCmd(this));
        getCommand("keseadmin").setExecutor(new KeseAdminCmd(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            db.getConnection().close();
        } catch (SQLException exception) {
            db.report(exception);
        }
    }

    public static Kese getInstance() {
        return instance;
    }
    public Database getDatabase() { return db;}
    public KeseVaultEconomy getEconomy() { return economy;}

}

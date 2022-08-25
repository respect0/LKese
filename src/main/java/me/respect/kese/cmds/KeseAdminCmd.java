package me.respect.kese.cmds;

import me.respect.kese.Kese;
import me.respect.kese.vault.KeseVaultEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeseAdminCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    private Kese main;
    public KeseAdminCmd(Kese main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("set")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                    return true;
                }

                if (amount < 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notZero")));
                    return true;
                }

                /* NOT:
                 getOfflinePlayer offline mode içinde sorun çıkarmıyor
                 */
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.noPlayerFound")));
                    return true;
                }

                if (economy.setBalance(target, amount).type == EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.setGoldSuccess").replaceAll("%player%", target.getName().replaceAll("%gold%", economy.format(amount)))));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                }

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("add")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                    return true;
                }

                if (amount < 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notZero")));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.noPlayerFound")));
                    return true;
                }

                if (economy.setBalance(target, economy.getBalance(target) + amount).type == EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("addGoldSuccess").replaceAll("%player%", target.getName().replaceAll("%gold%", economy.format(amount)))));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                }

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                    return true;
                }

                if (amount < 1) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notZero")));
                    return true;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("  noPlayerFound")));
                    return true;
                }

                if (economy.setBalance(target, Math.max(0, economy.getBalance(target) - amount)).type == EconomyResponse.ResponseType.SUCCESS) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("addGoldSuccess").replaceAll("%player%", target.getName().replaceAll("%gold%", String.valueOf(amount)))));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("anErrorOccurred")));
                }

                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("balance")) {
            if (args.length == 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if (!target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("noPlayerFound")));
                    return true;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("playerGoldAmount").replaceAll("%player%", target.getName().replaceAll("%gold%", String.valueOf(economy.getBalance(target))))));
                return true;
            } else {
                sendHelpMessage(sender);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            this.main.getPluginLoader().disablePlugin(this.main);
            this.main.getPluginLoader().enablePlugin(this.main);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.reload")));
            return true;
        }

        sendHelpMessage(sender);


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("set");
            completions.add("add");
            completions.add("remove");
            completions.add("balance");
            completions.add("reload");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }

    private void sendHelpMessage(CommandSender player) {
        List<String> messages = this.main.config.getStringList("gui.admin");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", messages)));

    }

}

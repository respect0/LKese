package me.respect.kese.cmds;

import me.respect.kese.Kese;
import me.respect.kese.vault.KeseVaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.logging.Level;

public class KeseCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();
    private Kese main;
    public KeseCmd(Kese main) {
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notConsole")));
            return true;
        }


        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("koy")) {
            if (args.length == 2) {
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

                String formatted = economy.format(amount);
                int z = 0;
                if(checkGold(economy.getBalance(player), amount, player)) return true;
                HashMap<Integer, ItemStack> hm = player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                if (hm.isEmpty()) {
                    if (!economy.depositPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                        return true;
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.addedGoldPouch").replaceAll("%gold%", formatted)));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.goldAmount").replaceAll("%gold%", economy.format(economy.getBalance(player)))));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.incomingGold").replaceAll("%gold%", formatted)));
                    return true;
                } else {
                    for (Map.Entry<Integer, ItemStack> entry : hm.entrySet()) {
                        ItemStack value = entry.getValue();
                        z += value.getAmount();
                    }
                    if(checkGold(economy.getBalance(player), amount - z, player)) return true;
                    if (!economy.depositPlayer(player, amount - z).transactionSuccess()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                        return true;
                    }

                    formatted = economy.format(amount - z);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.addedGoldPouch").replaceAll("%gold%", formatted)));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.goldAmount").replaceAll("%gold%", economy.format(economy.getBalance(player)))));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.incomingGold").replaceAll("%gold%", formatted)));

                    return true;
                }

            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("al")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                    return true;
                }

                if (amount < 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notZero")));
                    return true;
                }
                if(amount > this.main.config.getInt("settings.maxWithdraw")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.maxWithdraw").replaceAll("%max%", String.valueOf(this.main.config.getInt("settings.maxWithdraw")))));
                    return true;
                }
                String formatted = economy.format(amount);

                if (economy.has(player, amount)) {
                    double bal = economy.getBalance(player);
                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.anErrorOccurred")));
                        return true;
                    }
                    HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                    if (!map.isEmpty() && map.get(0).getAmount() != 0) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.goldDropped")));
                        if (map.get(0).getAmount() <= 64) {
                            player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount()));
                        } else {
                            for (int i = map.get(0).getAmount(); i >= 64; i = i - 64) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, 64));
                            }
                            if (map.get(0).getAmount() % 64 != 0) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount() % 64));
                            }
                        }
                    }
                    return true;
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notGold")));
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.notNumber")));
                return true;
            }
        }

        if (args.length > 0) {
            sendHelpMessage(player);
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.pouchGold").replaceAll("%gold%", economy.format(economy.getBalance(player)))));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("al");
            completions.add("koy");
            completions.add("yardım");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }

    private boolean checkGold(double keseAltini, double eklenecek, Player player) {
        if(keseAltini == this.main.config.getDouble("settings.maxGold")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.maxGold").replaceAll("%gold%", String.valueOf(this.main.config.getInt("maxGold")))));
            return true;
        } else if((keseAltini + eklenecek) > this.main.config.getDouble("settings.maxGold")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.main.config.getString("messages.maxGoldAndAdded").replaceAll("%max%", String.valueOf(this.main.config.getDouble("settings.maxGold"))).replaceAll("%gold%", String.valueOf((this.main.config.getDouble("settings.maxGold") - keseAltini < 0) ? 0 : this.main.config.getDouble("settings.maxGold") - keseAltini))));
            return true;
        }
        return false;
    }

    private void sendHelpMessage(Player player) {
        List<String> messages = this.main.config.getStringList("gui.player");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.join("\n", messages)));
    }

}
// $Id$
/*
 * Copyright (C) 2010, 2011 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.commandbook.commands;

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.sk89q.commandbook.CommandBookPlugin;
import com.sk89q.commandbook.CommandBookUtil;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class FunCommands {
    
    protected static Random random = new Random();
    
    @Command(aliases = {"spawnmob"},
            usage = "<mob> [count] [location]", desc = "Spawn a mob",
            flags = "dirp",
            min = 1, max = 3)
    @CommandPermissions({"commandbook.spawnmob"})
    public static void spawnMob(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Location loc;

        if (args.argsLength() >= 3) {
            loc = plugin.matchLocation(sender, args.getString(2));
        } else {
            loc = plugin.checkPlayer(sender).getLocation();
        }

        String creatureName = args.getString(0);
        int count = Math.max(1, args.getInteger(1, 1));
        CreatureType type = plugin.matchCreatureType(sender, creatureName);
        
        plugin.checkPermission(sender, "commandbook.spawnmob." + type.getName());
        
        if (count > 10) {
            plugin.checkPermission(sender, "commandbook.spawnmob.many");
        }
        
        for (int i = 0; i < count; i++) {
            LivingEntity creature = loc.getWorld().spawnCreature(loc, type);
            if (args.hasFlag('d')) {
                creature.setHealth(1);
            }
            if (args.hasFlag('i')) {
                creature.setFireTicks(20 * 25);
            }
            if (args.hasFlag('r')) {
                creature.setVelocity(new Vector(0, 2, 0));
            }
            if (args.hasFlag('p') && creature instanceof Creeper) {
                ((Creeper) creature).setPowered(true);
            }
        }
        
        sender.sendMessage(ChatColor.YELLOW + "" + count + " mob(s) spawned!");
    }
    
    @Command(aliases = {"slap"},
            usage = "[target]", desc = "Slap a player", flags = "hdvs",
            min = 0, max = 1)
    @CommandPermissions({"commandbook.slap"})
    public static void slap(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Iterable<Player> targets = null;
        boolean included = false;
        int count = 0;
        
        // Detect arguments based on the number of arguments provided
        if (args.argsLength() == 0) {
            targets = plugin.matchPlayers(plugin.checkPlayer(sender));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.slap");
        } else if (args.argsLength() == 1) {            
            targets = plugin.matchPlayers(sender, args.getString(0));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.slap.other");
        }

        for (Player player : targets) {
            count++;
            
            if (args.hasFlag('v')) {
                player.setVelocity(new Vector(
                        random.nextDouble() * 10.0 - 5,
                        random.nextDouble() * 10,
                        random.nextDouble() * 10.0 - 5));
            } else if (args.hasFlag('h')) {
                player.setVelocity(new Vector(
                        random.nextDouble() * 5.0 - 2.5,
                        random.nextDouble() * 5,
                        random.nextDouble() * 5.0 - 2.5));
            } else {
                player.setVelocity(new Vector(
                        random.nextDouble() * 2.0 - 1,
                        random.nextDouble() * 1,
                        random.nextDouble() * 2.0 - 1));
            }
            
            if (args.hasFlag('d')) {
                player.setHealth(player.getHealth() - 1);
            }

            if (args.hasFlag('s')) {
                // Tell the user
                if (player.equals(sender)) {
                    player.sendMessage(ChatColor.YELLOW + "Slapped!");
                    
                    // Keep track of this
                    included = true;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You've been slapped by "
                            + plugin.toName(sender) + ".");
                    
                }
            } else {
                if (count < 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " slapped " + plugin.toName(player));
                } else if (count == 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " slapped more people...");
                }
            }
        }
        
        // The player didn't receive any items, then we need to send the
        // user a message so s/he know that something is indeed working
        if (!included && args.hasFlag('s')) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Players slapped.");
        }
    }
    
    @Command(aliases = {"rocket"},
            usage = "[target]", desc = "Rocket a player", flags = "hs",
            min = 0, max = 1)
    @CommandPermissions({"commandbook.rocket"})
    public static void rocket(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Iterable<Player> targets = null;
        boolean included = false;
        int count = 0;
        
        // Detect arguments based on the number of arguments provided
        if (args.argsLength() == 0) {
            targets = plugin.matchPlayers(plugin.checkPlayer(sender));
        } else if (args.argsLength() == 1) {            
            targets = plugin.matchPlayers(sender, args.getString(0));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.rocket.other");
        }

        for (Player player : targets) {
            if (args.hasFlag('h')) {
                player.setVelocity(new Vector(0, 50, 0));
            } else {
                player.setVelocity(new Vector(0, 20, 0));
            }

            if (args.hasFlag('s')) {
                // Tell the user
                if (player.equals(sender)) {
                    player.sendMessage(ChatColor.YELLOW + "Rocketed!");
                    
                    // Keep track of this
                    included = true;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You've been rocketed by "
                            + plugin.toName(sender) + ".");
                    
                }
            } else {
                if (count < 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " rocketed " + plugin.toName(player));
                } else if (count == 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " rocketed more people...");
                }
            }
        }
        
        // The player didn't receive any items, then we need to send the
        // user a message so s/he know that something is indeed working
        if (!included && args.hasFlag('s')) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Players rocketed.");
        }
    }
    
    @Command(aliases = {"barrage"},
            usage = "[target]", desc = "Send a barrage of arrows", flags = "s",
            min = 0, max = 1)
    @CommandPermissions({"commandbook.barrage"})
    public static void barrage(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Iterable<Player> targets = null;
        boolean included = false;
        int count = 0;
        
        // Detect arguments based on the number of arguments provided
        if (args.argsLength() == 0) {
            targets = plugin.matchPlayers(plugin.checkPlayer(sender));
        } else if (args.argsLength() == 1) {            
            targets = plugin.matchPlayers(sender, args.getString(0));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.barrage.other");
        }

        for (Player player : targets) {
            double diff = (2 * Math.PI) / 24.0;
            for (double a = 0; a < 2 * Math.PI; a += diff) {
                Vector vel = new Vector(Math.cos(a), 0, Math.sin(a));
                CommandBookUtil.sendArrowFromPlayer(player, vel, 2, 12);
            }

            if (args.hasFlag('s')) {
                // Tell the user
                if (player.equals(sender)) {
                    player.sendMessage(ChatColor.YELLOW + "Barrage attack!");
                    
                    // Keep track of this
                    included = true;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "BARRAGE attack from "
                            + plugin.toName(sender) + ".");
                    
                }
            } else {
                if (count < 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " used BARRAGE on " + plugin.toName(player));
                } else if (count == 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " used it more people...");
                }
            }
        }
        
        // The player didn't receive any items, then we need to send the
        // user a message so s/he know that something is indeed working
        if (!included && args.hasFlag('s')) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Barrage attack sent.");
        }
    }
    
    @Command(aliases = {"shock"},
            usage = "[target]", desc = "Shock a player", flags = "ksa",
            min = 0, max = 1)
    @CommandPermissions({"commandbook.shock"})
    public static void shock(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Iterable<Player> targets = null;
        boolean included = false;
        int count = 0;
        
        // Detect arguments based on the number of arguments provided
        if (args.argsLength() == 0) {
            targets = plugin.matchPlayers(plugin.checkPlayer(sender));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.shock");
        } else if (args.argsLength() == 1) {            
            targets = plugin.matchPlayers(sender, args.getString(0));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.shock.other");
        }

        for (final Player player : targets) {
            count++;
            
            // Area effect
            if (args.hasFlag('a')) {
                final Location origLoc = player.getLocation();
                
                for (int i = 0; i < 10; i++) {
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            Location loc = origLoc.clone();
                            loc.setX(loc.getX() + random.nextDouble() * 20 - 10);
                            loc.setZ(loc.getZ() + random.nextDouble() * 20 - 10);
                            player.getWorld().strikeLightning(loc);
                        }
                    }, Math.max(0, i * 3 + random.nextInt(10) - 5));
                }
            } else {
                player.getWorld().strikeLightning(player.getLocation());
            }
            
            if (args.hasFlag('k')) {
                player.setHealth(0);
            }

            if (args.hasFlag('s')) {
                // Tell the user
                if (player.equals(sender)) {
                    player.sendMessage(ChatColor.YELLOW + "Shocked!");
                    
                    // Keep track of this
                    included = true;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You've been shocked by "
                            + plugin.toName(sender) + ".");
                    
                }
            } else {
                if (count < 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " shocked " + plugin.toName(player));
                } else if (count == 6) {
                    plugin.getServer().broadcastMessage(
                            ChatColor.YELLOW + plugin.toName(sender)
                            + " shocked more people...");
                }
            }
        }
        
        // The player didn't get anything, then we need to send the
        // user a message so s/he know that something is indeed working
        if (!included && args.hasFlag('s')) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Players shocked.");
        }
    }
    
    @Command(aliases = {"thor"},
            usage = "[target]", desc = "Give a player Thor power", flags = "",
            min = 0, max = 1)
    @CommandPermissions({"commandbook.thor"})
    public static void thor(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Iterable<Player> targets = null;
        boolean included = false;
        
        // Detect arguments based on the number of arguments provided
        if (args.argsLength() == 0) {
            targets = plugin.matchPlayers(plugin.checkPlayer(sender));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.thor");
        } else if (args.argsLength() == 1) {            
            targets = plugin.matchPlayers(sender, args.getString(0));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.thor.other");
        }

        for (final Player player : targets) {
            plugin.getSession(player).setHasThor(true);
            
            // Tell the user
            if (player.equals(sender)) {
                player.sendMessage(ChatColor.YELLOW + "You've been given Thor's hammer (use any pickaxe)!");
                
                // Keep track of this
                included = true;
            } else {
                player.sendMessage(ChatColor.YELLOW + "You've been given Thor's hammer by "
                        + plugin.toName(sender) + ".");
                
            }
        }
        
        // The player didn't get anything, then we need to send the
        // user a message so s/he know that something is indeed working
        if (!included && args.hasFlag('s')) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Players given Thor's hammer.");
        }
        
    }
    
    @Command(aliases = {"unthor"},
            usage = "[target]", desc = "Revoke a player's Thor power", flags = "",
            min = 0, max = 1)
    @CommandPermissions({"commandbook.thor"})
    public static void unthor(CommandContext args, CommandBookPlugin plugin,
            CommandSender sender) throws CommandException {

        Iterable<Player> targets = null;
        boolean included = false;
        
        // Detect arguments based on the number of arguments provided
        if (args.argsLength() == 0) {
            targets = plugin.matchPlayers(plugin.checkPlayer(sender));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.thor");
        } else if (args.argsLength() == 1) {            
            targets = plugin.matchPlayers(sender, args.getString(0));
            
            // Check permissions!
            plugin.checkPermission(sender, "commandbook.thor.other");
        }

        for (final Player player : targets) {
            plugin.getSession(player).setHasThor(false);
            
            // Tell the user
            if (player.equals(sender)) {
                player.sendMessage(ChatColor.YELLOW + "You've lost Thor's hammer!");
                
                // Keep track of this
                included = true;
            } else {
                player.sendMessage(ChatColor.YELLOW + "Thor's hammer has been revoked from you by "
                        + plugin.toName(sender) + ".");
                
            }
        }
        
        // The player didn't get anything, then we need to send the
        // user a message so s/he know that something is indeed working
        if (!included && args.hasFlag('s')) {
            sender.sendMessage(ChatColor.YELLOW.toString() + "Thor's hammer revokved from players.");
        }
        
    }
    
}

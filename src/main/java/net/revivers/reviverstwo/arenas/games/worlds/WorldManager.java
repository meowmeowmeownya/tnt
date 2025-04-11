package net.revivers.reviverstwo.arenas.games.worlds;

import net.revivers.reviverstwo.ReviversTwo;
import net.revivers.reviverstwo.arenas.games.Game;
import net.revivers.reviverstwo.arenas.games.GameManager;
import net.revivers.reviverstwo.arenas.Arena;
import net.revivers.reviverstwo.utilities.RandomText;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class WorldManager {

    private static final HashMap<Game, World> gameWorlds = new HashMap<>();

    public static void startUp() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ReviversTwo.getPlugin(), () -> {
            if (Bukkit.getOnlinePlayers().size() <= ReviversTwo.getConfiguration().getLong("World Manager.Max Players") && !gameWorlds.isEmpty()) {
                for (Game game : new ArrayList<>(gameWorlds.keySet())) {
                    if (GameManager.isGameRegistered(game)) continue;
                    if (gameWorlds.get(game).getPlayers().size() > 0) continue;

                    deleteWorld(gameWorlds.get(game));
                    gameWorlds.remove(game);

                    return;
                }
            }
        }, 0, ReviversTwo.getConfiguration().getLong("World Manager.Queue Rate") * 20L);
    }

    public static void cleanUp() {
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();

            if (worldName.startsWith("dynamite_temp_world-")) {
                try {
                    // Get all players in the world and teleport them to the spawn of the first world
                    for (Player player : world.getPlayers()) {
                        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    }
                    
                    // Safely unload the world before deleting
                    Bukkit.unloadWorld(world, false);
                    
                    // Delete the world folder after unloading
                    File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
                    deleteFolder(worldFolder);
                } catch (Exception e) {
                    // Log the error but continue
                    ReviversTwo.getPlugin().getLogger().warning("Error cleaning up world: " + worldName);
                    e.printStackTrace();
                }
            }
        }
    }

    public static World cloneWorld(Game game, Arena arena) {
        String randomWorldName = "dynamite_temp_world-" + arena.getWorld().getName() + "-" + new RandomText(12).get();
        World world = WorldUtils.copyWorld(arena.getWorld(), randomWorldName);
        world.setAutoSave(false);
        gameWorlds.put(game, world);
        return world;
    }

    public static void deleteWorld(World world) {
        Bukkit.getServer().unloadWorld(world, false);
        WorldUtils.deleteWorld(world.getWorldFolder());
    }
    
    // Added helper method to recursively delete a folder.
    private static void deleteFolder(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        folder.delete();
    }
}
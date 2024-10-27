package com.zenakin.robsmod;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zenakin.robsmod.config.RobsConfig;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import com.zenakin.robsmod.config.pages.PageName___;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = RobsMod.MODID, name = RobsMod.NAME, version = RobsMod.VERSION)
public class RobsMod {

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    @Mod.Instance(MODID)

    public static RobsMod instance;
    public RobsConfig config;
    public String displayMessage;
    private final Map<String, JsonObject> playerDataCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    //TODO: FUTUR FEATURE - public static Map<String, Boolean> playersInParty = new HashMap<>();
    private static final long CACHE_EXPIRY = TimeUnit.MINUTES.toMillis(RobsConfig.cacheExpiry);
    private long lastRequestTime = 0;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        config = new RobsConfig();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new StartMod());
    }

    public class StartMod {
        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ChatComponentText message0 = new ChatComponentText("Initial chat message...");
                    ChatStyle style = new ChatStyle()
                            .setColor(EnumChatFormatting.LIGHT_PURPLE)
                            .setBold(true)
                            .setItalic(true)
                            .setUnderlined(true);
                    message0.setChatStyle(style);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(message0);
                    StartMethod();
                }
            }, 4000);
        }
    }

    private void StartMethod() {
        if (!RobsConfig.variableName) {
            displayMessage = "Mod is ENABLED!";
            return;
        }
        displayMessage = "Mod is DISSABLED...";
    }

    public boolean checkNick(JsonObject playerData) {
        String jsonString = "{\"success\":true,\"player\":null}";
        JsonElement jsonElement = new JsonParser().parse(jsonString);

        return playerData.get("player").isJsonNull();
    }

    private synchronized void tempDelay(Runnable requestTask) throws InterruptedException {
        long time = System.currentTimeMillis();
        long timeSinceLast = time - lastRequestTime;
        int intervalInMs = 500;

        if (timeSinceLast < intervalInMs) {
            Thread.sleep(intervalInMs - timeSinceLast);
        }

        requestTask.run();
        lastRequestTime = System.currentTimeMillis();
    }

    private synchronized void performRequestWithDelay(Runnable requestTask) throws InterruptedException {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRequest = currentTime - lastRequestTime;

        if (timeSinceLastRequest < RobsConfig.scanInterval) {
            Thread.sleep(RobsConfig.scanInterval - timeSinceLastRequest);
        }

        requestTask.run();
        lastRequestTime = System.currentTimeMillis();
    }

    private JsonObject getPlayerData(String playerName) throws Exception {
        long currentTime = System.currentTimeMillis();

        if (playerDataCache.containsKey(playerName) && (currentTime - cacheTimestamps.get(playerName) < CACHE_EXPIRY)) {
            return playerDataCache.get(playerName);
        }

        String urlString = "https://api.hypixel.net/player?key=" + RobsConfig.apiKey + "&name=" + playerName;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(response.toString());
            JsonObject playerData = jsonElement.getAsJsonObject();
            
            playerDataCache.put(playerName, playerData);
            cacheTimestamps.put(playerName, currentTime);
            
            return playerData;
        }
    }

    private int getBedwarsLevel(JsonObject playerData) {
        JsonObject stats = playerData.getAsJsonObject("player").getAsJsonObject("achievements");
        
        return stats.get("bedwars_level").getAsInt();
    }

    private float getBedwarsWLR(JsonObject playerData) {
        JsonObject stats = playerData.getAsJsonObject("player").getAsJsonObject("stats").getAsJsonObject("Bedwars");
        int wins = stats.get("wins_bedwars").getAsInt();
        int losses = stats.get("losses_bedwars").getAsInt();
        
        return (round((float)wins / losses, RobsConfig.precision));
    }

    public Set<String> getPlayersInTabList() {
        Set<String> scannedPlayers = ConcurrentHashMap.newKeySet();

        for (EntityPlayer player : Minecraft.getMinecraft().theWorld.playerEntities) {
            String playerName = player.getName();

            //TODO: Filter out party members and the player
            //TODO: FUTUR FEATURE - if (playersInParty.containsKey(playerName)) continue;

            scannedPlayers.add(playerName);
        }
        return scannedPlayers;
    }

    private static String getCurrentAreaFromScoreboard() {
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        if (scoreboard != null) {
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
            if (objective != null) {
                String obj = objective.getDisplayName();
                return cleanSB(obj);
            }
            return null;
        }
        return  null;
    }

    public static String getCurrentMapFromScoreboard() {
        List<String> scoreboardDetails = getSidebarLines();
        String mapName = null;
        assert scoreboardDetails != null;
        for (String s : scoreboardDetails) {
            String sCleaned = cleanSB(s);

            if (sCleaned.contains("Map: ")) {
                mapName = sCleaned.substring(5).trim();
            }
        }
        return mapName;
    }

    // cleanSB() and getSidebarLines() are taken from Dungeon Rooms Mod by Quantizr(_risk) who used some code from Danker's Skyblock Mod under the GNU General Public License.
    public static String cleanSB(String scoreboard) {
        char[] nvString = StringUtils.stripControlCodes(scoreboard).toCharArray();
        StringBuilder cleaned = new StringBuilder();

        for (char c : nvString) {
            if ((int) c > 20 && (int) c < 127) {
                cleaned.append(c);
            }
        }
        return cleaned.toString();
    }

    public static List<String> getSidebarLines() {
        List<String> lines = new ArrayList<>();
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        if (scoreboard == null) return null;
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return null;
        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream()
                .filter(input -> input != null && input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(list, scores.size() - 15));
        } else {
            scores = list;
        }

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            lines.add(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()));
        }

        return lines;
    }

    public static boolean isInBedwarsGame() {
        String currentArea = getCurrentAreaFromScoreboard();
        String currentMap = getCurrentMapFromScoreboard();
        return currentArea != null && currentMap != null && currentArea.toLowerCase().contains("bed wars");
    }

    public boolean isMapBlacklisted() {
        String map = getCurrentMapFromScoreboard();
        if (map == null) {
            return false;
        }

        map = map.trim();

        switch (map) {
            case "Acropolis":
                return PageName___.map1;
            case "Aetius":
                return PageName___.map2;
            case "Airshow":
                return PageName___.map3;
            case "Amazon":
                return PageName___.map4;
            case "Ambush":
                return PageName___.map5;
            case "Apollo":
                return PageName___.map6;
            case "Arcade":
                return PageName___.map7;
            case "Arid":
                return PageName___.map8;
            case "Ashfire":
                return PageName___.map9;
            case "Aqil":
                return PageName___.map10;
            case "Bio-Hazard":
                return PageName___.map11;
            case "Blossom":
                return PageName___.map12;
            case "Cascade":
                return PageName___.map13;
            case "Casita":
                return PageName___.map14;
            case "Cliffside":
                return PageName___.map15;
            case "Crogorm":
                return PageName___.map16;
            case "Crypt":
                return PageName___.map17;
            case "Deadwood":
                return PageName___.map18;
            case "Dockyard":
                return PageName___.map19;
            case "Dragon Light":
                return PageName___.map20;
            case "Dragonstar":
                return PageName___.map21;
            case "Gateway":
                return PageName___.map22;
            case "Glacier":
                return PageName___.map23;
            case "Hanging Gardens":
                return PageName___.map24;
            case "Harvest":
                return PageName___.map25;
            case "Hollow":
                return PageName___.map26;
            case "Impere":
                return PageName___.map27;
            case "Ironclad":
                return PageName___.map28;
            case "Keep":
                return PageName___.map29;
            case "Lightstone":
                return PageName___.map30;
            case "Lighthouse":
                return PageName___.map31;
            case "Lotus":
                return PageName___.map32;
            case "Lucky Rush":
                return PageName___.map33;
            case "Meso":
                return PageName___.map34;
            case "Mirage":
                return PageName___.map35;
            case "Nebuc":
                return PageName___.map36;
            case "Orbit":
                return PageName___.map37;
            case "Orchestra":
                return PageName___.map38;
            case "Pavilion":
                return PageName___.map39;
            case "Pernicious":
                return PageName___.map40;
            case "Playground":
                return PageName___.map41;
            case "Polygon":
                return PageName___.map42;
            case "Rooted":
                return PageName___.map43;
            case "Rooftop":
                return PageName___.map44;
            case "Sanctum":
                return PageName___.map45;
            case "Scorched Sands":
                return PageName___.map46;
            case "Serenity":
                return PageName___.map47;
            case "Siege":
                return PageName___.map48;
            case "Sky Rise":
                return PageName___.map49;
            case "Slumber":
                return PageName___.map50;
            case "Solace":
                return PageName___.map51;
            case "Speedway":
                return PageName___.map52;
            case "Steampunk":
                return PageName___.map53;
            case "Toro":
                return PageName___.map54;
            case "Tuzi":
                return PageName___.map55;
            case "Urban Plaza":
                return PageName___.map56;
            case "Vigilante":
                return PageName___.map57;
            case "Waterfall":
                return PageName___.map58;
            case "Yue":
                return PageName___.map59;
            case "Zarzul":
                return PageName___.map60;
            default:
                return false; // Return false if the map name doesn't match any known maps
        }
    }

    private static float round(float val, int precision){
        int tmp1 = (int) (val*Math.pow(10, precision));
        return (float) tmp1 / (float) Math.pow(10, precision);

    }
}

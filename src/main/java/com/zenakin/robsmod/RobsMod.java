package com.zenakin.robsmod;

import java.util.Scanner;
import com.zenakin.robsmod.config.RobsConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;  // Required for CommandBase
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Scanner;


@Mod(modid = RobsMod.MODID, version = RobsMod.VERSION, name = RobsMod.NAME)
public class RobsMod {
    public static final String MODID = "@ID@";
    public static final String VERSION = "@VER@";
    public static final String NAME = "@NAME@";


    public static RobsConfig config; // Add this if missing

    // Define the floor give XP amounts and XP requirements
    static final int[] floorGiveXpAmount = {160, 400, 1420, 2400, 5000};
    static final int[] floorXpRequirements = {625, 3045, 17960, 97640, 488640};

    static int floor2Cost = RobsConfig.f2;
    static int floor3Cost = RobsConfig.f3;
    static int floor4Cost = RobsConfig.f4;
    static int floor5Cost = RobsConfig.f5;
    static int floor6Cost = RobsConfig.f6;

    public static String f2result;
    public static String f3result;
    public static String f4result;
    public static String f5result;
    public static String f6result;
    public static String totalCostResult;

    static final int[] floorCosts = {0, floor2Cost, floor3Cost, floor4Cost, floor5Cost, floor6Cost};


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        config = new RobsConfig();
    }





    public static void main() {

        // Input current XP
        int currentXp = RobsConfig.currentXP;

        // Target XP (fixed value)
        int targetXp = 488640;

        // Calculate XP needed to reach the target
        int xpNeeded = targetXp - currentXp;

        // Array to store the number of runs needed for each floor
        int[] floors = new int[floorGiveXpAmount.length];

        // Variable to store total cost
        int totalCost = 0;

        // Process each floor
        for (int i = 0; i < floorGiveXpAmount.length; i++) {
            if (currentXp >= targetXp) {  // If current XP already exceeds or meets target XP
                break;
            }
            if (currentXp < floorXpRequirements[i]) {  // If current XP is less than the requirement for this floor
                int xpToThreshold = floorXpRequirements[i] - currentXp;
                int runsNeeded = (xpToThreshold + floorGiveXpAmount[i] - 1) / floorGiveXpAmount[i];
                floors[i] += runsNeeded;
                totalCost += runsNeeded * floorCosts[i + 1];
                currentXp += runsNeeded * floorGiveXpAmount[i];
            }
            if (currentXp >= targetXp) {
                break;
            }
        }



        f2result = "Number of floor 2's: " + String.valueOf(floors[0]);
        f3result = "Number of floor 3's: " + String.valueOf(floors[1]);
        f4result = "Number of floor 4's: " + String.valueOf(floors[2]);
        f5result = "Number of floor 5's: " + String.valueOf(floors[3]);
        f6result = "Number of floor 6's: " + String.valueOf(floors[4]);
        totalCostResult = "Total cost: " + String.valueOf(totalCost);

    }


}


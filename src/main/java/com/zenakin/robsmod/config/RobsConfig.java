package com.zenakin.robsmod.config;

import cc.polyfrost.oneconfig.hud.Hud;
import com.zenakin.robsmod.RobsMod;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import com.zenakin.robsmod.RobsMod;
import com.zenakin.robsmod.config.pages.PageName___;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import com.zenakin.robsmod.hud.HudName___;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class RobsConfig extends Config {


    @Number(
            name = "Current Cata XP",
            description = "The current catacombs xp of the player you are carrying.",
            size = OptionSize.DUAL,
            min = 0, max = 100000000,
            category = "Config",
            subcategory = "Cata XP"
    )
    public static int currentXP = 0;

    @Number(
            name = "Floor 2",
            description = "Cost per run for a floor 2",
            size = OptionSize.DUAL,
            min = 0, max = 100000000,
            category = "Config",
            subcategory = "Floor cost per run: "
    )
    public static int f2 = 10000;

    @Number(
            name = "Floor 3",
            description = "Cost per run for a floor 3",
            size = OptionSize.DUAL,
            min = 0, max = 100000000,
            category = "Config",
            subcategory = "Floor cost per run: "
    )
    public static int f3 = 50000;

    @Number(
            name = "Floor 4",
            description = "Cost per run for a floor 4",
            size = OptionSize.DUAL,
            min = 0, max = 100000000,
            category = "Config",
            subcategory = "Floor cost per run: "
    )
    public static int f4 = 350000;

    @Number(
            name = "Floor 5",
            description = "Cost per run for a floor 5",
            size = OptionSize.DUAL,
            min = 0, max = 100000000,
            category = "Config",
            subcategory = "Floor cost per run: "
    )
    public static int f5 = 1000000;

    @Number(
            name = "Floor 6",
            description = "Cost per run for a floor 6",
            size = OptionSize.DUAL,
            min = 0, max = 100000000,
            category = "Config",
            subcategory = "Floor cost per run: "
    )
    public static int f6 = 2000000;



    @Button(
            name = "Calculate",
            description = "Calculate the total cost and amount of runs. ",
            text = "Click",
            subcategory = "Floor cost per run: ",
            category = "Config",
            size = OptionSize.DUAL
    )
    public static void finalCalculate() {
        RobsMod.main();
    }


    @HUD(
            name = "Display Results"
    )
    public HudName___ hud = new HudName___();


    public RobsConfig() {
        super(new Mod(RobsMod.NAME, ModType.UTIL_QOL), RobsMod.MODID + ".json");

        initialize();

    }
}


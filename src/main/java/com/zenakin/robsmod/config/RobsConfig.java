package com.zenakin.robsmod.config;

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

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class RobsConfig extends Config {
    public static RobsConfig instance;

    /** TEMP VARIABLES (to keep other code working) */
    public static long cacheExpiry = 1000;
    public static int scanInterval = 10;
    public static String apiKey = "";
    public static int precision = 3;

    @Switch(
            name = "Name...",
            description = "Description..."
    )
    public static boolean variableName = true;

    @Number(
            name = "Name...",
            description = "Description...",
            size = OptionSize.DUAL,
            min = 10, max = 5000,
            step = 25
    )
    public static int variableName2 = 150;

    @Checkbox(
            name = "Name...",
            description = "Description..."
    )
    public static boolean variableName3 = true;

    @Slider(
            name = "Name...",
            description = "Description...",
            min = 0, max = 10.1F,
            step = 1
    )
    public static int variableName4 = 2;

    @Color(
            name = "Name...",
            description = "Description..."
    )
    public static OneColor variableName5 = new OneColor(0, 255, 0);

    @Text(
            name = "Name...",
            placeholder = "Placeholder...",
            secure = true, multiline = false
    )
    public static String variableName6 = "";

    @Page(
            name = "Name...",
            location = PageLocation.BOTTOM,
            description = "Description..."
    )
    public PageName___ variableName7 = new PageName___();

    @Dropdown(
            name = "Name...", // Name of the Dropdown
            options = {"Option 1", "Option 2", "Option 3", "Option 4"} // Options available.
    )
    public static int variableName8 = 1; // Default option (in this case "Option 2")

    public RobsConfig() {
        super(new Mod(RobsMod.NAME, ModType.UTIL_QOL), RobsMod.MODID + ".json");

        initialize();

        RobsMod.instance.config = this;
    }
}


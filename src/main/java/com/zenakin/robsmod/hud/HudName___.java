package com.zenakin.robsmod.hud;

/** FOR SINGLE LINE */
// import cc.polyfrost.oneconfig.hud.SingleTextHud;
import cc.polyfrost.oneconfig.hud.TextHud;

import java.util.List;

/** FOR SINGLE LINE */
// public class HudName___ extends SingleTextHud {
public class HudName___ extends TextHud {

    public HudName___() {
        super(true);
    }

    /** FOR SINGLE LINE */
    /*
    @Override
    public String getText(boolean example) {
        return "Example Text";
    }
    */

    @Override
    protected void getLines(List<String> line, boolean example) {
        line.add("Example Line");
        line.add("Example Line");
        line.add("Example Line");
    }
}

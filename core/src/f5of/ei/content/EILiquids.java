package f5of.ei.content;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class EILiquids {
    public static Liquid argon;

    public static void load() {
        argon = new Liquid("argon", Color.valueOf("d183eb")){{
            heatCapacity = 0.2f;
            viscosity = 0f;
            temperature = 0.5f;
        }};
    }
}

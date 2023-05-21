package f5of.ei.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class EIItems {
    public static Item titanium, copper, krynite;

    public static void load() {
        copper = new Item("copper", Color.valueOf("d99d73")){{
            hardness = 1;
            cost = 0.5f;
        }};
        titanium = new Item("titanium", Color.valueOf("8da1e3")){{
            hardness = 3;
            cost = 1f;
        }};
        krynite = new Item("krynite", Color.valueOf("a18de3")){{
            hardness = 3;
            cost = 1.5f;
        }};
    }
}

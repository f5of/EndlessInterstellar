package f5of.ei.railroad;

import mindustry.gen.Building;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;

public class RailRoadBlock extends Wall {
    public RailRoadBlock(String name) {
        super(name);
        size = 1;
        health = 200;
        alwaysUnlocked = true;

        update = true;
        noUpdateDisabled = false;
        breakable = true;
        solid = true;
        group = BlockGroup.none;
    }

    public class RailRoadBuild extends Building {

    }
}

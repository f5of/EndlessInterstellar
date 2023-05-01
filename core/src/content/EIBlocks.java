package content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Log;
import mindustry.content.Items;
import mindustry.graphics.Shaders;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.storage.CoreBlock;
import world.blocks.prower.BeamTransmitter;

public class EIBlocks {
    public static CoreBlock colonyCore;
    public static BeamTransmitter rayTransmitter;

    public static void load() {
        colonyCore = new CoreBlock("colony-core"){{
            requirements(Category.power, ItemStack.with(Items.copper, 1));
            health = 3600;
            armor = 5f;
            size = 4;
            unitCapModifier = 10;
            itemCapacity = 5000;
            alwaysUnlocked = true;
            hasPower = true;
            outputsPower = true;
            consumesPower = false;
            buildType = Build::new;
            clipSize = 1000;
        }
            class Build extends CoreBlock.CoreBuild {
                @Override
                public float getPowerProduction() {
                    return 200f / 60f;
                }

                int i = 0;
                @Override
                public void draw() {
                    super.draw();

                    i = 0;
                    Core.atlas.getTextures().each(t -> {
                        i++;
                        Draw.rect(new TextureRegion(t), x + 256 * (i % 5), y + 256 * (i / 5), 256, 256);
                    });
                }
            }
        };
        rayTransmitter = new BeamTransmitter("ray-transmitter"){{
            requirements(Category.power, ItemStack.with(Items.copper, 1));
            health = 20;
            size = 1;
            range = 15;
            laserColor1 = Color.valueOf("fd9e81");
        }};
    }
}

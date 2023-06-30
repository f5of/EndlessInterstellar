package f5of.ei.content;

import arc.graphics.Color;
import f5of.ei.satellites.SatellitesSystemAccessorBlock;
import f5of.ei.world.blocks.power.BeamTransmitter;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.environment.SteamVent;
import mindustry.world.blocks.liquid.ArmoredConduit;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFlame;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

public class EIBlocks {
    public static CoreBlock colonyCore;
    public static BeamTransmitter rayTransmitter;
    public static BeamNode substation;
    public static Conveyor titaniumConveyor;
    public static ArmoredConduit titaniumConduit;
    public static GenericCrafter kryniteSmelter;

    public static SteamVent snowVent, stoneVent;

    public static SatellitesSystemAccessorBlock satellitesSystemAccessorBlock;

    public static void load() {
        snowVent = new SteamVent("snow-vent") {{
            variants = 2;
            parent = blendGroup = Blocks.snow;
            attributes.set(Attribute.steam, 0.25f);
        }};
        stoneVent = new SteamVent("stone-vent") {{
            variants = 2;
            parent = blendGroup = Blocks.stone;
            attributes.set(Attribute.steam, 1f);
        }};

        colonyCore = new CoreBlock("colony-core") {{
            requirements(Category.effect, ItemStack.with(EIItems.copper, 1));
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
        }
            class Build extends CoreBlock.CoreBuild {
                @Override
                public float getPowerProduction() {
                    return 200f / 60f;
                }
            }
        };
        rayTransmitter = new BeamTransmitter("ray-transmitter") {{
            requirements(Category.power, ItemStack.with(EIItems.copper, 2, EIItems.titanium, 3));
            health = 20;
            size = 1;
            range = 15;
        }};
        substation = new BeamNode("substation") {{
            requirements(Category.power, ItemStack.with(EIItems.copper, 10, EIItems.titanium, 2));
            range = 5;
            health = 40;
            size = 1;
        }};

        titaniumConveyor = new Conveyor("titanium-conveyor") {{
            requirements(Category.distribution, ItemStack.with(Items.copper, 2, Items.titanium, 1));
            health = 20;
            size = 1;
            speed = 10 / 60f / 2f;
            itemCapacity = 2;
            displayedSpeed = 10f;

            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            consumePower(1 / 60f);
        }};

        titaniumConduit = new ArmoredConduit("titanium-conduit"){{
            requirements(Category.liquid, ItemStack.with(EIItems.krynite, 1, EIItems.titanium, 1));
            size = 1;
            botColor = Pal.darkestMetal;
            leaks = true;
            liquidCapacity = 20f;
            liquidPressure = 2f;
            health = 20;
        }};

        kryniteSmelter = new GenericCrafter("krynite-smelter"){{
            requirements(Category.crafting, ItemStack.with(EIItems.titanium, 40, EIItems.copper, 50));
            craftEffect = Fx.smeltsmoke;
            outputItem = new ItemStack(EIItems.krynite, 1);
            craftTime = 3 * 60;
            itemCapacity = 20;
            size = 2;
            hasPower = true;
            hasLiquids = false;
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.07f;
            health = 160;

            consumeItems(ItemStack.with(EIItems.copper, 3, EIItems.titanium, 1));
            consumePower(80 / 60f);
        }};

        satellitesSystemAccessorBlock = new SatellitesSystemAccessorBlock("ssab"){{
            requirements(Category.effect, BuildVisibility.debugOnly, ItemStack.with(EIItems.titanium, 40, EIItems.copper, 50));
            size = 4;
            health = 400;
        }};
    }
}

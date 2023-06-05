package f5of.ei.railroad;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Reflect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;

public class UnformedBlock extends Block {
    public final Seq<Point2> blockSchema = new Seq<>();

    public UnformedBlock(String name) {
        super(name);
        blockSchema.add(new Point2(-1, -1), new Point2(1, 1));
    }

    static void setupTile(Tile tile, Building building) {
        Reflect.set(tile, "changing", true);

        if(building.block.isStatic() || Reflect.<Block>get(tile, "block").isStatic()){
            tile.recache();
            tile.recacheWall();
        }

        Reflect.invoke(tile, "preChanged");

        Reflect.set(tile, "block", building.block);

        tile.build = building;

        Reflect.invoke(tile, "changed");
        Reflect.set(tile, "changing", false);
    }

    public class UnformedBuild extends Building {
        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            Building out = super.init(tile, team, shouldAdd, rotation);
            blockSchema.each(p -> {
                setupTile(tile.nearby(p.x, p.y), this);
            });
            updateProximity();
            return out;
        }

        @Override
        public void updateProximity() {
            if (removing)
                return;
            proximity.clear();
            blockSchema.each(p -> {
                for(Point2 edge : Edges.getEdges(size)){
                    Building other = tile.nearby(edge.x, edge.y).build;
                    if(other != null && other != this){
                        proximity.add(other);
                    }
                }
            });
            onProximityAdded();
            onProximityUpdate();
            proximity.each(Building::onProximityUpdate);
        }

        @Override
        public void removeFromProximity() {
            onProximityRemoved();

        }

        private boolean removing = false;
        @Override
        public void remove() {
            super.remove();
            if (!removing) {
                removing = true;
                blockSchema.each(p -> {
                    tile.nearby(p.x, p.y).remove();
                });
                removing = false;
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.color(Pal.regen);
            blockSchema.each(p -> {
                Lines.rect(p.x * 8 - 4 + x, p.y * 8 - 4 + y, 8, 8);
            });
            Draw.color(Pal.ammo);
            proximity.each(b -> {
                Lines.rect(b.x, b.y, 8, 8);
            });
            Draw.color();
        }
    }
}

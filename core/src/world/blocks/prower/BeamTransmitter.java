package world.blocks.prower;

import arc.math.geom.Geometry;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;

public class BeamTransmitter extends BeamNode {
    public BeamTransmitter(String name) {
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        for(int i = 0; i < 4; i++){
            int maxLen = range + size/2;
            Building dest = null;
            var dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;
            int offset = size/2;
            for(int j = 1 + offset; j <= range + offset; j++){
                var other = Vars.world.build(x + j * dir.x, y + j * dir.y);

                if (other == null) continue;
                if (!(other.block instanceof BeamNode)) continue;

                if(other.isInsulated()){
                    break;
                }

                if(other.block.hasPower && other.team == Vars.player.team() && !(other.block instanceof PowerNode)){
                    maxLen = j;
                    dest = other;
                    break;
                }
            }

            Drawf.dashLine(Pal.placing,
                    x * Vars.tilesize + dx * (Vars.tilesize * size / 2f + 2),
                    y * Vars.tilesize + dy * (Vars.tilesize * size / 2f + 2),
                    x * Vars.tilesize + dx * maxLen * Vars.tilesize,
                    y * Vars.tilesize + dy * maxLen * Vars.tilesize
            );

            if(dest != null){
                Drawf.square(dest.x, dest.y, dest.block.size * Vars.tilesize/2f + 2.5f, 0f);
            }
        }
    }

    public class BeamTransmitterBuild extends BeamNodeBuild {
        @Override
        public void updateDirections() {
            for(int i = 0; i < 4; i ++){
                var prev = links[i];
                var dir = Geometry.d4[i];
                links[i] = null;
                dests[i] = null;
                int offset = size/2;
                for(int j = 1 + offset; j <= range + offset; j++){
                    var other = Vars.world.build(tile.x + j * dir.x, tile.y + j * dir.y);

                    if (other == null) continue;
                    if (!(other.block instanceof BeamNode)) continue;

                    if(other.isInsulated()) break;

                    if(other.block.hasPower && other.team == team && !(other.block instanceof PowerNode)){
                        links[i] = other;
                        dests[i] = Vars.world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
                        break;
                    }
                }

                var next = links[i];

                if(next != prev){
                    if(prev != null){
                        prev.power.links.removeValue(pos());
                        power.links.removeValue(prev.pos());

                        PowerGraph newgraph = new PowerGraph();
                        newgraph.reflow(this);

                        if(prev.power.graph != newgraph){
                            PowerGraph og = new PowerGraph();
                            og.reflow(prev);
                        }
                    }

                    if(next != null){
                        power.links.addUnique(next.pos());
                        next.power.links.addUnique(pos());

                        power.graph.addGraph(next.power.graph);
                    }
                }
            }
        }
    }
}
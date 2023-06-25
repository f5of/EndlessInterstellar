package f5of.ei.satellites.parts;

import arc.scene.ui.layout.Table;
import f5of.ei.satellites.Satellite;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

public abstract class SatellitePart {
    public String name;
    public Satellite owner;
    public boolean enabled = true;

    public SatellitePart(String n, Satellite o) {
        name = n;
        owner = o;
    }

    public void update() {

    }

    public void draw() {

    }

    public String getListText() {
        return name;
    }

    public void buildList(Table table, Runnable runnable) {
        table.button(getListText(), Styles.nonet, runnable);
    }

    public void buildConfig(Table table) {
        table.add(getListText());
        table.row();
    }

    public void saveConfig() {

    }
}

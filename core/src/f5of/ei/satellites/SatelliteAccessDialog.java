package f5of.ei.satellites;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.scene.Element;
import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import f5of.ei.satellites.parts.SatellitePart;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class SatelliteAccessDialog extends BaseDialog {
    public Satellite satellite;

    public SatellitePart part;
    public Table partConfig;

    public SatelliteAccessDialog() {
        super("@satelliteacessdialog");

        shown(this::build);
        hidden(() -> {
            destroyPartConfig();
            destroy();
        });
    }

    public void buildPartConfig() {
        part.buildConfig(partConfig);
    }

    public void savePartConfig() {
        if (part != null)
            part.saveConfig();
    }

    public void destroyPartConfig() {
        partConfig.clear();
    }

    public void build() {
        addCloseButton(200);
    }

    public Dialog show(Satellite s) {
        satellite = s;
        return super.show();
    }

    public void destroy() {
        cont.clear();
    }
}

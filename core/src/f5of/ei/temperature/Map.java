package f5of.ei.temperature;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.PixmapTextureData;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Buffers;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Tex;
import mindustry.graphics.Shaders;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class Map {
    public static Map currentDrawingInstance;

    //public FrameBuffer buffer = new FrameBuffer();
    public Seq<Cons<Map>> updateRuns = new Seq<>();

    public float sclX, sclY;

    public ByteBuffer buf;

    public Texture temperatureMap;
    public PixmapRegion circle;

    public Map() {
        currentDrawingInstance = this;
        Events.on(EventType.WorldLoadEndEvent.class, event -> {
            temperatureMap = new Texture(new PixmapTextureData(new Pixmap(Vars.world.width(), Vars.world.height()),
                    false, false));
        });
        Events.run(EventType.Trigger.update, () -> {
            if (Vars.state.isGame())
                update();
        });
        Events.run(EventType.Trigger.drawOver, () -> {
            if (Vars.state.isGame())
                draw();
        });
    }

    public static void update(Cons<Map> run) {
        currentDrawingInstance.updateRuns.add(run);
    }

    public void update() {
        if (circle == null) {
            TextureRegion reg = Core.atlas.find("circle-shadow");
            circle = new PixmapRegion(reg.texture.getTextureData().getPixmap(),
                    reg.getX(), reg.getY(),
                    reg.width, reg.height);
        }

        update((t) -> {
            t.temperatureMap.getTextureData().getPixmap().draw(circle.pixmap, 0, 0);
        });

        temperatureMap.getTextureData().getPixmap().fillRect(0, 0, 500, 500, Color.yellow.rgba8888());
        updateRuns.each(r -> {
            r.get(this);
        });
        updateRuns.clear();
    }

    public void draw() {
        Draw.rect(new TextureRegion(temperatureMap), Vars.player.x, Vars.player.y);
    }

    void colorAt(int x, int y) {
        byte r = buf.get((x * 50 + y) * 4);
        byte g = buf.get((x * 50 + y) * 4 + 1);
        byte b = buf.get((x * 50 + y) * 4 + 2);
        byte a = buf.get((x * 50 + y) * 4 + 3);
        Log.info("Color: @ @ @ @", r, g, b, a);
    }

    // TODO remove debug tools
    static void printClass(Object o, Class<?> clazz) {
        if (clazz == null) return;
        Log.info("Logging object @ with class @", o, clazz.getCanonicalName());
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Log.info("@ @ = @;", field.getType().getCanonicalName(), field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        printClass(o, clazz.getSuperclass());
    }
}

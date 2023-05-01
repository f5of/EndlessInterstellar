package files;

import arc.assets.loaders.TextureLoader;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.graphics.PixmapIO;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Strings;
import arc.util.io.Reads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EIAtlasLoader {
    private static ObjectMap<String, Object> parameters;
    public static PixmapIO.PngReader reader = new PixmapIO.PngReader();

    /**Atlas structure:
     * <ul>
     *     <li>Header: flipX (bool), flipY (bool), images number (int), texture regions number (int)</li>
     *     <li>Images: [image size (int), width (int), height (int), <br>
     *     [image byte (byte)] * image size <br>
     *     ] * images number</li>
     *     <li>Texture regions: [ x (int), y (int), w (int), h (int), texture id (int), region name (UTF-8 String) <br>
     *     ] * texture regions number</li>
     * </ul>
     * **/
    public static ObjectMap<String, TextureRegion> loadAtlas(Fi atlasFile) {
        parameters = new ObjectMap<>();
        ObjectMap<Integer, Texture> textures = new ObjectMap<>();
        ObjectMap<String, TextureRegion> regions = new ObjectMap<>();

        if (!atlasFile.exists()) throw new RuntimeException(Strings.format("Atlas file @ not exists.", atlasFile));
        Reads reads = atlasFile.reads();

        param("flipX", reads.bool());
        param("flipY", reads.bool());
        param("images", reads.i());
        param("regions", reads.i());

        for (int i = 0; i < (int) param("images"); i++) {
            int imageSize = reads.i();
            byte[] image = reads.b(imageSize);

            ByteArrayInputStream input = new ByteArrayInputStream(image);
            Texture texture = null;
            try {
                ByteBuffer buffer = reader.read(input);
                texture = new Texture(new Pixmap(buffer, reader.width, reader.height));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.info("Putted texture at " + i);
            textures.put(i, texture);
        }

        for (int i = 0; i < (int) param("regions"); i++) {
            int x = reads.i();
            int y = reads.i();
            int w = reads.i();
            int h = reads.i();
            int textureID = reads.i();
            String name = reads.str();

            Log.info(name + " uses texture with id " + textureID);
            Log.info("x: @     y: @", x, y);
            TextureRegion region = new TextureRegion(textures.get(textureID), x, y, w, h);
            region.flip(param("flipX"), param("flipY"));
            regions.put(name, region);
        }

        reads.close();

        return regions;
    }

    private static <T> T param(String p) {
        return (T) parameters.get(p);
    }

    private static <T> void param(String p, T v) {
        parameters.put(p, v);
    }
}

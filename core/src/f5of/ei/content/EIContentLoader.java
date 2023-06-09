package f5of.ei.content;

import arc.Events;
import arc.struct.Seq;
import f5of.ei.files.loads.core.Loads;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.game.EventType;

public class EIContentLoader {
    public static Seq<Content> eiContent = new Seq<>();

    public static void load() {
        Seq<Content> before = createContentList();

        EILiquids.load();
        EIItems.load();
        EIBlocks.load();
        EIPlanets.load();

        EITechTree.load();

        Seq<Content> after = createContentList();
        after.each(c -> {
            if (!before.contains(c))
                eiContent.add(c);
        });

        Events.on(EventType.ClientLoadEvent.class, e -> {
            eiContent.each(EIContentLoader::handleContent);
        });
        Events.on(EventType.ClientLoadEvent.class, e -> {
            eiContent.each(Loads::load);
        });
    }

    public static <T extends Content> T handleContent(T content) {
        return content;
    }

    static Seq<Content> createContentList() {
        Seq<Content> out = new Seq<>();
        for (Seq<Content> contents : Vars.content.getContentMap()) {
            contents.each(out::add);
        }
        return out;
    }
}

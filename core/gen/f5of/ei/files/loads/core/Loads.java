package f5of.ei.files.loads.core;

import mindustry.ctype.Content;

public class Loads {
  public static void load(Content content) {
    if (content instanceof f5of.ei.world.blocks.power.BeamTransmitter c) {
      c.region = arc.Core.atlas.find(("@-reg").replaceAll("@", c.name));}
  }
}

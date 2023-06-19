package f5of.ei.campaign;

import arc.util.Threads;
import arc.util.Time;

public class CampaignController {
    public final Thread controllerThread;

    private transient boolean run = false;
    private long prev = 0;
    private final int frequency;
    private final float delay;

    private transient float delta;

    public CampaignController() {
        frequency = 2;
        delay = 1000f / frequency;

        controllerThread = Threads.daemon(() -> {
            while (true) {
                if (Time.timeSinceMillis(prev) < delay) {
                    try {
                        Thread.sleep((long) (delay - Time.timeSinceMillis(prev)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (run)
                    this.update();
            }
        });
    }

    public void start() {
        run = true;
    }

    public void stop() {
        run = false;
    }

    public void update() {
        delta = prev / delay;
        prev = Time.millis();
    }
}

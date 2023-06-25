package f5of.ei.controllers;

import arc.util.Log;
import arc.util.Threads;
import arc.util.Time;

public abstract class GenericController {
    public final Thread controllerThread;

    private volatile boolean run = false;
    private long prev = 0;
    private final float frequency;
    private final float delay;

    public volatile float delta;

    public GenericController(float frequency) {
        this.frequency = frequency;
        delay = 1000f / frequency;

        controllerThread = Threads.daemon(() -> {
            try {
                while (true) {
                    long dt = Time.timeSinceMillis(prev);
                    if (dt < delay) {
                        Thread.sleep((long) (delay - dt));
                    }
                    delta = Time.timeSinceMillis(prev) / delay * (60 / frequency);
                    prev = Time.millis();
                    if (run) {
                        this.update();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void start() {
        run = true;
    }

    public void stop() {
        run = false;
    }

    public abstract void update();
}

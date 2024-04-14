import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
    private int count = 1;

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "CustomThread-" + count);
        count++;
        return t;
    }
}



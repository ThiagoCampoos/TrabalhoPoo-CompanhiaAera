package comum;

import java.time.LocalDateTime;



public class SystemClock {
    
    private static SystemClock instance;

    public SystemClock() {}

    public static SystemClock getInstance() {
        if (instance == null) {
            instance = new SystemClock();
        }
        return instance;
    }

    public LocalDateTime today() {
        return LocalDateTime.now();
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
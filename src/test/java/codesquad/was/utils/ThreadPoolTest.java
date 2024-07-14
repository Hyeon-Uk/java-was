package codesquad.was.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadPoolTest {

    private ThreadPool threadPool;

    @BeforeEach
    public void setUp() {
        threadPool = new ThreadPool();
    }

    @AfterEach
    public void tearDown() {
        threadPool.shutDown();
    }

    @Test
    public void testExecute() throws InterruptedException {
        // given
        AtomicInteger integer = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        Runnable task = () -> {
            integer.incrementAndGet();
            latch.countDown();
        };

        // when
        threadPool.execute(task);
        latch.await();

        // then
        assertEquals(1,integer.get());
    }

    @Test
    public void testMultipleTasks() throws InterruptedException {
        // given
        int numberOfTasks = 10;
        CountDownLatch latch = new CountDownLatch(numberOfTasks);
        AtomicInteger integer = new AtomicInteger(0);
        Runnable task = () -> {
           integer.incrementAndGet();
           latch.countDown();
        };

        // when
        for (int i = 0; i < numberOfTasks; i++) {
            threadPool.execute(task);
        }
        latch.await();

        // then
        assertEquals(numberOfTasks,integer.get());
    }

    @Test
    public void testShutDown() throws InterruptedException {
        // given
        Runnable task = () -> {
            try {
                // Simulate some work
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // when
        threadPool.execute(task);
        threadPool.shutDown();

        // then
        assertTrue(threadPool.isShutDown(), "ExecutorService should be shut down");
    }

    @Test
    public void testShutDownNow() {
        // given
        CountDownLatch latch = new CountDownLatch(1);
        Runnable task = () -> {
            try {
                // Simulate some work
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        // when
        threadPool.execute(task);
        threadPool.shutDownNow();

        // then
        assertTrue(threadPool.isShutDown());
    }

    @Test
    public void testExceedThreadPoolSize() throws InterruptedException {
        // given
        int numberOfTasks = 50 * 2;
        CountDownLatch latch = new CountDownLatch(numberOfTasks);
        AtomicInteger integer = new AtomicInteger(0);
        Runnable task = () -> {
            integer.incrementAndGet();
            latch.countDown();
        };

        // when
        for (int i = 0; i < numberOfTasks; i++) {
            threadPool.execute(task);
        }

        latch.await();

        // then
        assertEquals(50*2,integer.get());
    }
}

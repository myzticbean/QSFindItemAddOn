package io.myzticbean.finditemaddon.utils.async;

import io.myzticbean.finditemaddon.utils.log.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadScheduler {
    // Use a shared executor for all virtual-thread tasks
    private static final ExecutorService VIRTUAL_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    private VirtualThreadScheduler() {
        // prevent instantiation
    }

    /**
     * Run a task asynchronously on a Java Virtual Thread.
     *
     * @param task The task to run.
     * @return a Future representing the result of the task.
     */
    public static Future<?> runTaskAsync(Runnable task) {
        return VIRTUAL_EXECUTOR.submit(task);
    }

    /**
     * Run a task asynchronously on a Java Virtual Thread with result.
     *
     * @param task The task to run.
     * @param <T>  The type of the result.
     * @return a Future with the result.
     */
    public static <T> Future<T> runTaskAsync(java.util.concurrent.Callable<T> task) {
        return VIRTUAL_EXECUTOR.submit(task);
    }

    /**
     * Shut down the virtual thread executor gracefully.
     */
    public static void shutdown() {
        Logger.logInfo("Shutting down virtual thread executor...");
        VIRTUAL_EXECUTOR.shutdown();
    }
}

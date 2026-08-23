package io.myzticbean.finditemaddon.utils.async;

import io.myzticbean.finditemaddon.utils.log.Logger;

import lombok.experimental.UtilityClass;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@UtilityClass
public class VirtualThreadScheduler {
    // Use a shared executor for all virtual-thread tasks
    private static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

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

    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(supplier, VIRTUAL_EXECUTOR);
    }

    /**
     * Run a task asynchronously on a Java Virtual Thread, returning a CompletableFuture for chaining.
     *
     * @param task The task to run.
     * @return a CompletableFuture that completes when the task finishes.
     */
    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task, VIRTUAL_EXECUTOR);
    }

    /**
     * The shared virtual-thread executor, for use as the explicit executor argument to
     * {@link CompletableFuture} continuations. Without it, a continuation runs on whichever thread
     * completed the upstream stage - which for QuickShop's async APIs is a region thread or a
     * database thread.
     *
     * @return the virtual-thread executor
     */
    public static Executor executor() {
        return VIRTUAL_EXECUTOR;
    }

    /**
     * Shut down the virtual thread executor gracefully.
     */
    public static void shutdown() {
        Logger.logInfo("Shutting down virtual thread executor...");
        VIRTUAL_EXECUTOR.shutdown();
    }
}

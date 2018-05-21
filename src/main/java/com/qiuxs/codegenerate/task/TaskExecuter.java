package com.qiuxs.codegenerate.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class TaskExecuter {

	private static ExecutorService thread_pool = Executors.newCachedThreadPool();

	private static Service<Boolean> builderService = new TableBuilderService();

	@SuppressWarnings("unchecked")
	public static <T> Future<T> executeTask(Task<T> callable) {
		return (Future<T>) thread_pool.submit(callable);
	}

	public static void startBuilder(EventHandler<WorkerStateEvent> onBuilderFinishHandler) {
		State state = builderService.getState();
		builderService.setOnCancelled(onBuilderFinishHandler);
		builderService.setOnFailed(onBuilderFinishHandler);
		builderService.setOnReady(onBuilderFinishHandler);
		builderService.setOnSucceeded(onBuilderFinishHandler);
		if (state != State.READY) {
			builderService.reset();
		}
		builderService.start();
	}

	public static void cancelBuild() {
		builderService.cancel();
	}
}

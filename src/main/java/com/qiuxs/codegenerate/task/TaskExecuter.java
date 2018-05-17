package com.qiuxs.codegenerate.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.concurrent.Task;

public class TaskExecuter {

	private static ExecutorService thread_pool = Executors.newCachedThreadPool();

	@SuppressWarnings("unchecked")
	public static <V, T extends TaskResult<V>> Future<T> executeTask(Task<T> callable) {
		return (Future<T>) thread_pool.submit(callable);
	}
}

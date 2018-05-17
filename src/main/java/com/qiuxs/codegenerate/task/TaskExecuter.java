package com.qiuxs.codegenerate.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskExecuter {

	private static ExecutorService thread_pool = Executors.newCachedThreadPool();

	public static <V, T extends TaskResult<V>> Future<T> executeTask(Callable<T> callable) {
		return thread_pool.submit(callable);
	}
}

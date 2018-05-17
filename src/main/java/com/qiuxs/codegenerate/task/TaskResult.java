package com.qiuxs.codegenerate.task;

public class TaskResult<T> {

	private boolean successFlag = false;
	private T data;
	private String msg;
	private Exception e;

	public TaskResult() {
	}

	public TaskResult(boolean successFlag, T data, String msg, Exception e) {
		super();
		this.successFlag = successFlag;
		this.data = data;
		this.msg = msg;
		this.e = e;
	}

	public static <T> TaskResult<T> makeSuccess(T date, String msg) {
		TaskResult<T> res = new TaskResult<T>();
		res.setData(date);
		res.setMsg(msg);
		res.setSuccessFlag(true);
		return res;
	}

	public static <T> TaskResult<T> makeError(String msg) {
		TaskResult<T> res = new TaskResult<T>();
		res.setMsg(msg);
		res.setSuccessFlag(false);
		return res;
	}

	public static <T> TaskResult<T> makeException(Exception e) {
		TaskResult<T> res = new TaskResult<T>();
		res.setMsg(e.getLocalizedMessage());
		res.setSuccessFlag(false);
		return res;
	}

	public boolean isSuccessFlag() {
		return successFlag;
	}

	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Exception getE() {
		return e;
	}

	public void setE(Exception e) {
		this.e = e;
	}

}

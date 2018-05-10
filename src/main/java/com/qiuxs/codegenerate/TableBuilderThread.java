package com.qiuxs.codegenerate;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.qiuxs.codegenerate.context.CodeTemplateContext;
import com.qiuxs.codegenerate.model.TableModel;

public class TableBuilderThread extends Thread {

	private CountDownLatch latch;

	public TableBuilderThread(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void run() {
		List<TableModel> tableModels = CodeTemplateContext.getAllBuildTableModels();
		for (TableModel tm : tableModels) {
			System.out.println(tm.getClassName());
		}
		this.latch.countDown();
	}

}

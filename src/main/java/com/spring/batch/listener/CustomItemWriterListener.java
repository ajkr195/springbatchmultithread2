package com.spring.batch.listener;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import com.spring.batch.model.Sales;
@Component
public class CustomItemWriterListener implements ItemWriteListener<Sales> {

	@Override
	public void beforeWrite(List<? extends Sales> items) {
		System.err.println("ItemWriteListener - beforeWrite");
	}

	@Override
	public void afterWrite(List<? extends Sales> items) {
		System.err.println("ItemWriteListener - afterWrite");
	}

	@Override
	public void onWriteError(Exception exception, List<? extends Sales> items) {
		System.err.println("ItemWriteListener - onWriteError");
	}

}
package com.spring.batch.listener;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

import com.spring.batch.model.Sales;
@Component
public class CustomItemReaderListener implements ItemReadListener<Sales> {

	@Override
	public void beforeRead() {
		System.err.println("ItemReadListener - beforeRead");
	}

	@Override
	public void afterRead(Sales item) {
		System.err.println("ItemReadListener - afterRead");
	}

	@Override
	public void onReadError(Exception ex) {
		System.err.println("ItemReadListener - onReadError");
	}
	
}
	
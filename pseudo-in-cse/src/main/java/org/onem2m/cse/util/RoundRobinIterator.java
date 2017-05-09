package org.onem2m.cse.util;

import java.util.Iterator;
import java.util.List;

public class RoundRobinIterator<T> implements Iterator<T> {

	private final List<T> list;
	private final int listSize;
	private final Object lock = new Object();
	private int index = 0;
	
	public RoundRobinIterator(List<T> list) {
		super();
		this.list = list;
		this.listSize = list.size();
	}
	
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public T next() {
		final T result;
		synchronized (lock) {
			result = list.get(index);
			index = (index + 1) % listSize;
		}
		return result;
	}

}

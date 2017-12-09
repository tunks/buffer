package dev.tunks.buffer;

import java.util.Observer;

public interface BaseDataStream<T> extends Observer {
   public T get();
   public void remove(T object);
}

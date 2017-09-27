package no.stelar7.api.l4j8.basic.utils;

import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.function.Function;

public class LazyList<T> extends ArrayList<T>
{
    private static final long serialVersionUID = -1691718732059707951L;
    
    private           int                        lastValue;
    private           int                        increment;
    private transient Function<Integer, List<T>> makerOfMoreData;
    private boolean hasMore = true;
    
    public LazyList(int increment, Function<Integer, List<T>> maker)
    {
        this.makerOfMoreData = maker;
        this.increment = increment;
    }
    
    public boolean loadMoreData()
    {
        List<T> more = makerOfMoreData.apply(lastValue);
        lastValue += increment;
        
        if (more.isEmpty())
        {
            return (hasMore = false);
        }
        
        this.addAll(more);
        return true;
    }
    
    
    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return new LazyListIterator(-1);
    }
    
    @NotNull
    @Override
    public ListIterator<T> listIterator()
    {
        return new LazyListIterator(-1);
    }
    
    @NotNull
    @Override
    public ListIterator<T> listIterator(int index)
    {
        return new LazyListIterator(index);
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }
        
        LazyList<?> lazyList = (LazyList<?>) o;
        
        if (lastValue != lazyList.lastValue)
        {
            return false;
        }
        if (increment != lazyList.increment)
        {
            return false;
        }
        return (makerOfMoreData != null) ? makerOfMoreData.equals(lazyList.makerOfMoreData) : (lazyList.makerOfMoreData == null);
    }
    
    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + lastValue;
        result = 31 * result + increment;
        result = 31 * result + (makerOfMoreData != null ? makerOfMoreData.hashCode() : 0);
        return result;
    }
    
    @Override
    public T get(int index)
    {
        return listIterator(index - 1).next();
    }
    
    
    private class LazyListIterator implements ListIterator<T>
    {
        private int index = -1;
        private int size  = size();
        
        public LazyListIterator(int startIndex)
        {
            this.index = startIndex;
            
            while (startIndex >= size && LazyList.this.loadMoreData())
            {
                size = size();
            }
        }
        
        @Override
        public boolean hasNext()
        {
            if (index + 1 < size)
            {
                return true;
            }
            
            if (loadMoreData())
            {
                size = size();
                return true;
            }
            
            return false;
        }
        
        @Override
        public T next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            
            return LazyList.super.get(++index);
        }
        
        @Override
        public boolean hasPrevious()
        {
            return index >= 1;
        }
        
        @Override
        public T previous()
        {
            if (!hasPrevious())
            {
                throw new NoSuchElementException();
            }
            
            return LazyList.super.get(--index);
        }
        
        @Override
        public int nextIndex()
        {
            return index + 1;
        }
        
        @Override
        public int previousIndex()
        {
            return index - 1;
        }
        
        @Override
        public void remove()
        {
            if (index > 0)
            {
                LazyList.this.remove(index);
            }
        }
        
        @Override
        public void set(T t)
        {
            if (index > 0)
            {
                LazyList.this.set(index, t);
            }
        }
        
        @Override
        public void add(T t)
        {
            LazyList.this.add(index, t);
        }
    }
    
    @Override
    public String toString()
    {
        return "LazyList{" +
               "hasMore=" + hasMore +
               ", currentValue=" + lastValue +
               ", increment=" + increment +
               '}';
    }
}
package other;

import com.nickrobison.tuple.unsafe.Coterie;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Taken from: http://www.javacodegeeks.com/2013/07/lock-less-java-object-pool.html
 *
 */
public class FastObjectPool<T> {

    private Holder<T>[] objects;

    private volatile int takePointer;
    private int releasePointer;

    private final int mask;
    private final long BASE;
    private final long INDEXSCALE;
    private final long ASHIFT;

    public ReentrantLock lock = new ReentrantLock();
    private ThreadLocal<Holder<T>> localValue = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    public FastObjectPool(PoolFactory<T> factory , int size)
    {

        int newSize=1;
        while(newSize<size)
        {
            newSize = newSize << 1;
        }
        size = newSize;
        objects = new Holder[size];
        for(int x=0;x<size;x++)
        {
            objects[x] = new Holder<>(factory.create());
        }
        mask = size-1;
        releasePointer = size;
        BASE = Coterie.arrayBaseOffset(Holder[].class);
        INDEXSCALE = Coterie.arrayIndexScale(Holder[].class);
        ASHIFT = 31L - Integer.numberOfLeadingZeros((int) INDEXSCALE);
    }

    public Holder<T> take()
    {
        int localTakePointer;

        Holder<T> localObject = localValue.get();
        if(localObject!=null)
        {
            if(localObject.state.compareAndSet(Holder.FREE, Holder.USED))
            {
                return localObject;
            }
        }

        while(releasePointer != (localTakePointer=takePointer) )
        {
            int index = localTakePointer & mask;
            Holder<T> holder = objects[index];
            //if(holder!=null && Coterie.compareAndSwapObject(objects, (index*INDEXSCALE)+BASE, holder, null))
            if(holder!=null && Coterie.compareAndSwapObject(objects, (index<<ASHIFT)+BASE, holder, null))
            {
                takePointer = localTakePointer+1;
                if(holder.state.compareAndSet(Holder.FREE, Holder.USED))
                {
                    localValue.set(holder);
                    return holder;
                }
            }
        }
        return null;
    }

    public void release(Holder<T> object) throws InterruptedException
    {
        lock.lockInterruptibly();
        try{
            int localValue=releasePointer;
            //long index = ((localValue & mask) * INDEXSCALE ) + BASE;
            long index = ((localValue & mask)<<ASHIFT ) + BASE;
            if(object.state.compareAndSet(Holder.USED, Holder.FREE))
            {
                Coterie.putOrderedObject(objects, index, object);
                releasePointer = localValue+1;
            }
            else
            {
                throw new IllegalArgumentException("Invalid reference passed");
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    public static class Holder<T> {
        private T value;
        public static final int FREE = 0;
        public static final int USED = 1;

        private AtomicInteger state = new AtomicInteger(FREE);

        public Holder(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public interface PoolFactory<T> {
        T create();
    }
}

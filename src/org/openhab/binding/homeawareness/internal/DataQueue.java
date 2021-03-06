package org.openhab.binding.homeawareness.internal;

import java.util.LinkedList;

public class DataQueue
{
    private LinkedList<Integer> queue = new LinkedList<Integer>();

    public synchronized  void add(Integer element)
    {
        queue.add(element);
        notifyAll();
    }

    public synchronized Integer remove()
    {
        while (isEmpty())
        {
            try
            {
                wait();
            } catch (InterruptedException iExc) {
                return null;
            }
        }

        return queue.remove();
    }

    public synchronized boolean isEmpty()
    {
        return queue.isEmpty();
    }

    public void clear()
    {
        queue.clear();
    }
}

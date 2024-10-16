package program;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AmigoSet<E> extends AbstractSet<E> implements Serializable,Cloneable, Set<E> {

    private static final Object PRESENT = new Object(); //це наша заглушка

    private transient HashMap<E,Object> map; //список ключів буде нашим сетом,
                                             // а замість значень будемо вставляти в мапу нашу заглушку


    public AmigoSet() {
        this.map = new HashMap<>();
    }

    public AmigoSet (Collection<? extends E> collection){
        this.map = new HashMap<>(Math.max((int)(collection.size()/.75f)+1,16));
        addAll(collection);
    }

    public boolean add (E e){
        if (!map.containsKey(e)){
            map.put(e,PRESENT);
            return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean remove(Object o) {
        return  map.remove(o)==PRESENT;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            AmigoSet<E> newSet = (AmigoSet<E>) super.clone();
            newSet.map = (HashMap<E, Object>) map.clone();
            return newSet;
        } catch (Exception e){
            throw  new InternalError();
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(HashMapReflectionHelper.<Integer>callHiddenMethod(map,"capacity"));
        out.writeFloat(HashMapReflectionHelper.<Float>callHiddenMethod(map,"loadFactor"));
        out.writeInt(map.size());
        for (E e: map.keySet()) {
            out.writeObject(e);
        }

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        int capacity = in.readInt();
        float loadFactor = in.readFloat();
        map = new HashMap<>(capacity,loadFactor);
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            E e = (E) in.readObject();
            map.put(e,PRESENT);
        }
    }

}


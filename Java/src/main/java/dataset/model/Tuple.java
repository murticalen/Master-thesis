package main.java.dataset.model;

import java.util.Objects;

public class Tuple<T> {
    
    private T v1;
    private T v2;
    
    public Tuple(T v1, T v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    public T getV1() {
        return v1;
    }
    
    public T getV2() {
        return v2;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tuple<?> tuple = (Tuple<?>)o;
        return Objects.equals(v1, tuple.v1) &&
                Objects.equals(v2, tuple.v2);
    }
    
    @Override
    public int hashCode() {
        
        return Objects.hash(v1, v2);
    }
    
    @Override
    public String toString() {
        return "(" + v1.toString() + ", " + v2.toString() + ")";
    }
}

package main.java.generator.duration;

import java.util.Random;

public abstract class AbstractCallDuration {

    protected static Random random = new Random();

    public abstract int getCallDuration();

}

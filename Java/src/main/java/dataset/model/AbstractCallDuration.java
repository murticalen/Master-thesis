package main.java.dataset.model;

import java.util.Random;

public abstract class AbstractCallDuration {

    protected Random random = new Random();

    public abstract int getCallDuration();

}

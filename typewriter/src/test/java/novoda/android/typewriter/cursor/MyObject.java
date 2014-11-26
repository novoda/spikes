package novoda.android.typewriter.cursor;

import novoda.android.typewriter.annotation.Mapper;

public class MyObject {

    @Mapper("some_value")
    public String test2;

    public String test;
    public float myFloat;
    public int myInt;

    @Mapper("_id")
    public long id;

    public void setMyFloat(float myFloat) {
        this.myFloat = myFloat;
    }

    public void setMyInt(int myInt) {
        this.myInt = myInt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTest(String hello) {
        this.test = hello;
    }

    public void setTest2(String hello) {
        this.test2= hello;
    }
}

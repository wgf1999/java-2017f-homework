package test;

import org.junit.Test;
import ui.Field;

import static org.junit.Assert.*;

public class Test {
    @Test
    public void charge() throws Exception {
        Field field = new Field();
        field.initWorld();
        Huluwa huluwa = field.huluwa.get(0);
        Yaoguai yg = figure.charge();
        boolean t = true;
        assertEquals(t,temp.isLive());
        assertNotEquals("class Huluwa",temp.getClass());
        assertNotEquals("class Yaoguai",temp.getClass());
    }

}
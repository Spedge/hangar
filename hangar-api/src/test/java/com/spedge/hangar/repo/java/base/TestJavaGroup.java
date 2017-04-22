package com.spedge.hangar.repo.java.base;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestJavaGroup
{
    @Test
    public void testJavaDotKeyGav()
    {
        JavaGroup jg = JavaGroup.dotDelimited("org.test.these.things");
        
        assertEquals(jg.toString(), "org.test.these.things");
        assertEquals(jg.getGroup(), "org.test.these.things");
        assertEquals(jg.getGroupAsPath(), "org/test/these/things");
        
        List<String> group = new ArrayList<String>();
        group.add("org");
        group.add("test");
        group.add("these");
        group.add("things");
        
        assertEquals(jg.getGroupAsList(), group);  
    }
    
    @Test
    public void testJavaSlashKeyGav()
    {
        JavaGroup jg = JavaGroup.slashDelimited("org/test/these/things");
        
        assertEquals(jg.toString(), "org.test.these.things");
        assertEquals(jg.getGroup(), "org.test.these.things");
        assertEquals(jg.getGroupAsPath(), "org/test/these/things");
        
        List<String> group = new ArrayList<String>();
        group.add("org");
        group.add("test");
        group.add("these");
        group.add("things");
        
        assertEquals(jg.getGroupAsList(), group);  
    }
}

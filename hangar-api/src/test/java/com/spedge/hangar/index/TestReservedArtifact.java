package com.spedge.hangar.index;

import org.junit.Test;

import com.spedge.hangar.testutils.EqualsTester;

public class TestReservedArtifact
{
    @Test
    public void testEqualsAndHashCode() 
    {
        ReservedArtifact ra = new ReservedArtifact("");
        EqualsTester<ReservedArtifact> equalsTester = EqualsTester.newInstance(new ReservedArtifact(""));
        equalsTester.assertEqual( ra, ra );
        equalsTester.assertNotEqual( ra, new ReservedArtifact("") );
    }
}

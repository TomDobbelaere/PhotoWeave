package be.howest.photoweave.model.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tomdo on 30/11/2017.
 */
public class PrimitiveUtilTest {
    @Test
    public void testDecomposeIntToBytes() throws Exception {
        byte[] decomposed = PrimitiveUtil.decomposeIntToBytes(0x01020304);

        assertEquals("First byte of decompose not correct", 1, decomposed[0]);
        assertEquals("Second byte of decompose not correct", 2, decomposed[1]);
        assertEquals("Third byte of decompose not correct", 3, decomposed[2]);
        assertEquals("Fourth byte of decompose not correct", 4, decomposed[3]);
    }

    @Test
    public void testComposeIntFromBytes() throws Exception {
        byte[] decomposedExample = {4, 3, 2, 1};
        int composed = PrimitiveUtil.composeIntFromBytes(decomposedExample);

        assertEquals("Composed integer is not correct", 0x04030201, composed);
    }

    @Test
    public void testModifyInt() throws Exception {
        byte[] decomposed = PrimitiveUtil.decomposeIntToBytes(0x01020304);

        decomposed[0] = 5;
        decomposed[3] = 8;

        int composed = PrimitiveUtil.composeIntFromBytes(decomposed);

        assertEquals("Composed integer is not correct", 0x05020308, composed);
    }
}
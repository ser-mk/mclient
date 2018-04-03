package sermk.pipi.mclient;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ser on 03.04.18.
 */
public class MUtilsTest {
    @Test
    public void md5() throws Exception {
        String res = MUtils.md5("123");
        System.out.println("res :" + res);
        assertEquals(res,"202cb962ac59075b964b07152d234b70");

        res = MUtils.md5("zxc");
        System.out.println("res :" + res);
        assertEquals(res,"5fa72358f0b4fb4f2c5d7de8c9a41846");

        res = MUtils.md5("zxcgkeopjgi4oe5jtg54ujtgnti0g35gug0fg8ub9ugrf98bugrf");
        System.out.println("res :" + res);
        assertEquals(res,"bc8438b66b3e267e8dc4ef989c2110a4");
    }

}
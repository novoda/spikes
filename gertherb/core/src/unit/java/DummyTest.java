import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class DummyTest {

    @Test
    public void testDoesNotExplode() throws Exception {
        Dummy dummy = new Dummy();

        boolean result = dummy.explodes();

        assertThat(result).isFalse();
    }
}

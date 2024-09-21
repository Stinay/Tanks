package Tanks;


import org.junit.jupiter.api.Test;

public class SampleTest {

    @Test
    public void simpleTest() {
        // make a new instance
        App app = new App();
        assert (App.FPS ==30);  
    }

    public void verySimpleTest() {
        App app = new App();
        // to test some methods return no values
        app.setup();
        app.settings();
        assert (app.WIDTH == 864);

    }
}

// gradle test jacocoTestReport
package Tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;

import processing.core.PImage;
import processing.data.JSONObject;

//never combine the test cases

/**
 * Test class for the App class.
 */
public class AppTest {


    // @BeforeEach
    // public void setUp() {
    //     App app = new App();
    //     app.settings();     // Setup is called to initialize the game
    //     app.dispose();
    // }

    @Test
    public void simpleTest() {
        App app = new App();
        assert (app.FPS ==30);  
        assert (app.WIDTH == 864);
        app.dispose();
    }

   
    // @Test
    // public void testResourceLoading() {
    //     PImage testImage = app.loadImage("src/main/resources/Tanks/parachute.png");
    //     assertNotNull(testImage, "Image resource should be loaded correctly.");
    // }

    // @Test
    // public void testConfigurationLoading() {
    //     // Test configuration loading
    //     JSONObject configObj = app.loadJSONObject("config.json");
    //     assertNotNull(configObj, "Configuration JSON object should not be null.");
    // }

    // @AfterEach
    // public void tearDown() {
    //     app.dispose();
    // }
    
}

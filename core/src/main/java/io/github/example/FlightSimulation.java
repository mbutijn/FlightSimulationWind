package io.github.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.example.UIComponents.*;

public class FlightSimulation extends ApplicationAdapter implements InputProcessor {
    private final OrthographicCamera worldCamera, uiCamera;
    private final Viewport worldViewport;
    private final FitViewport uiViewport;
    private ShapeRenderer shape;
    private Aircraft aircraft;
    private boolean paused = false;
    private boolean camFocusAircraft = true;
    private static SteeringMode steeringMode = SteeringMode.AUTO_PILOT;
    private SpriteBatch batch;
    private BackGround backGround;
    private final Air air;
    private Array<FlightDataUI> uiComponents;
    private DataTape altitudeTape, velocityTape;
    private PitchAngleDataUI pitchAngleDataUI;
    private AngleOfAttackDataUI angleOfAttackDataUI;
    private ClimbRateDataUI climbRateDataUI;
    private SteeringModeDataUI steeringModeDataUI;
    private AutoPilotModeDataUI autoPilotModeDataUI;

    public FlightSimulation(){
        worldCamera = new OrthographicCamera();
        worldCamera.position.x = 512;
        worldCamera.position.y = 384;
        worldCamera.zoom = 0.1f;
        worldViewport = new FitViewport(1024, 768, worldCamera);

        uiCamera = new OrthographicCamera();
        uiCamera.position.x = 512;
        uiCamera.position.y = 384;
        uiViewport = new FitViewport(1024, 768, uiCamera);

        air = new Air();
    }

    @Override
    public void create() {
        shape = new ShapeRenderer();
        aircraft = new Aircraft(1000, air);

        batch = new SpriteBatch();
        altitudeTape = new DataTape(aircraft, uiViewport, shape, batch, uiViewport.getWorldWidth() - 110, 0.5f * uiViewport.getWorldHeight() - 150);
        altitudeTape.setProperties(60, 2f, 10);
        climbRateDataUI = new ClimbRateDataUI(aircraft, uiViewport, shape, batch, uiViewport.getWorldWidth() - 200, 85);
        velocityTape = new DataTape(aircraft, uiViewport, shape, batch, 0, 0.5f * uiViewport.getWorldHeight() - 150);
        velocityTape.setProperties(20, 6f, 10);
        pitchAngleDataUI = new PitchAngleDataUI(aircraft,  uiViewport, shape, batch, 0.5f * uiViewport.getWorldWidth() - 37.5f, 0);
        ThrottleDataUI throttleDataUI = new ThrottleDataUI(aircraft, uiViewport, shape, batch, uiViewport.getWorldWidth() - 340, 85);
        angleOfAttackDataUI = new AngleOfAttackDataUI(aircraft, uiViewport, shape, batch, 100, 75);
        ElevatorDataUI elevatorDataUI = new ElevatorDataUI(aircraft, uiViewport, shape, batch, 300, 75);
        SpeedDataUI speedDataUI = new SpeedDataUI(aircraft, uiViewport, shape, batch, 20, 0.5f * uiViewport.getWorldHeight() - 160);
        steeringModeDataUI = new SteeringModeDataUI(aircraft, uiViewport, shape, batch, 0.5f * uiViewport.getWorldWidth() - 100f, 0.75f * uiViewport.getWorldHeight());
        AirPropertiesDataUI airPropertiesDataUI = new AirPropertiesDataUI(aircraft, uiViewport, shape, batch, uiViewport.getWorldWidth() - 120, uiViewport.getWorldHeight() - 50);
        airPropertiesDataUI.setAir(air);
        autoPilotModeDataUI = new AutoPilotModeDataUI(aircraft, uiViewport, shape, batch, uiViewport.getWorldWidth() - 100, 110);

        worldCamera.setToOrtho(false);
        worldCamera.position.set(worldCamera.viewportWidth / 2, worldCamera.viewportHeight / 2, 0);
        worldCamera.update();

        backGround = new BackGround();

        // Set up the input processor
        Gdx.input.setInputProcessor(this);

        uiComponents = new Array<>();
        uiComponents.add(angleOfAttackDataUI);
        uiComponents.add(elevatorDataUI);
        uiComponents.add(throttleDataUI);
        uiComponents.add(autoPilotModeDataUI);
        uiComponents.add(climbRateDataUI);
        uiComponents.add(airPropertiesDataUI);
        uiComponents.add(speedDataUI);
        uiComponents.add(steeringModeDataUI);
        uiComponents.add(pitchAngleDataUI);

        // generate data for plot with aerodynamic behaviour
//        for (float alpha = -180; alpha <= 180; alpha += 0.1f){
//            System.out.printf("%.2f, %.4f, %.4f, %.4f\n", alpha, aircraft.getCl().calculateCoefficient(alpha),
//                    aircraft.getCd().calculateCoefficient(alpha), aircraft.getCm().calculateCoefficient(alpha));
//        }
    }

    @Override
    public void render() {
//        long time = System.nanoTime();
        if (!paused) {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            worldViewport.apply();
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(FlightDataUI.color);
            AutoPilot autoPilot = aircraft.getAutoPilot();
            altitudeTape.drawTape(aircraft.getPosition().y * UnitConversionUtils.getM2Feet(), steeringMode == SteeringMode.AUTO_PILOT && autoPilot.drawSetAltitude(), autoPilot.getSetAltitude() * UnitConversionUtils.getM2Feet());
            altitudeTape.drawStaticPart();
            velocityTape.drawTape(aircraft.getTrueAirspeed() * UnitConversionUtils.getMps2Knts(), steeringMode == SteeringMode.AUTO_PILOT && autoPilot.drawSetAirspeed(), autoPilot.getSetAirspeed() * UnitConversionUtils.getMps2Knts());
            velocityTape.drawStallRegionsWarning(aircraft.getStallSpeed() * UnitConversionUtils.getMps2Knts());
            shape.setColor(FlightDataUI.color);
            velocityTape.drawStaticPart();
            angleOfAttackDataUI.drawStaticPart();
            pitchAngleDataUI.draw(steeringMode == SteeringMode.AUTO_PILOT && autoPilot.getMode() == AutoPilotMode.PITCH_HOLD);

            batch.begin();
            batch.setProjectionMatrix(worldCamera.combined);
            backGround.render(batch, worldCamera, worldViewport.getScreenWidth(), worldViewport.getScreenHeight());
            aircraft.render(batch);
            batch.end();

            float deltaTime = Gdx.graphics.getDeltaTime();
            worldCamera.update();
            uiCamera.update();
            if (steeringMode == SteeringMode.AUTO_PILOT){
                aircraft.updateAutoPilot();
            }
            aircraft.update(deltaTime);
            shape.end();

            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setProjectionMatrix(worldCamera.combined);
            uiViewport.apply();
            shape.setProjectionMatrix(uiCamera.combined);

            for (FlightDataUI flightDataUI : uiComponents){
                flightDataUI.draw();
            }
            if (steeringMode == SteeringMode.AUTO_PILOT && aircraft.getAutoPilot().getMode() == AutoPilotMode.VERTICAL_SPEED) {
                climbRateDataUI.drawSetValue();
            }
            shape.end();

            // write values of UI components
            batch.setProjectionMatrix(uiCamera.combined);
            batch.begin();

            for (FlightDataUI flightDataUI : uiComponents){
                flightDataUI.writeValues();
            }
            altitudeTape.writeValues(aircraft.getPosition().y * UnitConversionUtils.getM2Feet());
            velocityTape.writeValues(aircraft.getTrueAirspeed() * UnitConversionUtils.getMps2Knts());
            steeringModeDataUI.writeSteeringMode(steeringMode);

            batch.end();

            FlightDataUI.updateUIColor(aircraft.getPosition().y); // improve readability UI for high altitude

            if (camFocusAircraft) { // set the camera position
                Vector2 pos = aircraft.getPosition();
                worldCamera.position.x = pos.x;
                worldCamera.position.y = pos.y;
            }
        }

//        System.out.println(System.nanoTime() - time);
    }

    @Override
    public void dispose() {
        batch.dispose();
        backGround.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.SPACE) {
            aircraft.reset();
        }
        if (keycode == Input.Keys.ESCAPE) {
            paused = !paused;
            System.out.println("paused: " + paused);
        }
        if (keycode == Input.Keys.PAGE_DOWN){
            aircraft.setChangeThrottle(ChangeThrottle.DOWN);
        }
        if (keycode == Input.Keys.PAGE_UP){
            aircraft.setChangeThrottle(ChangeThrottle.UP);
        }
        if (keycode == Input.Keys.HOME){
            worldCamera.zoom = 0.1f;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.PAGE_DOWN || keycode == Input.Keys.PAGE_UP) {
            aircraft.setChangeThrottle(ChangeThrottle.NONE);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            if (steeringMode == SteeringMode.MOUSE_CONTROL){
                steeringMode = SteeringMode.AUTO_PILOT;
                steeringModeDataUI.reset();
            } else if (steeringMode == SteeringMode.AUTO_PILOT){
                AutoPilot autoPilot = aircraft.getAutoPilot();
                float y = uiViewport.getWorldHeight() - screenY;
                if (autoPilotModeDataUI.mouseAboveUI(screenX, y)){
                    autoPilot.setMode(AutoPilotMode.VERTICAL_SPEED);
                    autoPilot.toggleClimbAndHold();
                    autoPilot.verticalSpeedController.resetErrorIntegral();
                } else if (pitchAngleDataUI.mouseAboveUI(screenX, y)){
                    autoPilot.setMode(AutoPilotMode.PITCH_HOLD);
                    autoPilot.pitchController.resetErrorIntegral();
                } else if (climbRateDataUI.mouseAboveUI(screenX + climbRateDataUI.getRadius(), y + climbRateDataUI.getRadius())) {
                    autoPilot.setMode(AutoPilotMode.VERTICAL_SPEED);
                    autoPilot.verticalSpeedController.resetErrorIntegral();
                    autoPilot.setClimbAndHold(false);
                } else if (velocityTape.mouseAboveUI(screenX, screenY)) {
                    autoPilot.toggleAutoThrottle();
                } else {
                    steeringMode = SteeringMode.NONE;
                    steeringModeDataUI.reset();
                }
            } else if (steeringMode == SteeringMode.NONE){
                steeringMode = SteeringMode.MOUSE_CONTROL;
                steeringModeDataUI.reset();
            }
        }
        if (button == 1) {
            camFocusAircraft = !camFocusAircraft;
            System.out.println("worldCamera focus on aircraft: " + camFocusAircraft);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (steeringMode == SteeringMode.MOUSE_CONTROL) {
            aircraft.moveElevatorFromMousePosition(screenY);
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        float xMouse = Gdx.input.getX();
        float yMouse = Gdx.input.getY();

        if (steeringMode == SteeringMode.AUTO_PILOT) {
            AutoPilot autoPilot = aircraft.getAutoPilot();
            if (pitchAngleDataUI.mouseAboveUI(xMouse, uiViewport.getWorldHeight() - yMouse)) {
                autoPilot.setMode(AutoPilotMode.PITCH_HOLD);
                autoPilot.changeSetPitchAngle(amountY);
            } else if (climbRateDataUI.mouseAboveUI(xMouse + climbRateDataUI.getRadius(),
                    uiViewport.getWorldHeight() - yMouse + climbRateDataUI.getRadius())) {
                autoPilot.setMode(AutoPilotMode.VERTICAL_SPEED);
                autoPilot.changeSetClimbRate(amountY, true);
            } else if (altitudeTape.mouseAboveUI(xMouse, yMouse)) {
                autoPilot.changeSetAltitude(amountY, true);
                autoPilot.setMode(AutoPilotMode.ALTITUDE_HOLD);
            } else if (autoPilotModeDataUI.mouseAboveUI(xMouse, uiViewport.getWorldHeight() - yMouse)) {
                boolean coarseTuning = autoPilotModeDataUI.mouseAboveLeftButton(xMouse);
                if (autoPilot.isClimbAndHold()){
                    autoPilot.changeSetAltitude(amountY, coarseTuning);
                } else {
                    autoPilot.changeSetClimbRate(amountY, coarseTuning);
                }
            } else if (velocityTape.mouseAboveUI(xMouse, yMouse)) {
                autoPilot.changeSetAirspeed(amountY);
            } else {
                zoom(amountY);
            }
        } else {
            zoom(amountY);
        }
        return false;
    }

    public void zoom(float amount){
        worldCamera.zoom += amount * worldCamera.zoom * 0.1f;
        worldCamera.zoom = Math.max(0.01f, Math.min(worldCamera.zoom, 10.0f)); // Clamp the zoom level
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }


    public static SteeringMode getSteeringMode(){
        return steeringMode;
    }
}

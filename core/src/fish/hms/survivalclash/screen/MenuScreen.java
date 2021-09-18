package fish.hms.survivalclash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import fish.hms.survivalclash.SurvivalClash;

public class MenuScreen implements Screen {
    private final SpriteBatch batch;
    private final Stage stage;

    private final TextButton playButton;
    private final Texture bg;

    public MenuScreen(final SurvivalClash game) {
        this.batch = game.getBatch();
        this.stage = new Stage(game.getScreenViewport(), this.batch);
        this.bg = new Texture("assets/world/WorldMap.png");
        FreeTypeFontGenerator fontGenerator = game.getFontGenerator();
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = game.getFontParameter();

        Gdx.input.setInputProcessor(stage);

        // Game Title
        fontParameter.size = 30;
        fontParameter.borderWidth = 1;
        fontParameter.color = new Color(0.9f, 0.3f, 0.2f, 1);
        fontParameter.shadowOffsetX = 3;
        fontParameter.shadowOffsetY = 3;
        fontParameter.shadowColor = new Color(0, 0, 0, 0.5f);
        BitmapFont font = fontGenerator.generateFont(fontParameter);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label gameLabel = new Label("Survival Clash", labelStyle);
        gameLabel.setSize(160, 30);
        gameLabel.setPosition(25, 260);
        stage.addActor(gameLabel);

        // Play Button
        fontParameter.size = 15;
        fontParameter.color = new Color(0.2f, 0.8f, 0.45f,1);
        font = fontGenerator.generateFont(fontParameter);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        playButton = new TextButton("PLAY", textButtonStyle);
        playButton.setSize(100, 15);
        playButton.setPosition(140, 175);
        playButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));

                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playButton.getLabel().setColor(0.15f, 0.7f, 0.4f,1);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                playButton.getLabel().setColor(0.2f, 0.8f, 0.45f,1);
            }
        });
        stage.addActor(playButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        batch.begin();
        batch.draw(bg, 0, 0, 384, 384);
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

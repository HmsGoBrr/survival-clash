package fish.hms.survivalclash;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import fish.hms.survivalclash.entities.AnimationType;
import fish.hms.survivalclash.entities.Prop;
import fish.hms.survivalclash.screen.MenuScreen;

public class SurvivalClash extends Game {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private FitViewport screenViewport;

	private FreeTypeFontGenerator fontGenerator;
	private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;

	@Override
	public void resize(final int width, final int height) {
		screenViewport.update(width, height);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		screenViewport = new FitViewport(384, 384, camera);

		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Acme7Wide.ttf"));
		fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		camera.update();
		screenViewport.apply();

		super.render();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
	}

	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
		fontGenerator.dispose();
		for (AnimationType animationType : AnimationType.values()) {
			animationType.getTexture().dispose();
		}
		for (Prop.PropType propType : Prop.PropType.values()) {
			propType.getTexture().dispose();
		}
	}

	public SpriteBatch getBatch() {
		return batch;
	}
	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
	public OrthographicCamera getCamera() {
		return camera;
	}
	public FitViewport getScreenViewport() {
		return screenViewport;
	}
	public FreeTypeFontGenerator getFontGenerator() {
		return fontGenerator;
	}
	public FreeTypeFontGenerator.FreeTypeFontParameter getFontParameter() {
		return fontParameter;
	}
}

package fish.hms.survivalclash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import fish.hms.survivalclash.SurvivalClash;
import fish.hms.survivalclash.entities.*;
import fish.hms.survivalclash.entities.Character;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private final Texture map;
    private final Character knight;
    private final Enemy[] enemies;
    private final Prop[] props;

    private final static float MAP_SCALE = 4.0f;

    private float enemySpawnStateTime;
    private final float enemySpawnCooldown;

    private float time;
    private int level;

    private final BitmapFont timeTextFont;
    private final BitmapFont levelTextFont;

    public GameScreen(SurvivalClash game) {
        this.batch = game.getBatch();
        this.shapeRenderer = game.getShapeRenderer();
        this.camera = game.getCamera();

        this.map = new Texture("assets/world/WorldMap.png");
        this.knight = new Character(this, getMapWidth()/2 - 40, getMapHeight()/2 - 40);

        this.enemies = new Enemy[50];
        for (int i = 0; i < 50; i++) {
            enemies[i] = new Enemy(this, 160, getMapHeight()/2, AnimationType.SLIME_IDLE, AnimationType.SLIME_RUN, AnimationType.SLIME_ATTACK);
            enemies[i].setTarget(knight);
            enemies[i].setAlive(false);
        }

        this.props = new Prop[20];
        for (int i = 0; i < 20; i++) {
            if (props[i] != null) continue;

            float x = MathUtils.random(3,20)*80;
            float y = MathUtils.random(3,20)*80;

            final Prop.PropType propType;
            switch (MathUtils.random(1, 4)) {
                case 1:
                    propType = Prop.PropType.BUSH;
                    break;
                case 2:
                    propType = Prop.PropType.LOG;
                    break;
                case 3:
                    propType = Prop.PropType.ROCK;
                    break;
                case 4:
                    if (i + 1 < 20) {
                        propType = Prop.PropType.TREE1;
                        props[i + 1] = new Prop(Prop.PropType.TREE2, x, y + 80);
                    } else {
                        propType = Prop.PropType.BUSH;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + MathUtils.random(1, 2));
            }

            props[i] = new Prop(propType, x, y);
        }

        this.enemySpawnStateTime = 0;
        this.enemySpawnCooldown = 3;
        this.time = 0;

        FreeTypeFontGenerator fontGenerator = game.getFontGenerator();
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = game.getFontParameter();

        fontParameter.size = 20;
        fontParameter.borderWidth = 0;
        fontParameter.color = Color.WHITE;
        fontParameter.shadowOffsetX = 0;
        fontParameter.shadowOffsetY = 0;
        fontParameter.shadowColor = new Color(0, 0, 0, 0);
        timeTextFont = fontGenerator.generateFont(fontParameter);

        fontParameter.size = 10;
        levelTextFont = fontGenerator.generateFont(fontParameter);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        enemySpawnStateTime += delta;
        time += delta;
        level = ((int)time / 60) + 1;

        camera.position.set(knight.getPos().x + knight.getWidth()/2, knight.getPos().y + knight.getHeight()/2, 0);

        ScreenUtils.clear(1, 1, 1, 1);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(map, 0, 0, getMapWidth(), getMapHeight());
        knight.tick(batch, delta);

        if (enemySpawnStateTime >= enemySpawnCooldown) {
            for (int i = 0; i < 50; i++) {
                if (!enemies[i].isAlive()) {
                    enemies[i].getPos().set(160, getMapHeight()/2);
                    enemies[i].setHealth(30);
                    enemies[i].setAlive(true);
                    enemySpawnStateTime = 0;
                    break;
                }
            }
        }

        for (int i = 0; i < 50; i++) {
            if (enemies[i].isAlive()) enemies[i].tick(batch, delta);
        }

        for (Prop prop : props) {
            prop.render(batch);
        }

        batch.end();

        batch.setProjectionMatrix(camera.projection);
        batch.begin();
        timeTextFont.draw(batch, formatSeconds(time), -52.5f, 185);
        levelTextFont.draw(batch, "Level: " + level, -52.5f, 165);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 30; i++) {
            enemies[i].drawHealthBar(shapeRenderer);
        }
        shapeRenderer.end();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        knight.drawHurtEffect(shapeRenderer, delta);
        
        shapeRenderer.rect(-100, -182, 200, 20, Color.RED, Color.RED, Color.RED, Color.RED);
        if (knight.getHealth() > 0) {
            shapeRenderer.rect(
                    -100, -182,
                    (knight.getHealth() / knight.getMaxHealth() * 100) * 2,
                    20,
                    Color.LIME, Color.LIME, Color.LIME, Color.LIME
            );
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
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
        map.dispose();
        timeTextFont.dispose();
    }

    public float getMapWidth() {
        return map.getWidth() * MAP_SCALE;
    }
    public float getMapHeight() {
        return map.getHeight() * MAP_SCALE;
    }

    public Character getKnight() {
        return knight;
    }

    public Prop[] getProps() {
        return props;
    }

    private static String formatSeconds(float timeInSeconds) {
        return String.format("%02d:%02d:%02d",
                (int)timeInSeconds/60%60,
                (int)timeInSeconds%60,
                (int)((timeInSeconds%60)*100) - (int)timeInSeconds%60 * 100
        );
    }

    /*private void drawHitBoxes() {
        Rectangle rectangle = knight.getWeaponCollisionRec();
        shapeRenderer.rect(
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height,
                Color.RED,
                Color.RED,
                Color.RED,
                Color.RED
        );
        rectangle = knight.getCollisionRec();
        shapeRenderer.rect(
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height,
                Color.GREEN,
                Color.GREEN,
                Color.GREEN,
                Color.GREEN
        );
        for (int i = 0; i < 30; i++) {
            rectangle = enemies[i].getCollisionRec();
            shapeRenderer.rect(
                    rectangle.x,
                    rectangle.y,
                    rectangle.width,
                    rectangle.height,
                    Color.GREEN,
                    Color.GREEN,
                    Color.GREEN,
                    Color.GREEN
            );
        }
        for (Prop prop : props) {
            if (prop.isHasCollision()) {
                rectangle = prop.getCollisionRec();
                shapeRenderer.rect(
                        rectangle.x,
                        rectangle.y,
                        rectangle.width,
                        rectangle.height,
                        Color.BLUE,
                        Color.BLUE,
                        Color.BLUE,
                        Color.BLUE
                );
            }
        }
    }*/
}

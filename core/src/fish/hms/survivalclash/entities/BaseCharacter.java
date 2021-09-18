package fish.hms.survivalclash.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import fish.hms.survivalclash.screen.GameScreen;

public abstract class BaseCharacter {
    private final GameScreen gameScreen;
    protected AnimationType animationType;
    protected Vector2 pos;
    protected Vector2 velocity;
    protected float speed;
    protected float width;
    protected float height;
    protected float scale;
    protected float stateTime;
    protected float health;
    protected float maxHealth;
    protected boolean isFacingLeft;
    protected boolean wasFacingLeft;
    private boolean alive;

    public BaseCharacter(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        animationType = AnimationType.KNIGHT_IDLE;
        pos = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        speed = 150.0f;
        width = (float) (animationType.getTexture().getWidth() / animationType.getFrameCols());
        height = (float) animationType.getTexture().getHeight();
        scale = 4.0f;
        stateTime = 0;
        isFacingLeft = false;
        wasFacingLeft = false;
        alive = true;
    }

    public void tick(SpriteBatch batch, float dt) {
        if ((pos.x < 160.0f && velocity.x < 0) || (pos.x > gameScreen.getMapWidth() - 160.0f - width * scale && velocity.x > 0))
            velocity.x = 0;
        if ((pos.y < 160.0f && velocity.y < 0) || (pos.y > gameScreen.getMapHeight() - 160.0f - height * scale && velocity.y > 0))
            velocity.y = 0;

        for (Prop prop : gameScreen.getProps()) {
            if (prop.isHasCollision()) {
                final Rectangle nextRect = new Rectangle(
                        getCollisionRec().x + velocity.x * speed * dt,
                        getCollisionRec().y + velocity.y * speed * dt,
                        getCollisionRec().width, getCollisionRec().height
                );
                if (nextRect.overlaps(prop.getCollisionRec())) {
                    if ((getCollisionRec().x + getCollisionRec().width < prop.getCollisionRec().x && velocity.x > 0) ||
                        (getCollisionRec().x > prop.getCollisionRec().x + prop.getCollisionRec().width && velocity.x < 0)) {
                        velocity.x = 0;
                    }
                    if ((getCollisionRec().y + getCollisionRec().height < prop.getCollisionRec().y && velocity.y > 0) ||
                        (getCollisionRec().y > prop.getCollisionRec().y + prop.getCollisionRec().height && velocity.y < 0)) {
                        velocity.y = 0;
                    }
                }
            }
        }

        pos.x += velocity.x * speed * dt;
        pos.y += velocity.y * speed * dt;
        velocity.x = 0;
        velocity.y = 0;

        stateTime += dt;

        TextureRegion currentFrame = animationType.getAnimation().getKeyFrame(stateTime, true);
        if (isFacingLeft) currentFrame.flip(!currentFrame.isFlipX(), currentFrame.isFlipY());
        batch.draw(currentFrame, pos.x, pos.y, width * scale, height * scale);
    }

    public Vector2 getPos() {
        return pos;
    }
    public float getWidth() {
        return width * scale;
    }
    public float getHeight() {
        return height * scale;
    }
    public Rectangle getCollisionRec() {
        return new Rectangle(pos.x, pos.y, width * scale, height * scale);
    }
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public float getHealth() {
        return health;
    }
    public void setHealth(float health) {
        this.health = health;
    }
    public float getMaxHealth() {
        return maxHealth;
    }

    public void takeDamage(Enemy enemyDamaging, float damage) {
        health -= damage;
        if (health <= 0.0f) setAlive(false);
    }
}

package fish.hms.survivalclash.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import fish.hms.survivalclash.screen.GameScreen;

public class Character extends BaseCharacter {
    private final AnimationType weapon;
    private final Animation<TextureRegion> weaponAnimation;
    private Rectangle weaponCollisionRec;

    private boolean attacking;
    private boolean finishedAttacking;
    private float weaponStateTime;

    private Enemy enemyDamaging;
    private boolean knockingPlayerBack;
    private int knockBackIteration;
    private float knockBackForce;

    private boolean hurtEffectActive;
    private float hurtEffectStateTime;
    private float hurtEffectAlpha;

    public Character(GameScreen gameScreen, float x, float y) {
        super(gameScreen);
        this.weapon = AnimationType.WEAPON;
        this.weaponAnimation = weapon.getAnimation();
        this.attacking = false;
        this.finishedAttacking = false;
        this.health = 100;
        this.maxHealth = 100;
        this.pos.x = x;
        this.pos.y = y;
        this.knockingPlayerBack = false;
        this.knockBackIteration = 0;
        this.knockBackForce = 15;
        this.hurtEffectActive = false;
        this.hurtEffectStateTime = 0;
        this.hurtEffectAlpha = 1;
    }

    @Override
    public void tick(SpriteBatch batch, float dt) {
        if (!isAlive()) return;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) velocity.x -= 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) velocity.x += 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) velocity.y += 1.0f;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) velocity.y -= 1.0f;

        if (Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y)) != 0.0f) animationType = AnimationType.KNIGHT_RUN;
        else animationType = AnimationType.KNIGHT_IDLE;

        isFacingLeft = Gdx.input.getX() < Gdx.graphics.getWidth() / 2;

        if (knockingPlayerBack) {
            knockBackIteration += 1;
            velocity.x = ((pos.x - enemyDamaging.pos.x) > 0 ? 2 : -2) * knockBackForce / 10;
            velocity.y = ((pos.y - enemyDamaging.pos.y) > 0 ? 2 : -2) * knockBackForce / 10;
            if (knockBackForce > 0) knockBackForce -= 1;
            if (knockBackIteration >= 15) {
                knockingPlayerBack = false;
                knockBackIteration = 0;
                knockBackForce = 15;
            }
        }

        super.tick(batch, dt);

        Vector2 offset = new Vector2(isFacingLeft ? -35 : 35, 0);
        weaponCollisionRec = new Rectangle(
                pos.x + offset.x + (isFacingLeft ? -10 : 20),
                pos.y + offset.y - 25 + weapon.getTexture().getHeight() * scale / 2.5f,
                weapon.getTexture().getWidth() / 5.0f * scale - 10,
                weapon.getTexture().getHeight() * scale - 10
        );

        finishedAttacking = false;

        if ((Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && stateTime > 0.5f)
            attacking = true;

        if (attacking) {
            weaponStateTime += dt;
            if (weaponAnimation.isAnimationFinished(weaponStateTime)) {
                attacking = false;
                finishedAttacking = true;
                weaponStateTime = 0;
            }
        }

        TextureRegion weaponFrame = weaponAnimation.getKeyFrame(weaponStateTime, false);
        weaponFrame.flip(isFacingLeft != weaponFrame.isFlipX(), false);
        batch.draw(weaponFrame,
                pos.x + offset.x, pos.y + offset.y,
                weapon.getTexture().getWidth() / 5.0f * scale, weapon.getTexture().getHeight() * scale
        );
    }

    public void drawHurtEffect(ShapeRenderer shapeRenderer, float dt) {
        if (!hurtEffectActive) {
            hurtEffectStateTime = 0;
            hurtEffectAlpha = 1;
            return;
        }

        hurtEffectStateTime += dt;

        if (hurtEffectStateTime >= 0.05f) {
            hurtEffectAlpha -= 0.07f;
            hurtEffectStateTime = 0;
        }

        if (hurtEffectAlpha <= 0) hurtEffectActive = false;

        shapeRenderer.rect(-192, -192, 384, 384,
            new Color(0.9f, 0.3f, 0.2f, hurtEffectAlpha - 0.2f),
            new Color(0.9f, 0.3f, 0.2f, hurtEffectAlpha - 0.2f),
            new Color(0.9f, 0.3f, 0.2f, hurtEffectAlpha - 0.2f),
            new Color(0.9f, 0.3f, 0.2f, hurtEffectAlpha - 0.2f)
        );
    }

    public Rectangle getWeaponCollisionRec() {
        return weaponCollisionRec;
    }

    public float getHealth() {
        return health;
    }

    @Override
    public void takeDamage(Enemy enemyDamaging, float damage) {
        health -= damage;

        hurtEffectActive = true;
        hurtEffectStateTime = 0;
        hurtEffectAlpha = 1;

        if (!knockingPlayerBack) this.enemyDamaging = enemyDamaging;
        knockingPlayerBack = true;

        if (health <= 0.0f) setAlive(false);
    }

    public boolean isFinishedAttacking() {
        return finishedAttacking;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean isKnockingPlayerBack() {
        return knockingPlayerBack;
    }

    @Override
    public Rectangle getCollisionRec() {
        return new Rectangle(pos.x + 12, pos.y + 4, (width * scale) - 24, (height * scale) - 8);
    }
}

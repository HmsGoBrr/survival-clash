package fish.hms.survivalclash.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fish.hms.survivalclash.screen.GameScreen;

public class Enemy extends BaseCharacter {
    public enum EnemyType {
        SLIME(AnimationType.SLIME_IDLE, AnimationType.SLIME_RUN, AnimationType.SLIME_ATTACK, 100, 7, 0.4f),
        GOBLIN(AnimationType.SLIME_IDLE, AnimationType.SLIME_RUN, AnimationType.SLIME_ATTACK, 120, 4, 0.6f);

        private final AnimationType idle, run, attack;
        private final float speed;
        private final float damageCoolDown;
        private final float damage;

        EnemyType(AnimationType idle, AnimationType run, AnimationType attack, float speed, float damage, float damageCoolDown) {
            this.idle = idle;
            this.run = run;
            this.attack = attack;
            this.speed = speed;
            this.damage = damage;
            this.damageCoolDown = damageCoolDown;
        }

        public AnimationType getIdle() {
            return idle;
        }
        public AnimationType getRun() {
            return run;
        }
        public AnimationType getAttack() {
            return attack;
        }
        public float getSpeed() {
            return speed;
        }
        public float getDamage() {
            return damage;
        }
        public float getDamageCoolDown() {
            return damageCoolDown;
        }
    }

    private final GameScreen gameScreen;
    private final AnimationType IDLE, RUN, ATTACK;
    private Character target;
    private final float damage;
    private float damageStateTime;
    private final float damageCoolDown;
    private final float damageRadius;
    private final Animation<TextureRegion> deathVFX;
    private float vfxStateTime;

    public Enemy(GameScreen gameScreen, float x, float y, AnimationType IDLE, AnimationType RUN, AnimationType ATTACK) {
        super(gameScreen);
        this.gameScreen = gameScreen;
        this.pos.x = x;
        this.pos.y = y;
        this.IDLE = IDLE;
        this.RUN = RUN;
        this.ATTACK = ATTACK;
        this.animationType = IDLE;
        this.width = (float) (animationType.getTexture().getWidth() / animationType.getFrameCols());
        this.height = (float) animationType.getTexture().getHeight();
        this.speed = 120;
        this.damage = 5;
        this.damageStateTime = 0;
        this.damageCoolDown = 0.5f;
        this.damageRadius = 30;
        this.deathVFX = AnimationType.SMOKE_VFX.getAnimation();
        this.vfxStateTime = 0;
        this.health = 30;
        this.maxHealth = 30;
    }

    @Override
    public void tick(SpriteBatch batch, float dt) {
        if (!isAlive()) {
            vfxStateTime += dt;
            if (!deathVFX.isAnimationFinished(vfxStateTime)) {
                final TextureRegion vfxFrame = deathVFX.getKeyFrame(vfxStateTime, false);
                batch.draw(
                        vfxFrame,
                        pos.x + (width * scale / 2) - vfxFrame.getRegionWidth() / 2.0f,
                        pos.y + (height + scale / 2) - vfxFrame.getRegionWidth() / 2.0f,
                        width * scale, height * scale
                );
            }
            return;
        }

        velocity.x = (target.getPos().x - getPos().x) > 0 ? 1 : -1;
        velocity.y = (target.getPos().y - getPos().y) > 0 ? 1 : -1;
        if (pos.dst(target.pos) < damageRadius) {
            velocity.x = 0;
            velocity.y = 0;
        }

        if (Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y)) != 0 && !target.isKnockingPlayerBack()) {
            animationType = RUN;
            isFacingLeft = velocity.x < 0.0f;
        } else if (target.getCollisionRec().overlaps(getCollisionRec()) && pos.dst(target.pos) < damageRadius) {
            animationType = ATTACK;
            damageStateTime += dt;
            if (damageStateTime >= damageCoolDown) {
                target.takeDamage(this, damage);
                damageStateTime = 0;
            }
        } else {
            animationType = IDLE;
        }

        if (target.isKnockingPlayerBack()) {
            velocity.x = 0;
            velocity.y = 0;
        }

        super.tick(batch, dt);

        if (gameScreen.getKnight().isFinishedAttacking() && getCollisionRec().overlaps(gameScreen.getKnight().getWeaponCollisionRec())) {
            takeDamage(null, 10);
        }
    }

    public void drawHealthBar(ShapeRenderer shapeRenderer) {
        if (!isAlive()) return;
        shapeRenderer.rect(pos.x + 5, pos.y + 64, (width * scale) - 10, 10, Color.RED, Color.RED, Color.RED, Color.RED);
        shapeRenderer.rect(pos.x + 5, pos.y + 64, (health / maxHealth) * ((width * scale) - 10), 10, Color.LIME, Color.LIME, Color.LIME, Color.LIME);
    }

    public void setTarget(Character target) {
        this.target = target;
    }
}

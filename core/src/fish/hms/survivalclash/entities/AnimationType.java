package fish.hms.survivalclash.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum AnimationType {
    KNIGHT_IDLE(new Texture("assets/characters/knight_idle_spritesheet.png"), 6, 1, 1/12.0f),
    KNIGHT_RUN(new Texture("assets/characters/knight_run_spritesheet.png"), 6, 1, 1/12.0f),
    WEAPON(new Texture("assets/characters/weapon_spritesheet.png"), 5, 1, 1/15.0f),

    SLIME_IDLE(new Texture("assets/characters/slime_idle_spritesheet.png"), 6, 1, 1/12.0f),
    SLIME_RUN(new Texture("assets/characters/slime_run_spritesheet.png"), 6, 1, 1/12.0f),
    SLIME_ATTACK(new Texture("assets/characters/slime_attack_spritesheet.png"), 6, 1, 1/12.0f),

    GOBLIN_IDLE(new Texture("assets/characters/goblin_idle_spritesheet.png"), 6, 1, 1/12.0f),
    GOBLIN_RUN(new Texture("assets/characters/goblin_run_spritesheet.png"), 6, 1, 1/12.0f),
    GOBLIN_ATTACK(new Texture("assets/characters/goblin_attack_spritesheet.png"), 6, 1, 1/12.0f),

    SMOKE_VFX(new Texture("assets/vfx/smoke.png"), 5, 1, 1/10.0f);

    private final Texture texture;
    private final int frameCols, frameRows;
    private final float frameDuration;

    AnimationType(Texture texture, int frameCols, int frameRows, float frameDuration) {
        this.texture = texture;
        this.frameCols = frameCols;
        this.frameRows = frameRows;
        this.frameDuration = frameDuration;
    }

    public Texture getTexture() {
        return texture;
    }

    public Animation<TextureRegion> getAnimation() {
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / frameCols,
                texture.getHeight() / frameRows);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index++] = tmp[i][j];
            }
        }

        return new Animation<>(frameDuration, frames);
    }

    public int getFrameCols() {
        return frameCols;
    }
}

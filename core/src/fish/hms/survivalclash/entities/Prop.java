package fish.hms.survivalclash.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Prop {
    public enum PropType {
        BUSH(new Texture("assets/world/Bush.png")),
        LOG(new Texture("assets/world/Log.png")),
        ROCK(new Texture("assets/world/Rock.png")),
        TREE1(new Texture("assets/world/Tree1.png")),
        TREE2(new Texture("assets/world/Tree2.png"));

        private final Texture texture;

        PropType(Texture texture) {
            this.texture = texture;
        }

        public Texture getTexture() {
            return texture;
        }
    }

    private final PropType propType;
    private final Vector2 pos;
    private final float scale;
    private final boolean hasCollision;

    public Prop(PropType propType, float x, float y) {
        this.propType = propType;
        this.pos = new Vector2(x, y);
        this.scale = 2.5f;
        this.hasCollision = propType != PropType.BUSH && propType != PropType.TREE2;
    }

    public void render(SpriteBatch batch) {
        batch.draw(propType.texture, pos.x, pos.y, propType.texture.getWidth() * scale, propType.texture.getHeight() * scale);
    }

    public Rectangle getCollisionRec() {
        if (!hasCollision) return null;

        float x = pos.x;
        float y = pos.y;
        float width = propType.texture.getWidth() * scale;
        float height = propType.texture.getHeight() * scale;
        if (propType == PropType.TREE1) {
            x += 11 * scale;
            y += 5 * scale;
            width -= 22 * scale;
            height -= 21 * scale;
        }

        return new Rectangle(x, y, width, height);
    }

    public boolean isHasCollision() {
        return hasCollision;
    }
}

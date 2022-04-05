package io.github.eninja33.magillurgy.content.magic;

/**
 * A "Resonance" is a tricky thing to explain, but I'll do my
 * best to describe how I envision it in the context of the mod.
 * The following is an example of how something like this might
 * be explained in-game, with a bit of a storytelling/textbook flair.
 *
 * A Resonance is a descriptor of magical energy. There are several
 * types of magic out there: Life, Soul, Ender, Void, Astral, and Arcane.
 * These six types of magic are commonly laid out in a pyramidal shape, to
 * aid visualization. Imagine the four corners of the pyramid point in the
 * cardinal directions.
 * Life and Ender exist on the east and west corners of the pyramid, respectively.
 * Soul and Void exist on the north and south.
 * Astral sits at the point atop the pyramid, above the rest.
 * Finally, Arcane lies at the bottom and center of the pyramid.
 * Why does this work? Well, Life and Ender energies are the opposites of one
 * another. When the two of them meet, they cancel each other out, resulting in
 * some Arcane. The same is true for Soul and Void. This works with the pyramid
 * visualization because, well, the midpoint between the East and West corners
 * is exactly Arcane, and likewise for North and South.
 *
 * There is a hypothesis that there should exist an opposite for Astral - that the
 * pyramid should be mirrored beneath the ground, with a sixth point - making it a
 * cube. Mathematically, it fits into all our models - yet no one has found this
 * seventh element. Experiments attempting to invert Astral magic are ongoing, and
 * some have created speculations on what this inverse might be capable of. However,
 * discussions of this potential seventh element are beyond the scope of this
 * introductory text. We will focus our efforts on the six that we know and can use.
 *
 * Anyway the class is basically just a Vec4f lol
 * Methods in the class return the object itself for easy method chaining
 */
public class Resonance {

    public static final Resonance LIFE = new Resonance(1, 0, 0, 0);
    public static final Resonance ENDER = new Resonance(-1, 0, 0, 0);
    public static final Resonance SOUL = new Resonance(0, 1, 0, 0);
    public static final Resonance VOID = new Resonance(0, -1, 0, 0);
    public static final Resonance ASTRAL = new Resonance(0, 0, 1, 0);
    //The one who wrote that book didn't know about Nether. Why?
    public static final Resonance NETHER = new Resonance(0, 0, -1, 0);
    public static final Resonance ARCANE = new Resonance(0, 0, 0, 1);

    public float life, soul, astral;
    private float arcane;
    private boolean arcaneDirty = true;

    //Methods that are actually related to the resonance and its API go up here

    /**
     * Dilutes this Resonance, bringing it closer to Arcane. The parameter
     * indicates how much to dilute. dilute(0) leaves it unchanged, while
     * dilute(1) sets it exactly to pure Arcane.
     * @param factor The factor to dilute by.
     * @return This object, modified.
     */
    public Resonance dilute(float factor) {
        return mix(ARCANE, 1-factor, factor);
    }

    /**
     * Adds some random variation to the Resonance, in a random direction.
     * It's NOT completely uniform, like a hollow sphere around the starting point.
     * It chooses a random point on the sphere, then moves towards it a random
     * percentage of the way. This makes it impossible to escape the sphere,
     * so long as 0 <= minAmount <= maxAmount <= 1, and makes it more likely to
     * draw the Resonance generally towards Arcane, in the center.
     */
    public Resonance contaminate(float minAmount, float maxAmount) {
        double r1, r2, r3;
        do {
            r1 = Math.random()*2-1;
            r2 = Math.random()*2-1;
            r3 = Math.random()*2-1;
        } while (r1*r1+r2*r2+r3*r3 > 1);
        float rand = (float) Math.random()*(maxAmount - minAmount) + minAmount;
        double len = Math.sqrt(r1*r1 + r2*r2 + r3*r3);
        r1 /= len;
        r2 /= len;
        r3 /= len;
        return mix((float) r1, (float) r2, (float) r3, 1-rand, rand);
    }

    public Resonance mix(Resonance other, float percentA, float percentB) {
        return mix(other.life, other.soul, other.astral, percentA, percentB);
    }

    public Resonance mix(float otherLife, float otherSoul, float otherAstral, float percentA, float percentB) {
        life = percentA * life + percentB * otherLife;
        soul = percentA * soul + percentB * otherSoul;
        astral = percentA * astral + percentB * otherAstral;
        arcaneDirty = true;
        return this;
    }

    public float getArcane() {
        if (arcaneDirty) {
            arcane = (float) Math.sqrt(1 - life*life - soul*soul - astral*astral);
            arcaneDirty = false;
        }
        return arcane;
    }

    //Methods that are just generic vector-y helper methods go down here

    public Resonance(float life, float soul, float astral) {
        set(life, soul, astral, 0);
        getArcane();
    }

    public Resonance(float life, float soul, float astral, float arcane) {
        set(life, soul, astral, arcane);
    }

    public Resonance set(float life, float soul, float astral, float arcane) {
        this.life = life;
        this.soul = soul;
        this.astral = astral;
        this.arcane = arcane;
        return this;
    }

    public Resonance copy() {
        return new Resonance(life, soul, astral, arcane);
    }

    public Resonance copyFrom(Resonance r) {
        return set(r.life, r.soul, r.astral, r.arcane);
    }

}

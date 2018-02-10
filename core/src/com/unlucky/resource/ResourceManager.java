package com.unlucky.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.unlucky.battle.Move;
import com.unlucky.inventory.Item;

import java.util.Random;

/**
 * Main resource loading and storage class. Uses an AssetManager to manage textures, sounds,
 * musics, etc. Contains convenience methods to load and get resources from the asset manager.
 *
 * @author Ming Li
 */
public class ResourceManager {

    public AssetManager assetManager;
    // json
    private JsonReader jsonReader;

    // Texture Atlas that contains every sprite
    public TextureAtlas atlas;

    // 2D TextureRegion arrays that stores sprites of various sizes for easy animation
    public TextureRegion[][] sprites16x16;
    public TextureRegion[][] tiles16x16;
    public TextureRegion[][] atiles16x16;
    public TextureRegion[][] items20x20;
    public TextureRegion[][] battleSprites96x96;
    public TextureRegion[][] battleBackgrounds400x240;
    public TextureRegion[][] battleAttacks64x64;
    public TextureRegion[][] battleHeal96x96;
    public TextureRegion[][] levelUp96x96;

    public TextureRegion lightmap;

    // UI
    public TextureRegion[][] dirpad20x20;
    public TextureRegion[][] movebutton145x50;
    public TextureRegion[][] stdmedbutton110x50;
    public TextureRegion[][] optionbutton32x32;
    public TextureRegion[][] exitbutton18x18;
    public TextureRegion[][] invbuttons92x28;
    public TextureRegion dialogBox400x80;
    public TextureRegion playerhpbar145x40;
    public TextureRegion enemyhpbar145x40;
    public TextureRegion levelupscreen400x240;
    public TextureRegion inventoryui372x212;
    public TextureRegion selectedslot28x28;

    // Skin
    public Skin skin;
    public Skin dialogSkin;

    // misc
    public TextureRegion shadow11x6;
    public TextureRegion redarrow10x9;

    // Arrays for each type of Move
    // Contains the entire pool of moves for each type
    public final Array<Move> accurateMoves = new Array<Move>();
    public final Array<Move> wideMoves = new Array<Move>();
    public final Array<Move> critMoves = new Array<Move>();
    public final Array<Move> healMoves = new Array<Move>();

    // contains the movepools of each boss referenced by bossIndex
    public final Array<Array<Move>> bossMoves = new Array<Array<Move>>();

    // contains all the items separated by rarity
    public final Array<Array<Item>> items = new Array<Array<Item>>();

    // Fonts
    public final BitmapFont pixel10 = new BitmapFont(Gdx.files.internal("fonts/pixel.fnt"),
                                                    Gdx.files.internal("fonts/pixel.png"), false);

    public ResourceManager() {
        assetManager = new AssetManager();
        jsonReader = new JsonReader();

        assetManager.load("textures.atlas", TextureAtlas.class);
        assetManager.load("skins/ui.atlas", TextureAtlas.class);
        assetManager.load("skins/dialog.atlas", TextureAtlas.class);

        assetManager.finishLoading();

        atlas = assetManager.get("textures.atlas", TextureAtlas.class);

        skin = new Skin(assetManager.get("skins/ui.atlas", TextureAtlas.class));
        skin.add("default-font", pixel10);
        skin.load(Gdx.files.internal("skins/ui.json"));

        dialogSkin = new Skin(assetManager.get("skins/dialog.atlas", TextureAtlas.class));
        dialogSkin.add("default-font", pixel10);
        dialogSkin.load(Gdx.files.internal("skins/dialog.json"));

        // sprites
        sprites16x16 = atlas.findRegion("16x16_sprites").split(16, 16);
        tiles16x16 = atlas.findRegion("16x16_tiles").split(16, 16);
        atiles16x16 = atlas.findRegion("16x16_atiles").split(16, 16);
        items20x20 = atlas.findRegion("20x20_items").split(20, 20);
        battleSprites96x96 = atlas.findRegion("96x96_battle_sprites").split(96, 96);
        battleBackgrounds400x240 = atlas.findRegion("battle_bgs").split(400, 240);
        shadow11x6 = atlas.findRegion("11x6_shadow");
        redarrow10x9 = atlas.findRegion("10x9_redarrow");
        battleAttacks64x64 = atlas.findRegion("64x64_battle_attacks").split(64, 64);
        battleHeal96x96 = atlas.findRegion("96x96_battle_heal").split(96, 96);
        levelUp96x96 = atlas.findRegion("96x96_level_up").split(96, 96);
        levelupscreen400x240 = atlas.findRegion("level_up");
        lightmap = atlas.findRegion("testlightmap");

        // ui
        dirpad20x20 = atlas.findRegion("dir_pad").split(40, 40);
        movebutton145x50 = atlas.findRegion("move_buttons").split(145, 50);
        stdmedbutton110x50 = atlas.findRegion("standard_med_button").split(110, 50);
        dialogBox400x80 = atlas.findRegion("dialog_box");
        playerhpbar145x40 = atlas.findRegion("player_hp_bar");
        enemyhpbar145x40 = atlas.findRegion("enemy_hp_bar");
        optionbutton32x32 = atlas.findRegion("option_buttons").split(32, 32);
        inventoryui372x212 = atlas.findRegion("inv_ui");
        exitbutton18x18 = atlas.findRegion("exit_button").split(18, 18);
        selectedslot28x28 = atlas.findRegion("selected_slot");
        invbuttons92x28 = atlas.findRegion("inv_buttons").split(92, 28);

        loadMoves();
        loadItems();
    }

    /**
     * Loads the ImageButton styles for buttons with up and down image effects
     *
     * @param numButtons
     * @param sprites
     * @return a style array for ImageButtons
     */
    public ImageButton.ImageButtonStyle[] loadImageButtonStyles(int numButtons, TextureRegion[][] sprites) {
        ImageButton.ImageButtonStyle[] ret = new ImageButton.ImageButtonStyle[numButtons];
        for (int i = 0; i < numButtons; i++) {
            ret[i] = new ImageButton.ImageButtonStyle();
            ret[i].imageUp = new TextureRegionDrawable(sprites[0][i]);
            ret[i].imageDown = new TextureRegionDrawable(sprites[1][i]);
        }
        return ret;
    }

    private void loadMoves() {
        // parse moves.json
        JsonValue base = jsonReader.parse(Gdx.files.internal("moves/moves.json"));
        JsonValue boss = jsonReader.parse(Gdx.files.internal("moves/boss_moves.json"));

        // accurate Moves
        for (JsonValue move : base.get("accurate")) {
            Move m = new Move(move.getInt("type"), move.getString("name"),
                    move.getFloat("minDamage"), move.getFloat("maxDamage"));
            accurateMoves.add(m);
        }
        //System.out.println("accurate: " + accurateMoves.size);
        // wide Moves
        for (JsonValue move : base.get("wide")) {
            Move m = new Move(move.getInt("type"), move.getString("name"),
                    move.getFloat("minDamage"), move.getFloat("maxDamage"));
            wideMoves.add(m);
        }
        //System.out.println("wide: " + wideMoves.size);
        // crit Moves
        for (JsonValue move : base.get("crit")) {
            Move m = new Move(move.getString("name"), move.getFloat("damage"), move.getInt("crit"));
            critMoves.add(m);
        }
        //System.out.println("crit: " + critMoves.size);
        // heal Moves
        for (JsonValue move : base.get("healing")) {
            Move m = new Move(move.getInt("type"), move.getString("name"),
                    move.getFloat("minHeal"), move.getFloat("maxHeal"));
            healMoves.add(m);
        }
        //System.out.println("heal: " + healMoves.size);

        Array<Move> slimeMoves = new Array<Move>();
        // load boss moves
        for (JsonValue move : boss.get("slime")) {
            if (move.getInt("type") == 1)
                slimeMoves.add(new Move(1, move.getString("name"),
                        move.getFloat("minDamage"), move.getFloat("maxDamage")));
            else
                slimeMoves.add(new Move(3, move.getString("name"),
                        move.getFloat("minHeal"), move.getFloat("maxHeal")));
        }
        bossMoves.add(slimeMoves);

        Array<Move> lpMoves = new Array<Move>();
        for (JsonValue move : boss.get("loganpaul")) {
            if (move.getInt("type") == 0)
                lpMoves.add(new Move(0, move.getString("name"),
                        move.getFloat("minDamage"), move.getFloat("maxDamage")));
            else
                lpMoves.add(new Move(3, move.getString("name"),
                        move.getFloat("minHeal"), move.getFloat("maxHeal")));
        }
        bossMoves.add(lpMoves);
    }

    private void loadItems() {
        // parse items.json
        JsonValue itemPool = jsonReader.parse(Gdx.files.internal("items/items.json"));

        // load by rarity
        for (int i = 0; i < 4; i++) {
            loadItems(itemPool, i, "rare" + i);
        }
    }

    private void loadItems(JsonValue itemPool, int rarity, String r) {
        Array<Item> rare = new Array<Item>();
        for (JsonValue i : itemPool.get(r)) {
            switch (i.getInt("type")) {
                case 0:
                    rare.add(new Item(this, i.getString("name"), i.getString("desc"),
                            rarity, i.getInt("imgIndex"), i.getInt("hp"), i.getInt("sell")));
                    break;
                case 1:
                    rare.add(new Item(this, i.getString("name"), i.getString("desc"),
                            rarity, i.getInt("imgIndex"), i.getInt("sell")));
                    break;
                default:
                    rare.add(new Item(this, i.getString("name"), i.getString("desc"),
                            i.getInt("type"), rarity, i.getInt("imgIndex"), i.getInt("mhp"),
                            i.getInt("dmg"), i.getInt("acc"), i.getInt("sell")));
                    break;
            }
        }
        items.add(rare);
    }

    /**
     * Returns a copy of a random item from the item pool given rarity
     * This was made so that duplicate Item actors could be created
     *
     * @param rarity
     * @param rand
     * @return
     */
    public Item getItem(int rarity, Random rand) {
        Item item = items.get(rarity).get(rand.nextInt(items.get(rarity).size));
        if (item.type == 0)
            return new Item(this, item.name, item.desc, rarity, item.imgIndex, item.hp, item.sell);
        else if (item.type == 1)
            return new Item(this, item.name, item.desc, rarity, item.imgIndex, item.sell);
        else
            return new Item(this, item.name, item.desc, item.type, rarity, item.imgIndex, item.mhp, item.dmg, item.acc, item.sell);
    }

    /**
     * Returns a copy of an indexed Item from the item pool
     *
     * @param rarity
     * @param index
     * @return
     */
    public Item getItem(int rarity, int index) {
        Item item = items.get(rarity).get(index);
        if (item.type == 0)
            return new Item(this, item.name, item.desc, rarity, item.imgIndex, item.hp, item.sell);
        else if (item.type == 1)
            return new Item(this, item.name, item.desc, rarity, item.imgIndex, item.sell);
        else
            return new Item(this, item.name, item.desc, item.type, rarity, item.imgIndex, item.mhp, item.dmg, item.acc, item.sell);
    }

    /**
     * Returns a random Item from the item pool with weighted rarity
     *
     * @param rand
     * @return
     */
    public Item getRandomItem(Random rand) {
        int k = rand.nextInt(100);
        // common
        if (k < Util.COMMON_ITEM_RNG_INDEX) return getItem(0, rand);
        // rare
        else if (k < Util.RARE_ITEM_RNG_INDEX) return getItem(1, rand);
        // epic
        else if (k < Util.EPIC_ITEM_RNG_INDEX) return getItem(2, rand);
        // legendary
        else if (k < Util.LEGENDARY_ITEM_RNG_INDEX) return getItem(3, rand);

        return null;
    }

    /**
     * Returns a random Item from the item pool regardless of rarity
     *
     * @param rand
     * @return
     */
    public Item getRandomItemFromPool(Random rand) {
        return getItem(rand.nextInt(4), rand);
    }

    public void dispose() {
        assetManager.dispose();
    }

}

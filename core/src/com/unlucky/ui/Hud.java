package com.unlucky.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.unlucky.entity.enemy.Enemy;
import com.unlucky.entity.Player;
import com.unlucky.event.EventState;
import com.unlucky.inventory.Inventory;
import com.unlucky.inventory.Item;
import com.unlucky.map.TileMap;
import com.unlucky.map.WeatherType;
import com.unlucky.resource.ResourceManager;
import com.unlucky.resource.Util;
import com.unlucky.screen.GameScreen;

/**
 * Handles button input and everything not in the game camera
 *
 * @author Ming Li
 */
public class Hud extends UI {

    // Buttons
    // --------------------------------------------------------------------
    // directional pad: index i 0 - down, 1 - up, 2 - right, 3 - left
    private ImageButton[] dirPad;
    // if dir pad is held down
    private boolean touchDown = false;
    // for keyboard
    private boolean kTouchDown = false;
    private int dirIndex = -1;

    // option buttons: inventoryUI and settings
    private ImageButton[] optionButtons;

    public Hud(GameScreen gameScreen, TileMap tileMap, Player player, ResourceManager rm) {
        super(gameScreen, tileMap, player, rm);

        createDirPad();
        createOptionButtons();
    }

    public void update(float dt) {
        // handle movement based on button press
        if ((touchDown || kTouchDown) && !player.isOnSpecialTile()) movePlayer(dirIndex);
        else player.getAm().stopAnimation();

        kTouchDown = Gdx.input.isKeyPressed(Input.Keys.S) ||
                Gdx.input.isKeyPressed(Input.Keys.W) ||
                Gdx.input.isKeyPressed(Input.Keys.D) ||
                Gdx.input.isKeyPressed(Input.Keys.A);

        player.setContinueMoving(touchDown || kTouchDown);

        // keyboard input
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dirIndex = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dirIndex = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dirIndex = 2;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dirIndex = 3;
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            toggle(false);
            gameScreen.setCurrentEvent(EventState.INVENTORY);
            gameScreen.inventoryUI.start();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    handleCommands(text);
                }

                @Override
                public void canceled() {

                }
            }, "Debug Command Prompt", "", "");
        }
    }

    public void render(float dt) {
        stage.act(dt);
        stage.draw();

    }

    /**
     * Turns the HUD on and off when another event occurs
     *
     * @param toggle
     */
    public void toggle(boolean toggle) {
        if (toggle) {
            gameScreen.getGame().fps.setPosition(5, 115);
            stage.addActor(gameScreen.getGame().fps);
        }
        for (int i = 0; i < 4; i++) {
            dirPad[i].setDisabled(!toggle);
            dirPad[i].setTouchable(toggle ? Touchable.enabled : Touchable.disabled);
        }
        for (int i = 0; i < 2; i++) {
            optionButtons[i].setDisabled(!toggle);
            optionButtons[i].setTouchable(toggle ? Touchable.enabled : Touchable.disabled);
        }
    }

    /**
     * Draws the directional pad and applies Drawable effects
     * Unfortunately have to do each one separately
     */
    private void createDirPad() {
        dirPad = new ImageButton[4];

        // when each button is pressed it changes for a more visible effect
        ImageButton.ImageButtonStyle[] styles = rm.loadImageButtonStyles(4, rm.dirpad20x20);

        // down
        dirPad[0] = new ImageButton(styles[0]);
        dirPad[0].setPosition(Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET, Util.DIR_PAD_OFFSET);
        // up
        dirPad[1] = new ImageButton(styles[1]);
        dirPad[1].setPosition(Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET, (Util.DIR_PAD_SIZE * 2) + Util.DIR_PAD_OFFSET);
        // right
        dirPad[2] = new ImageButton(styles[2]);
        dirPad[2].setPosition((Util.DIR_PAD_SIZE * 2) + Util.DIR_PAD_OFFSET, Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET);
        // left
        dirPad[3] = new ImageButton(styles[3]);
        dirPad[3].setPosition(Util.DIR_PAD_OFFSET, Util.DIR_PAD_SIZE + Util.DIR_PAD_OFFSET);

        handleDirPadEvents();

        for (int i = 0; i < dirPad.length; i++) {
            stage.addActor(dirPad[i]);
        }
    }

    /**
     * Creates the two option buttons: inventoryUI and settings
     */
    private void createOptionButtons() {
        optionButtons = new ImageButton[2];

        ImageButton.ImageButtonStyle[] styles = rm.loadImageButtonStyles(2, rm.optionbutton32x32);
        for (int i = 0; i < 2; i++) {
            optionButtons[i] = new ImageButton(styles[i]);
            optionButtons[i].setPosition(155 + (i * 25), 100);
            stage.addActor(optionButtons[i]);
        }
        handleOptionEvents();
    }

    /**
     * Handles player movement commands
     */
    private void handleDirPadEvents() {
        for (int i = 0; i < 4; i++) {
            final int index = i;
            dirPad[i].addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    touchDown = true;
                    dirIndex = index;
                    return true;
                }
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    touchDown = false;
                }
            });
        }
    }

    /**
     * Handles two option button commands
     */
    private void handleOptionEvents() {
        // inventoryUI
        optionButtons[0].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggle(false);
                gameScreen.setCurrentEvent(EventState.INVENTORY);
                gameScreen.inventoryUI.start();
            }
        });

        // @TODO CHANGE
        // command prompt for now
        optionButtons[1].addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        handleCommands(text);
                    }

                    @Override
                    public void canceled() {

                    }
                }, "Debug Command Prompt", "", "");
            }
        });
    }

    /**
     * Simple commands:
     * /heal
     * /tp [tileX] [tileY] (teleports the player to a tile coordinate)
     * /sethp [hp] (sets hp of player)
     * /setmaxhp [maxHp] (sets max hp of player)
     * /randitem (adds a random item from the item pool weighted by rarity)
     * /item [rarity] (adds a random item of a given rarity 0-3)
     * /setweather [weatherId] (0 - none, 1 - rain, 2 - heavy rain, 3 - thunderstorm, 4 - snow, 5 - blizzard)
     * /addentity [entityID] [tileX] [tileY] (adds an entity to a a tile position)
     * /removeentity [tileX] [tileY] (removes the entity at a tile position)
     * /togglenight [boolean] (toggles night time on or off with true or false)
     * /fillinv (fills the inventory with random items)
     * /clearinv (clear the inventory)
     * /fillrarity [rarity] (fills the inventory with items of a given rarity)
     * /levelup [exp] (levels up the player based on a given amount of exp)
     * /battle [entityId] (automatically starts a battle with the given entity id)
     * /setacc [acc] (sets the accuracy of the player)
     * /setsmovecd [cd] (sets the cooldown of special moves based on num of turns; 0 for no cd to test the icons)
     * (0 - distract, 1 - focus, 2 - intimidate, 3 - reflect, 4 - stun, 5 - invert, 6 - sacrifice, 7 - shield)
     * /addsmove [smoveId] (adds a smove to the player's current smoveset)
     * /setsmoveset [smoveId0] [smoveId1] ... [smoveId4] (clears and sets the player's smoveset and adds up to 5 smoves)
     * /clearsmoveset (clears the player's smoveset)
     *
     * @param command
     */
    private void handleCommands(String command) {
        String cmd = command.trim();
        if (eq(cmd, "/heal")) player.setHp(player.getMaxHp());
        if (cmd.startsWith("/tp")) {
            String[] input = cmd.split(" ");
            if (input.length == 3) {
                int x = Integer.parseInt(input[1]);
                int y = Integer.parseInt(input[2]);
                if (x >= 0 && x < tileMap.mapWidth && y >= 0 && y < tileMap.mapHeight) {
                    player.setPosition(tileMap.toMapCoords(new Vector2(x, y)));
                }
            }
        }
        if (cmd.startsWith("/sethp")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                player.setHp(Integer.parseInt(input[1]));
            }
        }
        if (cmd.startsWith("/setmaxhp")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                player.setMaxHp(Integer.parseInt(input[1]));
            }
        }
        if (eq(cmd, "/randitem")) {
            Item i = rm.getRandomItem();
            i.adjust(player.getLevel());
            player.inventory.addItem(i);
        }
        if (cmd.startsWith("/item")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int rarity = Integer.parseInt(input[1]);
                if (rarity >= 0 && rarity < 4) {
                    Item i = rm.getItem(rarity);
                    i.adjust(player.getLevel());
                    player.inventory.addItem(i);
                }
            }
        }
        if (cmd.startsWith("/setweather")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int wid = Integer.parseInt(input[1]);
                if (wid >= 0 && wid < 6) {
                    if (wid == 0) gameScreen.gameMap.setWeather(WeatherType.NORMAL);
                    if (wid == 1) gameScreen.gameMap.setWeather(WeatherType.RAIN);
                    if (wid == 2) gameScreen.gameMap.setWeather(WeatherType.HEAVY_RAIN);
                    if (wid == 3) gameScreen.gameMap.setWeather(WeatherType.THUNDERSTORM);
                    if (wid == 4) gameScreen.gameMap.setWeather(WeatherType.SNOW);
                    if (wid == 5) gameScreen.gameMap.setWeather(WeatherType.BLIZZARD);
                }
            }
        }
        if (cmd.startsWith("/addentity")) {
            String[] input = cmd.split(" ");
            if (input.length == 4) {
                int entityId = Integer.parseInt(input[1]);
                int x = Integer.parseInt(input[2]);
                int y = Integer.parseInt(input[3]);
                if (x >= 0 && x < tileMap.mapWidth && y >= 0 && y < tileMap.mapHeight) {
                    tileMap.addEntity(Util.getEntity(entityId, tileMap.toMapCoords(x, y), tileMap, rm), x, y);
                }
            }
        }
        if (cmd.startsWith("/removeentity")) {
            String[] input = cmd.split(" ");
            if (input.length == 3) {
                int x = Integer.parseInt(input[1]);
                int y = Integer.parseInt(input[2]);
                if (x >= 0 && x < tileMap.mapWidth && y >= 0 && y < tileMap.mapHeight) {
                    tileMap.removeEntity(x, y);
                }
            }
        }
        if (cmd.startsWith("/togglenight")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                boolean toggle = Boolean.parseBoolean(input[1]);
                gameScreen.gameMap.setDarkness(toggle);
            }
        }
        if (eq(cmd, "/fillinv")) {
            player.inventory.clear();
            for (int i = 0; i < Inventory.NUM_SLOTS; i++) {
                Item item = rm.getRandomItem();
                item.adjust(player.getLevel());
                player.inventory.addItem(item);
            }
        }
        if (eq(cmd, "/clearinv")) {
            player.inventory.clear();
        }
        if (cmd.startsWith("/fillrarity")) {
            player.inventory.clear();
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int rarity = Integer.parseInt(input[1]);
                for (int i = 0; i < Inventory.NUM_SLOTS; i++) {
                    Item item = rm.getItem(rarity);
                    item.adjust(player.getLevel());
                    player.inventory.addItem(item);
                }
            }
        }
        if (cmd.startsWith("/levelup")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int exp = Integer.parseInt(input[1]);
                player.levelUp(exp);
                player.applyLevelUp();
            }
        }
        if (cmd.startsWith("/battle")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int entityId = Integer.parseInt(input[1]);
                if (entityId >= 2 && entityId <= 4) {
                    player.setBattling((Enemy) Util.getEntity(entityId, new Vector2(), tileMap, rm));
                }
            }
        }
        if (cmd.startsWith("/setacc")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int acc = Integer.parseInt(input[1]);
                if (acc >= 0) player.setAccuracy(acc);
            }
        }
        if (cmd.startsWith("/setsmovecd")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int cd = Integer.parseInt(input[1]);
                if (cd >= 0) player.smoveCd = cd;
            }
        }
        if (cmd.startsWith("/addsmove")) {
            String[] input = cmd.split(" ");
            if (input.length == 2) {
                int smoveId = Integer.parseInt(input[1]);
                if (smoveId >= 0 && smoveId < Util.NUM_SPECIAL_MOVES) {
                    player.smoveset.addSMove(smoveId);
                }
            }
        }
        if (cmd.startsWith("/setsmoveset")) {
            String[] input = cmd.split(" ");
            if (input.length > 1 && input.length <= 6) {
                player.smoveset.clear();
                for (int i = 1; i < input.length; i++) {
                    int smoveId = Integer.parseInt(input[i]);
                    if (smoveId >= 0 && smoveId < Util.NUM_SPECIAL_MOVES) {
                        player.smoveset.addSMove(smoveId);
                    }
                }
            }
        }
        if (eq(cmd, "/clearsmoveset")) {
            player.smoveset.clear();
        }
        if (eq(cmd, "/exit")) {
            gameScreen.getGame().menuScreen.transitionIn = 0;
            gameScreen.getGame().setScreen(gameScreen.getGame().menuScreen);
        }
    }

    private boolean eq(String s1, String s2) {
        return s1.equalsIgnoreCase(s2);
    }

    private void movePlayer(int dir) {
        if (player.canMove()) player.getAm().setAnimation(dir);
        if (player.canMove() && !player.nextTileBlocked(dir)) {
            player.move(dir);
        }
    }

}
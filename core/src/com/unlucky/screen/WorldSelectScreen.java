package com.unlucky.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.unlucky.main.Unlucky;
import com.unlucky.resource.ResourceManager;

/**
 * Allows the player to select the world to battle in
 * Displays options for worlds with a scroll pane and renders an background
 * according to the world
 * For each world, displays the description and level range of the world
 *
 * @author Ming Li
 */
public class WorldSelectScreen extends DoubleDimensionScreen {

    private static final int NUM_WORLDS = 5;

    // current world selection index that determines bg and descriptions
    private int currentWorldIndex = 0;

    private String[] worldStrs = {
        "SLIME FOREST",
        "????????????????????",
        "????????????????????",
        "????????????????????",
        "????????????????????"
    };

    // Scene2D scroll pane
    private Table scrollTable;
    private Table selectionContainer;
    private ScrollPane scrollPane;

    public WorldSelectScreen(final Unlucky game, final ResourceManager rm) {
        super(game, rm);

        scrollTable = new Table();
        scrollTable.setFillParent(true);
        stage.addActor(scrollTable);

        selectionContainer = new Table();
        for (int i = 0; i < NUM_WORLDS; i++) {
            TextButton b = new TextButton(worldStrs[i], rm.skin);
            selectionContainer.add(b).size(160, 40).row();
        }
        selectionContainer.pack();
        selectionContainer.setTransform(true);
        selectionContainer.setOrigin(selectionContainer.getWidth() / 2,
                                     selectionContainer.getHeight() / 2);

        scrollPane = new ScrollPane(selectionContainer, rm.skin);
        scrollTable.add(scrollPane).size(180, 185).fill();
        scrollTable.setPosition(-85, 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        renderBatch = false;
        batchFade = true;
        // fade in animation
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.run(new Runnable() {
            @Override
            public void run() {
                renderBatch = true;
            }
        }), Actions.fadeIn(0.5f)));
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            batchFade = false;
            // fade out animation
            stage.addAction(Actions.sequence(Actions.fadeOut(0.3f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        renderBatch = false;
                        game.setScreen(game.menuScreen);
                    }
                })));
        }
    }

    public void render(float dt) {
        update(dt);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (renderBatch) {
            stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
            stage.getBatch().begin();

            // fix fading
            if (batchFade) stage.getBatch().setColor(Color.WHITE);

            // render world background corresponding to the selected world
            // possibly expensive scaling call?
            stage.getBatch().draw(rm.worldSelectBackgrounds[currentWorldIndex], 0, 0,
                Unlucky.V_WIDTH * 2, Unlucky.V_HEIGHT * 2);

            //game.profile("WorldSelectScreen");

            stage.getBatch().end();
        }

        super.render(dt);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}

package com.gempukku.gdx.assistant;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.plugins.PluginRegistration;
import com.gempukku.gdx.plugins.PluginsProvider;
import com.gempukku.gdx.plugins.jar.JarsPluginsProvider;
import com.kotcrab.vis.ui.VisUI;

import java.io.File;
import java.util.function.Function;

public class GdxAssistant extends ApplicationAdapter {
	private final PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider;
	private AssistantScreen assistantScreen;
	private PluginRegistration<AssistantApplication, AssistantPlugin> pluginRegistration;

	private Skin skin;
	private ScreenViewport viewport;
	private Stage stage;
	private float uiScale = 1f;

	public GdxAssistant(PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider) {
		this.pluginsProvider = pluginsProvider;
	}

	@Override
	public void create () {
		pluginsProvider.loadPlugins();
		Gdx.files = pluginsProvider.getPluginFiles();

		skin = new Skin(Gdx.files.internal("skin/visui/uiskin.json"));
		VisUI.load(skin);

		viewport = new ScreenViewport();
		viewport.setUnitsPerPixel(uiScale);
		stage = new Stage(viewport);

		assistantScreen = new AssistantScreen(pluginsProvider, skin);
		assistantScreen.setFillParent(true);
		stage.addActor(assistantScreen);
		// Support for switching the UI scale
		stage.addListener(
				new InputListener() {
					@Override
					public boolean keyDown(InputEvent event, int keycode) {
						if (keycode == Input.Keys.F12 && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
							uiScale = (uiScale == 1f) ? 0.5f : 1f;
							viewport.setUnitsPerPixel(uiScale);
							resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
							return true;
						}
						return false;
					}
				});

		pluginRegistration = new PluginRegistration<>();
		pluginRegistration.registerPlugins(pluginsProvider,
				new Function<AssistantPlugin, AssistantApplication>() {
					@Override
					public AssistantApplication apply(AssistantPlugin assistantPlugin) {
						return assistantScreen.createApplicationForPlugin(assistantPlugin);
					}
				});

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void render() {
		assistantScreen.processUpdate(Gdx.graphics.getDeltaTime());
		stage.act();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.draw();
	}
	
	@Override
	public void dispose () {
		pluginRegistration.deregisterPlugins();
		VisUI.dispose();
	}
}

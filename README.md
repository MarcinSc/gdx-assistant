# gdx-assistant

A library for [libGDX](https://libgdx.badlogicgames.com/ "libGDX") cross-platform game development framework.

This library allows to manage and generate external resources in your [libGDX](https://libgdx.badlogicgames.com/ "libGDX")
projects. Those tasks are handled by plugins, which are loaded at runtime from the `plugins` folder. 

## Plugins
Here is a list of currently available plugins. To use them in your gdx-assistant, download the plugin jar from the website
listed under the plugin, and put it in `plugins` folder within a folder, where you have your gdx-assistant jar.

So your file structure should look like this:
```
[in some folder]
- plugins
    - gdx-graph-assistant-1.0.0.jar
    - [... other plugins]
- gdx-assistant-desktop-1.0.0.jar
```

### gdx-graph
Allows for creating graphs that you might use in your projects. Currently, the only supported type of graph is
`RenderingPipeline`, which handles rendering for you. More information available at
[Gempukku Studio YouTube channel](https://www.youtube.com/GempukkuStudio)

Plugin available for download at:<br>
https://github.com/MarcinSc/gdx-graph/releases

## Creating your own plugin
You can create your own plugin, to do so, add the following to your project dependency:
```
implementation "com.github.MarcinSc.gdx-assistant:gdx-assistant-plugin:$gdxAssistantVersion"
```

You also have to make sure, that the generated JAR that is a plugin has a `Gdx-Assistant-Plugin` attribute in its manifest
with a class name that implements `AssistantPlugin` interface, which will serve as an entry point for the gdx-assistant.

You can do it with the following snippet:
```
jar {
    manifest {
        attributes(
                'Gdx-Assistant-Plugin': "[full package and class name]",
        )
    }
}
```

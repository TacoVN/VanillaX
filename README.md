# VanillaX

VanillaX is a simple client-side mod designed to enhance your Vanilla Minecraft experience. Its features are based on my in-depth research into Minecraft for years

## Features

- **Gamma 5.0:** Configure brightness using gamma 5.0 **(patched in 1.19+)**
- **Permanent Sprint:** Always sprint, regardless of Hold/Toggle mode
- **Fog:** Remove some fog effects (water, lava,...)
- **Rendering:** Configure shield, fire, totem rendering, **and more!**
- **Bug Fixes:** Fix some Vanilla Minecraft "features"
- **Easter Egg:** An old hidden Vanilla Minecraft feature **(patched in 1.18)**, can you spot it?

## Configuration

VanillaX can be configured in-game via **Options** ➡ **Credits & Attribution** ➡ **VanillaX**

Works best with [Sodium](https://modrinth.com/mod/sodium)

## Settings Reference

The settings are grouped into **General**, **Shield & Fire**, **Totem**, **Overlay**, and **Others** sections with bold, underlined headers, matching the style of the vanilla Key Binds screen.

| Setting | Range | Default | Description |
| --- | --- | --- | --- |
| Permanent Sprint | On/Off | On | On makes the player sprint whenever they move forward, independent of whether Sprint is set to Toggle or Hold |
| Fog | On/Off | Off | Off removes the fog while underwater or inside lava, and the atmospheric fog of the Nether and the End; On restores vanilla. The terrain fog (render distance) stays vanilla in every dimension, and powder snow and the blindness/darkness fog are never touched |
| Eating Particles | On/Off | Off | On keeps the vanilla particles while eating or drinking; Off removes them |
| Explosion Particles | On/Off | Off | On keeps the vanilla particles from TNT, crystals, and other explosions; Off removes them |
| Clean Hitboxes | On/Off | On | On strips the red eye line and blue view arrow from entity hitboxes, leaving only the white box |
| Shield Height | 0 to 100 | 50 | Vertical position of the shield in first person |
| Fire Height | 0 to 100 | 20 | Vertical position of the screen fire overlay |
| Totem Scale | 0 to 100 | 80 | Size of the totem held in hand |
| Totem Pop Scale | 0 to 100 | 20 | Size of the totem activation animation |
| Totem Pop Height | 0 to 100 | 50 | Vertical position of the totem activation animation |
| Pumpkin Opacity | 0 to 100 | 70 | Opacity of the pumpkin overlay while wearing a carved pumpkin |
| Spyglass Opacity | 0 to 100 | 70 | Opacity of the spyglass overlay while scoping |
| Vanilla Video Settings | On/Off | On | On keeps the vanilla Video Settings screen even when another mod (Sodium, for example) replaces it; applies both at the main menu and in-game |
| Simple Mode | On/Off | Off | Only editable while Vanilla Video Settings is On. On trims the vanilla video screen down to the options below at the main menu and in-game; hidden options keep their values |
| Crosshair Fix | On/Off | On | On centers the crosshair exactly in the middle of the screen instead of half a pixel off |
| Wither Fix | On/Off | On | On recolors the withered hearts with the `#736156` tone so the effect is easier to notice |
| Riptide Fix | On/Off | On | On keeps the held item in its normal first person position during a Riptide spin attack instead of letting the item cover the view |
| Easter Egg | On/Off | Off | On removes the dark block overlay that covers the screen when your head is stuck inside sand, gravel, and similar blocks |

Brightness is not part of this screen: the mod always patches the vanilla **Brightness** slider so it
can reach **500%** (gamma 5.0) instead of stopping at 100%. The limit is fixed, so there is nothing to
toggle - see `GammaValueSet` and `GammaMixin`.

**Vanilla Video Settings** puts the vanilla Video Settings screen back when a rendering mod replaces it; the override is applied both at the main menu and in-game.

**Simple Mode** needs **Vanilla Video Settings** to be **On**; otherwise its button is locked and the feature stays inactive (the stored value is kept). When active it hides options in the vanilla video screen without changing their values:

| Section | Kept | Hidden |
| --- | --- | --- |
| Display | Brightness, Max Framerate, GUI Scale | Exclusive Fullscreen Mode, VSync, Reduce fps when..., Fullscreen, Exclusive Fullscreen, Graphics API |
| Quality & Performance | Render Distance, Simulation Distance, Entity Distance, Particles | Preset, Biome Blend, Chunk Builder, Smooth Lighting, Clouds, Mipmap Levels, Entity Shadows, Menu Background Blur, Cloud Distance, See-Through Leaves, Improved Transparency, Texture Filtering, Anisotropic Filtering, Weather Effect Radius |
| Preferences | - | The whole section: Autosave Indicator, Show Vignette, Attack Indicator, Chunk Fade |

For **Clean Hitboxes**, **On** keeps only the white hitbox outline (the vanilla red eye line and blue view arrow are hidden), and **Off** restores the full vanilla debug overlay. For the particle toggles, **On** restores the vanilla particles and **Off** removes them. **Crosshair Fix**, **Wither Fix**, and **Easter Egg** are **On** when the fix is active and **Off** for vanilla behaviour. All sliders use **0 to 100**; defaults are listed in the table, and **100** is vanilla for every slider. For the height sliders, lowering the value moves the element down at a constant speed, and **0** is just enough to move it off-screen. For the size sliders, **100** is the vanilla size and **0** is the smallest. For the opacity sliders, lowering the value fades the element at a constant rate, and **0** makes it fully transparent. Settings are stored in `config/vanillax.json` and applied immediately when a widget is changed.

## Others

Share your ideas, bug reports,... on my [Discord](https://discord.gg/n8WfxjEacE) server

Feel free to use this mod in your modpacks or Youtube/Tiktok videos without asking for permission. **Do not redistribute anywhere else!**

## Requirements

- Minecraft 26.3
- Fabric Loader 0.19.5 or newer
- Java 25
- Fabric API is **not** required

## Installation

1. Build the mod or use `release/vanillax-1.0.0.jar`.
2. Place the jar in your Minecraft `mods` folder.
3. Launch the game with the Fabric profile for Minecraft 26.3.

## Building

Use a Java 25 JDK:

```bash
./gradlew build
```

The built mod is written to `build/libs/vanillax-1.0.0.jar`.

## License

This project is available under the CC0-1.0 license.

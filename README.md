# Messy Craft

Messy Craft is a nbt crafting library runs on server-side.

## Command

- `/craft <recipe> [<times>]` Craft recipes.
- `/c <recipe> [<times>]` Alias of `/craft`.

## Recipe

Recipes can be configured by data packs in `data/MY_NAMESPACE/messy_recipe/.../MY_RECIPE.json`. (where `MY_NAMESPACE` and `MY_RECIPE` should be replaced, obviously)

Here is a template recipe file.

```json
{
    "ingredients": [
        {
            "count": 1,
            "id": "minecraft:stick",
            "components": {
                "minecraft:rarity": "uncommon"
            }
        }
    ],
    "result": {
        "count": 1,
        "id": "minecraft:milk_bucket",
        "components": {
            "minecraft:item_name": "Milky Water"
        }
    }
}
```

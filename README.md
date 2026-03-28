# JStove

**Warning**: This project is in early development and is not yet stable. Use at your own risk.

JStove is a Java library for parsing JSON-LD Recipe data in a structured and predictable way. Every recipe is parsed and
normalized into a consistent format, making it easier to work with recipe data from various sources.

## Features

- Parses JSON-LD Recipe data into a structured format.
- Normalizes recipe data to ensure consistency across different sources.
- Provides a simple API for accessing recipe information.
- Handles common recipe properties such as ingredients, instructions, cooking time, and more.

## Supported Fields Checklist
- [x] name
- [x] description
- [x] prepTime
- [x] cookTime
- [x] totalTime
- [x] recipeYield
- [x] recipeCategory
- [x] recipeCuisine
- [x] cookingMethod
- [x] keywords
- [x] recipeIngredient
- [x] recipeInstructions
- [x] nutrition
- [x] author
- [x] dateCreated
- [x] dateModified
- [x] datePublished
- [x] aggregateRating
- [ ] video
- [x] url
- [x] suitableForDiet (just RestrictedDiet for now)
- [ ] estimatedCost
- [x] performTime
- [ ] step (supersedes steps)
- [ ] supply
- [ ] tool
- [ ] about
- [ ] comment
- [ ] commentCount
- [ ] review(s)
- [ ] reviewCount

## Example Usage

```java
String json = "..."; // Or InputStream, Reader, etc.
var parser = RecipeParsers.defaultLenientParser();
ParseResult result = parser.parse(json);

if(result.hasWarnings()) {
   // Handle warnings
}

Recipe recipe = result.recipe();
System.out.println("Recipe Name: " + recipe.name());
System.out.println("Ingredients: " + recipe.ingredients());
System.out.println("Instructions: " + recipe.instructions());
```

## Installation

TODO

## Contributing

Contributions are currently not accepted as the project is in early development. However, if you have suggestions or
want to contribute in the future, please feel free to reach out.

Development takes place in the `develop` branch.

## License

This project is licensed under the GNU General Public License v3.0. See the LICENSE file for details.

## Roadmap

- [ ] NLP for better parsing of units
- [ ] Unit conversion
- [ ] Support for direct URL import (maybe out of scope)

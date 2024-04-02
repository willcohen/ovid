# ovid

[![Clojars Project](https://img.shields.io/clojars/v/ovid.svg)](https://clojars.org/ovid)

Geospatial metamorphoses in Clojure

## Usage

Pre-alpha work in progress.

## Implemented

### ovid.feature

Creation of a `Featurelike` protocol to wrap [geo](https://github.com/Factual/geo)'s `Shapelike`. The `Featurelike` protocol allows for transition between `Shapelike` objects and maps containing `:geometry` and `:properties` keywords.

Spatial operations on a `Shapelike` will operate directly on that object, and spatial operations on a `Featurelike` array will operate on its `:geometry`.

## In progress

### ovid.io

Ability to read the geometries from a shapefile's `.shp`.

### ovid.feature.specs

Preliminary support for `Featurelike` functions and accompanying generators

## Todo

- Read `.prj` and `.dbf`, and combine `.shp`, `.prj`, and `.dbf` into clearer method for reading shapefiles
- Consider use of transducers in shapefile IO to allow for greater flexibility in input data
- Complete specs and testing for `ovid.feature`

## Testing

Using `tools.deps.alpha`, run `clj -A:test:runner`. Depends on the (still incomplete) specs and generators defined in `ovid.feature.specs` to generatively test the functions in `ovid.feature`.

## License

```
Copyright (c) 2019, 2020, 2024 Will Cohen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

# ovid

Geospatial metamorphoses

## Usage

Pre-alpha work in progress.

## Implemented

Creation of a `Featurelike` protocol to wrap [geo](https://github.com/Factual/geo)'s `Shapelike`. The `Featurelike` protocol allows for transition between `Shapelike` objects and maps containing `:geometry` and `:properties` keywords.

Spatial operations on a `Shapelike` will operate directly on that object, and spatial operations on a `Featurelike` array will operate on its `:geometry`.

## Todo

* Specs of Featurelike and accompanying generators
* `.shp` input/output

## License

```
Copyright (c) 2019 Will Cohen

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

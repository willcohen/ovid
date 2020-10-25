;; Copyright (c) 2019, 2020 Will Cohen
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns ovid.feature
  (:require [geo.spatial :as geo.spatial]
            [geo.geohash :as geo.geohash]
            [geo.h3 :as geo.h3])
  (:import (ch.hsr.geohash GeoHash WGS84Point)
           (com.uber.h3core.util GeoCoord)
           (org.locationtech.jts.geom Geometry)
           (org.locationtech.spatial4j.shape Shape)
           (org.locationtech.spatial4j.shape.impl
            GeoCircle PointImpl RectangleImpl)
           (org.locationtech.spatial4j.shape.jts JtsGeometry
                                                 JtsPoint)))

(set! *warn-on-reflection* true)

(defprotocol Featurelike
  (-to-shape [this]
    "Internal helper for to-shape")
  (-to-jts [this] [this srid] [this c1 c2] [this c1 c2 geometry-factory]
    "Internal helper for to-jts")
  (-to-feature [this] [this properties]
    "Internal helper for to-feature")
  (-geometry [this]
    "Internal helper for geometry")
  (-properties [this]
    "Internal helper for properties")
  (-assoc-geometry [this s]
    "Internal helper for assoc-geometry")
  (-update-geometry [this f] [this f & args]
    "Internal helper for update-geometry")
  (-assoc-properties [this p]
    "Internal helper for assoc-properties")
  (-update-properties [this f]
    "Internal helper for update-properties"))

(extend-protocol Featurelike
  GeoCircle
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  RectangleImpl
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  PointImpl
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  JtsGeometry
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  JtsPoint
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (-to-jts this srid))
    ([this c1 c2] (-to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (-to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  Geometry
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  GeoCoord
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  GeoHash
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {:geohash (geo.geohash/string this)}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  WGS84Point
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid))
    ([this c1 c2] (geo.spatial/to-jts this c1 c2))
    ([this c1 c2 geometry-factory]
     (geo.spatial/to-jts this c1 c2 geometry-factory)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-geometry [this] this)
  (-properties [this] {})
  (-assoc-geometry [this s] s)
  (-update-geometry
    ([this f] (f this))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  clojure.lang.PersistentArrayMap
  (-to-shape [this] (-to-shape (:geometry this)))
  (-to-jts
    ([this] (-to-jts (:geometry this)))
    ([this srid] (-to-jts (:geometry this) srid))
    ([this c1 c2] (-to-jts (:geometry this) c1 c2))
    ([this c1 c2 geometry-factory]
     (-to-jts (:geometry this) c1 c2 geometry-factory)))
  (-to-feature
    ([this] this)
    ([this properties] {:geometry (:geometry this) :properties properties}))
  (-geometry [this] (:geometry this))
  (-properties [this] (:properties this))
  (-assoc-geometry [this s] (assoc this :geometry s))
  (-update-geometry
    ([this f] (update this :geometry f))
    ([this f & args]
     (-update-geometry
      this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (assoc this :properties p))
  (-update-properties [this f] (update this :properties f))

  clojure.lang.PersistentHashMap
  (-to-shape [this] (-to-shape (:geometry this)))
  (-to-jts
    ([this] (-to-jts (:geometry this)))
    ([this srid] (-to-jts (:geometry this) srid))
    ([this c1 c2] (-to-jts (:geometry this) c1 c2))
    ([this c1 c2 geometry-factory]
     (-to-jts (:geometry this) c1 c2 geometry-factory)))
  (-to-feature
    ([this] this)
    ([this properties] {:geometry (:geometry this) :properties properties}))
  (-geometry [this] (:geometry this))
  (-properties [this] (:properties this))
  (-assoc-geometry [this s] (assoc this :geometry s))
    (-update-geometry
     ([this f] (update this :geometry f))
     ([this f & args]
      (-update-geometry
       this (fn [x] (apply f (cons x args))))))
  (-assoc-properties [this p] (assoc this :properties p))
  (-update-properties [this f] (update this :properties f)))

(defn h3->feature
  [h3]
  {:geometry (geo.h3/to-jts h3) :properties {:h3 h3}})

(defn ^Shape to-shape
  "Get Shape from geometry of Featurelike
  (wrapping Shapelike from geo library)."
  [this]
  (-to-shape this))

(defn to-jts
  "Convert geometry of Featurelike to a projected JTS Geometry
  (wrapping Shapelike from geo library)."
  (^Geometry [this]
   (-to-jts this))
  (^Geometry [this srid]
   (-to-jts this srid))
  (^Geometry [this c1 c2]
   (-to-jts this c1 c2))
  (^Geometry [this c1 c2 geometry-factory]
   (-to-jts this c1 c2 geometry-factory)))

(defn to-feature
  "Convert anything to a Featurelike."
  ([this]
   (-to-feature this))
  ([this properties]
   (-to-feature this properties)))

(defn geometry
  "Get the geometry from a Featurelike."
  [this]
  (-geometry this))

(defn properties
  "Get the properties from a Featurelike.
  If the Featurelike is a Shapelike, returns an empty map."
  [this]
  (-properties this))

(defn assoc-geometry
  "Associate Featurelike with new Shapelike geometry s."
  [this s]
  (-assoc-geometry this s))

(defn update-geometry
  "Update Featurelike by applying f to existing Shapelike geometry."
  [this f]
  (-update-geometry this f))

(defn assoc-properties
  "Associate Featurelike with new properties p."
  [this p]
  (-assoc-properties this p))

(defn update-properties
  "Update Featurelike by applying f to existing properties."
  [this f]
  (-update-properties this f))

(defn jts-geometry
  "Convert a Feature's geometry to JTS."
  ([feat]
   (update-geometry feat to-jts))
  ([feat srid]
   (update-geometry feat (fn [x] (to-jts x srid))))
  ([feat c1 c2]
   (update-geometry feat (fn [x] (to-jts x c1 c2))))
  ([feat c1 c2 geometry-factory]
   (update-geometry
    feat (fn [x] (to-jts x c1 c2 geometry-factory)))))

(defn shape-geometry
  "Convert a Feature's geometry to spatial4j."
  [feat]
  (update-geometry feat to-shape))

(defn feature?
  "WARNING: this assumes feature is implemented as a hashmap, and uses duck-typing"
  [feat]
  (and (map? feat) (contains? feat :geometry) (contains? feat :properties)))

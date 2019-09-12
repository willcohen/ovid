;; Copyright (c) 2019 Will Cohen
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
           (org.locationtech.spatial4j.shape.impl
            GeoCircle PointImpl RectangleImpl)
           (org.locationtech.spatial4j.shape.jts JtsGeometry
                                                 JtsPoint)))

(set! *warn-on-reflection* true)

(defprotocol Featurelike
  (-to-shape [this]
    "Internal helper for to-shape")
  (-to-jts [this] [this srid]
    "Internal helper for to-jts")
  (-to-feature [this] [this properties]
    "Internal helper for to-feature")
  (-assoc-geometry [this s]
    "Internal helper for assoc-geometry")
  (-update-geometry [this f]
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
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  RectangleImpl
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  PointImpl
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  JtsGeometry
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  JtsPoint
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  Geometry
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  GeoCoord
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  GeoHash
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {:geohash (geo.geohash/string this)}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  WGS84Point
  (-to-shape [this] (geo.spatial/to-shape this))
  (-to-jts
    ([this] (geo.spatial/to-jts this))
    ([this srid] (geo.spatial/to-jts this srid)))
  (-to-feature
    ([this] (-to-feature this {}))
    ([this properties] {:geometry this :properties properties}))
  (-assoc-geometry [this s] s)
  (-update-geometry [this f] (f this))
  (-assoc-properties [this p] (-to-feature this p))
  (-update-properties [this f] (-update-properties (-to-feature this) f))

  clojure.lang.PersistentArrayMap
  (-to-shape [this] (-to-shape (:geometry this)))
  (-to-jts
    ([this] (-to-jts (:geometry this)))
    ([this srid] (-to-jts (:geometry this) srid)))
  (-to-feature
    ([this] this)
    ([this properties] {:geometry (:geometry this) :properties properties}))
  (-assoc-geometry [this s] (assoc this :geometry s))
  (-update-geometry [this f] (update this :geometry f))
  (-assoc-properties [this p] (assoc this :properties p))
  (-update-properties [this f] (update this :properties f))

  clojure.lang.PersistentHashMap
  (-to-shape [this] (-to-shape (:geometry this)))
  (-to-jts
    ([this] (-to-jts (:geometry this)))
    ([this srid] (-to-jts (:geometry this) srid)))
  (-to-feature
    ([this] this)
    ([this properties] {:geometry (:geometry this) :properties properties}))
  (-assoc-geometry [this s] (assoc this :geometry s))
  (-update-geometry [this f] (update this :geometry f))
  (-assoc-properties [this p] (assoc this :properties p))
  (-update-properties [this f] (update this :properties f)))

(defn h3->feature
  [h3]
  {:geometry (geo.h3/to-jts h3) :properties {:h3 h3}})

(defn to-shape
  "Get Shape from geometry of Featurelike (wrapping Shapelike from geo library)."
  [this]
  (-to-shape this))

(defn to-jts
  "Convert geometry of Featurelike to a projected JTS Geometry (wrapping Shapelike from geo library)."
  ([this]
   (-to-jts this))
  ([this srid]
   (-to-jts this srid)))

(defn to-feature
  "Convert anything to a Featurelike."
  ([this]
   (-to-feature this))
  ([this properties]
   (-to-feature this properties)))

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
  [feat]
  (update-geometry feat to-jts))

(defn shape-geometry
  [feat]
  (update-geometry feat to-shape))

(defn transform
  ([feature t]
   (update-geometry
    (jts-geometry feature)
    #(geo.jts/transform-geom % t)))
  ([feature c1 c2]
   (update-geometry
    (jts-geometry feature)
    #(geo.jts/transform-geom % c1 c2))))

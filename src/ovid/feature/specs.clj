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

(ns ovid.feature.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [geo.spatial :as geo.spatial]
            [geo.geohash :as geo.geohash]
            [geo.h3 :as geo.h3]
            [ovid.feature :as ovid.feature])
  (:import (ch.hsr.geohash GeoHash WGS84Point)
           (com.uber.h3core.util GeoCoord)
           (org.locationtech.spatial4j.shape Shape
                                             Rectangle)
           (org.locationtech.jts.geom Coordinate
                                      Geometry
                                      LineSegment
                                      LineString
                                      Point)
           (org.locationtech.spatial4j.shape.impl GeoCircle PointImpl RectangleImpl)
           (org.locationtech.spatial4j.shape.jts JtsGeometry
                                                 JtsPoint)))

(set! *warn-on-reflection* true)


(defn featurelike?
  [f]
  (satisfies? ovid.feature/Featurelike f))

(defn feature?
  [f]
  (and (featurelike? (:geometry f))
       (map? (:properties f))))

(s/def ::x-coord double?)
(s/def ::y-coord double?)
(s/def ::lat (s/and number? #(<= -90 % 90)))
(s/def ::lng (s/and number? #(<= -180 % 180)))

(s/def ::jts-coordinate (s/with-gen #(instance? Coordinate %)
                          #(gen/fmap (fn [[x y]] (geo.jts/coordinate x y))
                                     (gen/tuple (s/gen ::x-coord)
                                                (s/gen ::y-coord)))))

(s/def ::jts-lat-lng-coordinate
  (s/with-gen #(instance? Coordinate %)
    #(gen/fmap (fn [[x y]] (geo.jts/coordinate x y))
               (gen/tuple (s/gen ::lng)
                          (s/gen ::lat)))))

(s/def ::jts-point (s/with-gen #(instance? Point %)
                 #(gen/fmap (fn [x] (geo.jts/point x))
                            (s/gen ::jts-lat-lng-coordinate))))

(s/def ::jts-line-segment (s/with-gen #(instance? LineSegment %)
                        #(gen/fmap (fn [[x y]] (geo.jts/line-segment x y))
                                   (gen/tuple (s/gen ::jts-lat-lng-coordinate)
                                              (s/gen ::jts-lat-lng-coordinate)))))

(s/def ::jts-line-string (s/with-gen #(instance? LineString %)
                        #(gen/fmap (fn [[x y]] (geo.jts/linestring [x y]))
                                   (gen/tuple (s/gen ::jts-lat-lng-coordinate)
                                              (s/gen ::jts-lat-lng-coordinate)))))

(s/def ::jts-geometry (s/with-gen #(instance? Geometry %)
                    #(s/gen (s/or :jts-point ::jts-point
                                  :jts-line-string ::jts-line-string))))

(s/def ::shapelike (s/with-gen (partial satisfies? geo.spatial/Shapelike)
                     #(s/gen (s/or :geometry ::jts-geometry))))

(s/def ::properties (s/map-of keyword? any?))

(s/def ::feature (s/with-gen feature?
                   #(gen/fmap (fn [[x y]] (ovid.feature/to-feature x y))
                              (gen/tuple (s/gen ::shapelike)
                                         (s/gen ::properties)))))

(s/def ::featurelike (s/with-gen featurelike?
                       #(s/gen (s/or :feature ::feature
                                     :shapelike ::shapelike))))

(s/fdef ovid.feature/to-shape
  :args (s/cat :input ::featurelike)
  :ret ::featurelike)

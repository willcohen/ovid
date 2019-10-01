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

(ns ovid.feature-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as st]
            [geo.jts :as jts]
            [ovid.feature :refer :all]
            [ovid.feature.specs :as ovid.feature.specs])
  (:import (ch.hsr.geohash GeoHash WGS84Point)
           (com.uber.h3core.util GeoCoord)
           (org.locationtech.spatial4j.shape Shape
                                             Rectangle)
           (org.locationtech.jts.geom Coordinate Geometry)
           (org.locationtech.spatial4j.shape.impl GeoCircle PointImpl RectangleImpl)
           (org.locationtech.spatial4j.shape.jts JtsGeometry
                                                 JtsPoint)))


(deftest jts-objects
  (testing "JTS Coordinate"
    (is (= Coordinate
           (type (gen/generate (s/gen :ovid.feature.specs/jts-coordinate)))))))

(deftest feature-tests
  (testing "to-shape"
    (is (= 1 (-> (st/check `to-shape)
                 st/summarize-results
                 :check-passed)))))

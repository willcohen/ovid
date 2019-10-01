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

(ns ovid.io
  (:require [byte-streams :as byte-streams]
            [clojure.java.io :as jio]
            [geo.jts :as jts]
            [manifold.stream :as stream]
            [ovid.feature :as feature])
  (:import (java.nio ByteBuffer ByteOrder)
           (sun.nio.ch ChannelInputStream)))

(defn- ba->be-int
  [b]
  (.getInt (byte-streams/to-byte-buffer b)))

(defn- ba->le-buffer
  [b]
  (.order (byte-streams/to-byte-buffer b)
          ByteOrder/LITTLE_ENDIAN))

(defn- ba->le-int
  [b]
  (.getInt (ba->le-buffer b)))

(defn- ba->le-double
  [b]
  (.getDouble (ba->le-buffer b)))

(defn- read-unused-bytes
  [^ChannelInputStream f num]
  (let [n (byte-array num)]
    (doto f
      (.read n))))

(defn- read-be-int
  [^ChannelInputStream f]
  (let [n (byte-array 4)]
    (doto f
      (.read n))
    (ba->be-int n)))

(defn- read-le-int
  [^ChannelInputStream f]
  (let [n (byte-array 4)]
    (doto f
      (.read n))
    (ba->le-int n)))

(defn- read-le-double
  [^ChannelInputStream f]
  (let [n (byte-array 8)]
    (doto f
      (.read n))
    (ba->le-double n)))

(defn- read-box
  [^ChannelInputStream f]
  (let [xmin (read-le-double f)
        ymin (read-le-double f)
        xmax (read-le-double f)
        ymax (read-le-double f)]
    {:xmin xmin
     :ymin ymin
     :xmax xmax
     :ymax ymax}))

(defn- read-shp-header
  [^ChannelInputStream f]
  (let [file-code (read-be-int f)
        _ (read-unused-bytes f 20)
        file-length (read-be-int f)
        version (read-le-int f)
        shape-type (read-le-int f)
        box (read-box f)
        zmin (read-le-double f)
        zmax (read-le-double f)
        mmin (read-le-double f)
        mmax (read-le-double f)]
    {:file-code file-code
     :file-length file-length
     :version version
     :shape-type shape-type
     :xmin (:xmin box)
     :ymin (:ymin box)
     :xmax (:xmax box)
     :ymax (:ymax box)
     :zmin zmin
     :zmax zmax
     :mmin mmin
     :mmax mmax}))

(defn- split-points
  [parts points]
  (let [revparts (vec (reverse parts))]
    (loop [split '()
           revparts revparts
           points points]
      (if (= (count revparts) 0)
        (rest (into split (list points)))
        (let [divided (split-at (first revparts) points)]
          (recur (into split (rest divided))
                 (rest revparts)
                 (first divided)))))))

(defn- read-coordinate
  [^ChannelInputStream f]
  (let [x (read-le-double f)
        y (read-le-double f)]
    (jts/coordinate x y)))

;; Shape Type 1
(defn- read-point
  [^ChannelInputStream f]
  (let [x (read-le-double f)
        y (read-le-double f)]
    (jts/point x y)))

;; Shape Type 3
(defn- read-polyline
  [^ChannelInputStream f]
  (let [box (read-box f)
        num-parts (read-le-int f)
        num-points (read-le-int f)
        parts (vec (take num-parts (repeatedly #(read-le-int f))))
        points (vec (take num-points (repeatedly #(read-coordinate f))))]
    (->> (split-points parts points)
         (map jts/linestring)
         jts/multi-linestring)))

;; Shape Type 5
(defn- read-polygon
  [^ChannelInputStream f]
  (let [box (read-box f)
        num-parts (read-le-int f)
        num-points (read-le-int f)
        parts (vec (take num-parts (repeatedly #(read-le-int f))))
        points (vec (take num-points (repeatedly #(read-coordinate f))))]
    (->> (split-points parts points)
         (map jts/linear-ring)
         (#(jts/polygon (first %) (rest %))))))

;; Shape Type 8
(defn- read-multipoint
  [^ChannelInputStream f]
  (let [box (read-box f)
        num-points (read-le-int f)
        points (vec (take num-points (repeatedly #(read-point f))))]
    (jts/multi-point points)))

;; Shape Type 11
(defn- read-point-z
  [^ChannelInputStream f]
  (let [x (read-le-double f)
        y (read-le-double f)
        z (read-le-double f)
        m (read-le-double f)]
    (jts/point (jts/coordinate x y z m))))

;; Shape Type 13
(defn- read-polyline-z
  [^ChannelInputStream f]
  ;; Still need to use the ZArray and MArray
  (let [box (read-box f)
        num-parts (read-le-int f)
        num-points (read-le-int f)
        parts (vec (take num-parts (repeatedly #(read-le-int f))))
        points (vec (take num-points (repeatedly #(read-coordinate f))))
        zmin (read-le-int f)
        zmax (read-le-int f)
        zarray (vec (take num-points (repeatedly #(read-le-int f))))
        mmin (read-le-int f)
        mmax (read-le-int f)
        marray (vec (take num-points (repeatedly #(read-le-int f))))]
    (->> (split-points parts points)
         (map jts/linestring)
         jts/multi-linestring)))

;; Shape Type 15
(defn- read-polygon-z
  [^ChannelInputStream f]
  ;; Still need to use the ZArray and MArray
  (let [box (read-box f)
        num-parts (read-le-int f)
        num-points (read-le-int f)
        parts (vec (take num-parts (repeatedly #(read-le-int f))))
        points (vec (take num-points (repeatedly #(read-coordinate f))))
        zmin (read-le-int f)
        zmax (read-le-int f)
        zarray (vec (take num-points (repeatedly #(read-le-int f))))
        mmin (read-le-int f)
        mmax (read-le-int f)
        marray (vec (take num-points (repeatedly #(read-le-int f))))]
    (->> (split-points parts points)
         (map jts/linear-ring)
         (#(jts/polygon (first %) (rest %))))))

;; Shape Type 18
(defn- read-multipoint-z
  [^ChannelInputStream f]
  ;; Still need to use the ZArray and MArray
  (let [box (read-box f)
        num-points (read-le-int f)
        points (vec (take num-points (repeatedly #(read-point f))))
        zmin (read-le-int f)
        zmax (read-le-int f)
        zarray (vec (take num-points (repeatedly #(read-le-int f))))
        mmin (read-le-int f)
        mmax (read-le-int f)
        marray (vec (take num-points (repeatedly #(read-le-int f))))]
    (jts/multi-point points)))

;; Shape Type 21
(defn- read-point-m
  [^ChannelInputStream f]
  ;; Still need to use the M
  (let [x (read-le-double f)
        y (read-le-double f)
        m (read-le-double f)]
    (jts/point x y)))

;; Shape Type 23
(defn- read-polyline-m
  [^ChannelInputStream f]
  ;; Still need to use the MArray
  (let [box (read-box f)
        num-parts (read-le-int f)
        num-points (read-le-int f)
        parts (vec (take num-parts (repeatedly #(read-le-int f))))
        points (vec (take num-points (repeatedly #(read-coordinate f))))
        mmin (read-le-int f)
        mmax (read-le-int f)
        marray (vec (take num-points (repeatedly #(read-le-int f))))]
    (->> (split-points parts points)
         (map jts/linestring)
         jts/multi-linestring)))

;; Shape Type 25
(defn- read-polygon-m
  [^ChannelInputStream f]
  ;; Still need to use the MArray
  (let [box (read-box f)
        num-parts (read-le-int f)
        num-points (read-le-int f)
        parts (vec (take num-parts (repeatedly #(read-le-int f))))
        points (vec (take num-points (repeatedly #(read-coordinate f))))
        mmin (read-le-int f)
        mmax (read-le-int f)
        marray (vec (take num-points (repeatedly #(read-le-int f))))]
    (->> (split-points parts points)
         (map jts/linear-ring)
         (#(jts/polygon (first %) (rest %))))))

;; Shape Type 28
(defn- read-multipoint-m
  [^ChannelInputStream f]
  ;; Still need to use the MArray
  (let [box (read-box f)
        num-points (read-le-int f)
        points (vec (take num-points (repeatedly #(read-point f))))
        mmin (read-le-int f)
        mmax (read-le-int f)
        marray (vec (take num-points (repeatedly #(read-le-int f))))]
    (jts/multi-point points)))

(defn- read-content
  [^ChannelInputStream f]
  (let [shape-type (read-le-int f)]
    (cond (= 0 shape-type)
          nil
          (= 1 shape-type)
          (read-point f)
          (= 3 shape-type)
          (read-polyline f)
          (= 5 shape-type)
          (read-polygon f)
          (= 8 shape-type)
          (read-multipoint f)
          (= 11 shape-type)
          (read-point-z f)
          (= 13 shape-type)
          (read-polyline-z f)
          (= 15 shape-type)
          (read-polygon-z f)
          (= 18 shape-type)
          (read-multipoint-z f)
          (= 21 shape-type)
          (read-point-m f)
          (= 23 shape-type)
          (read-polyline-m f)
          (= 25 shape-type)
          (read-polygon-m f)
          (= 28 shape-type)
          (read-multipoint-m f))))
          ;; (= 31 shape-type) ; Unimplemented
          ;; (read-multipatch f) ; Unimplemented

(defn- read-record
  [^ChannelInputStream f]
  {:record-number (read-be-int f)
   :content-length (read-be-int f)
   :content (read-content f)})

(defn- read-record-loop
  [^ChannelInputStream f]
  (loop [records []
         new-record (read-record f)]
    (if (zero? (:record-number new-record))
      (do (.close f)
          records)
      (recur (conj records new-record)
             (read-record f)))))

;; Should try to implement as a transducer for streaming

(defn read-shp
  [f]
  (let [f (byte-streams/to-input-stream f)
        header (read-shp-header f)]
    {:header header
     :records (read-record-loop f)}))


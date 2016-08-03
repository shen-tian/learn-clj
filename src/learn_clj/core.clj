(ns learn-clj.core
  (:import (processing.core PVector))
  (:import (me.lsdo.processing OPC Dome DomeAnimation))
  (:gen-class))

(def dome
  (new Dome))

(def creep-speed 20)
(def ramp-length 60)

(def opc
  (new OPC "127.0.0.1" 7890))

(def coord-order
  (zipmap
    (map (fn [i] (.get (.-coords dome) i)) (range 1560))
    (range 1560)))

(defn unit-interval-to-byte
  [x]
  (mod (int (* x 256)) 256))

(defn get-brightness
  [coord t]
  (unit-interval-to-byte
    (/
      (- (get coord-order coord)
        (* creep-speed t))
        ramp-length)))

(defn get-saturaiton)

(def test-animation
  (proxy [DomeAnimation] [dome opc]
    (drawPixel [coord t]
      (proxy-super getHsbColor
        (mod (int (* t 100)) 255)
        255
        (get-brightness coord t)))))

(defn millis []
  (System/currentTimeMillis))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (def start (millis))
  (while true
    (.draw test-animation
      (/ (- (millis) start) 1000))))

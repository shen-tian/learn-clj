(ns learn-clj.core
  (:import (processing.core PVector))
  (:import (me.lsdo.processing OPC Dome DomeAnimation))
  (:gen-class))

(def dome
  (new Dome))

(defn rgb [r g b]
  (+ (* 256 256 r) (* 256 g) b))

(def opc
  (new OPC "127.0.0.1" 7890))

(def test-animation
  (proxy [DomeAnimation] [dome opc]
    (drawPixel [coord t]
      (rgb (mod (int (* t 100)) 255) 0 0))))

(defn millis []
  (System/currentTimeMillis))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (def start (millis))
  (while true
    (.draw test-animation
      (/ (- (millis) start) 1000))))

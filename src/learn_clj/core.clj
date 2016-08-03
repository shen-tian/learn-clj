(ns learn-clj.core
  (:import (processing.core PVector))
  (:import (me.lsdo.processing
    OPC
    Dome
    DomeAnimation
    LayoutUtil))
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

(def px-per-panel
  (LayoutUtil/pixelsPerPanel 15))

(def arms [4 4 4 1])

(defn panel [i]
  (int (/ i px-per-panel)))

(defn arm [panelx]
  (if (< panelx 4) 0
  (if (< panelx 8) 1
  (if (< panelx 12) 2 3))))


(defn unit-interval-to-byte
  [x]
  (mod (int (* x 255)) 256))

(defn get-brightness
  [coord t]
  (unit-interval-to-byte
    (/
      (- (get coord-order coord)
        (* creep-speed t))
        ramp-length)))

(defn get-saturaiton
  [coord t]
  (unit-interval-to-byte
    (+ 0.5 (* 0.5 (/
      (mod (panel (get coord-order coord)) 4)
      (double (max (-
        (get arms (arm (panel (get coord-order coord))))
          1) 1)))))))

(defn get-hue
  [coord t]
  (unit-interval-to-byte
    (/
      (arm (panel (get coord-order coord)))
      (double (count arms)))))

(def test-animation
  (proxy [DomeAnimation] [dome opc]
    (drawPixel [coord t]
      (proxy-super getHsbColor
        (get-hue coord t)
        (get-saturaiton coord t)
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

(ns learn-clj.pixel-test
  (:import (me.lsdo.processing
             OPC
             Dome
             DomeAnimation
             LayoutUtil)))

(def dome
  (new Dome))

(def creep-speed 20)
(def ramp-length 60)

(def opc
  (new OPC "127.0.0.1" 7890))

(def coord-order
  (zipmap
    (map #(.get (.-coords dome) %) (range 1560))
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


(defn unit-interval->byte
  [x]
  (mod (int (* x 255)) 256))

(defn get-brightness
  "Brightness ramps up, function of time and location within the panel"
  [coord t]
  (unit-interval->byte
    (/
      (- (coord-order coord)
         (* creep-speed t))
      ramp-length)))

(defn get-saturaiton
  "Calculates the saturation."
  [coord]
  (unit-interval->byte
    (let [i (coord-order coord)
          p (panel i)
          a (arm p)]
      (+ 0.5
         (* 0.5
            (/ (mod p 4)
               (double (max (- (arms a) 1) 1))))))))

(defn get-hue
  [coord]
  (unit-interval->byte
    (/
      (arm (panel (coord-order coord)))
      (double (count arms)))))

(def test-animation
  (proxy [DomeAnimation] [dome opc]
    (drawPixel [coord t]
      (proxy-super getHsbColor
                   (get-hue coord)
                   (get-saturaiton coord)
                   (get-brightness coord t)))))

(defn millis []
  (System/currentTimeMillis))

(defn run-sketch []
  (let [start (millis)]
    (while true
      (.draw test-animation
             (/ (- (millis) start) 1000)))))


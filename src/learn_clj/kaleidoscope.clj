(ns learn-clj.kaleidoscope
  (:import (me.lsdo.processing
             OPC
             Dome
             DomeAnimation
             DomeCoord
             TriCoord
             LayoutUtil
             MathUtil
             TriCoord$PanelOrientation
             TriCoord$Axis
             TriCoord$CoordType)))

(def dome
  (new Dome))

(def opc
  (new OPC "127.0.0.1" 7890))

(defn unit-interval->byte
  [x]
  (mod (int (* x 255)) 256))

(def base-panel
  (new TriCoord (TriCoord$CoordType/PANEL)
       0 0 -1))

(defn base-pixel-color
  [coord t]
  (let [p (.getLocation dome coord)
        p2 (LayoutUtil/Vrot p (* t (+ 0.5 (* 3 0.5
                                             (+ 1 (Math/cos (* 0.1213 t)))))))
        p3 (LayoutUtil/Vmult p2 (/ 1 (+ 1 (* 5 0.5
                                             (+ 1 (Math/cos (* 0.3025 t)))))))
        p4 (LayoutUtil/Vadd p3 (LayoutUtil/V (* 2 (Math/cos (* 0.2 t))) 0))]
    {:hue        (unit-interval->byte (MathUtil/fmod
                                        (+ (.-x p4) (* 0.4081 t))
                                        1.0))
     :saturation (* 0.6 255)
     :brightness (unit-interval->byte (* 0.5 (+ 1 (Math/cos (* 40 (.-x p4))))))
     }))

(defn pixel-color
  [coord t]
  (let [pos (MathUtil/mod (- (.-u (.-panel coord)) (.-v (.-panel coord))) 3)
        rot (MathUtil/mod (if (= (.getOrientation (.-panel coord))
                                 (TriCoord$PanelOrientation/A))
                            (* pos 2)
                            (- 1 (* 2 pos)))
                          6)
        flip (= (MathUtil/mod rot 2) 1)
        base-px (if flip
                  (.flip (.rotate (.-pixel coord) rot)
                         (TriCoord$Axis/U))
                  (.rotate (.-pixel coord) rot))]

    (base-pixel-color
      (new DomeCoord base-panel base-px) t)))

(def kaleidoscope-animation
  (proxy [DomeAnimation] [dome opc]
    (drawPixel [coord t]
      (let [color (pixel-color coord t)]
        (proxy-super getHsbColor
                     (:hue color)
                     (:saturation color)
                     (:brightness color))))))

(defn millis []
  (System/currentTimeMillis))

(defn run-sketch []
  (let [start (millis)]
    (while true
      (do
        (.draw kaleidoscope-animation
               (/ (- (millis) start) 1000))))))

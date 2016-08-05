(ns learn-clj.kaleidoscope
  (:import (me.lsdo.processing
             OPC
             Dome
             DomeAnimation
             DomeCoord
             TriCoord
             LayoutUtil
             MathUtil
             TriCoord$PanelOrientation)))

(def dome
  (new Dome))

(def opc
  (new OPC "127.0.0.1" 7890))

(defn unit-interval->byte
  [x]
  (mod (int (* x 255)) 256))

(def base-panel
  (new TriCoord (me.lsdo.processing.TriCoord$CoordType/PANEL)
       0 0 -1))

(defn base-pixel-color
  [coord t]
  (let [p (.getLocation dome coord)
        p2 (LayoutUtil/Vrot p (* t (+ 0.5 (* 3 0.5 (+ (Math/cos (* 0.1213 t)) 1)))))
        p3 (LayoutUtil/Vmult p2 (/ 1 (+ 1 (* 5 0.5 (+ (Math/cos (* 0.3025 t)) 1)))))
        p4 (LayoutUtil/Vadd p3 (LayoutUtil/V (* 2 (Math/cos (* 0.2 t))) 0))]
    {:hue        255
     :saturation 255
     :brightness (unit-interval->byte (* 0.5 (+ 1 (Math/cos (* 40 (.-x p4)))))) ;.5*(Math.cos(40*p.x)+1)
     }))
;
;int pos = MathUtil.mod(c.panel.u - c.panel.v, 3);
;int rot = MathUtil.mod(c.panel.getOrientation() == TriCoord.PanelOrientation.A ? 2*pos : 1-2*pos, 6);
;boolean flip = (MathUtil.mod(rot, 2) == 1);
;TriCoord basePx = c.pixel.rotate(rot);
;if (flip) {
;           basePx = basePx.flip(TriCoord.Axis.U);
;           }
;
;return getBasePixel(new DomeCoord(basePanel, basePx), t);


(defn pixel-color
  [coord t]
  (let [pos (MathUtil/mod (- (.-u (.-panel coord)) (.-v (.-panel coord))) 3)
        rot (MathUtil/mod (if (= (.getOrientation (.-panel coord))
                                 (TriCoord$PanelOrientation/A))
                            (* pos 2)
                            (- 1 (* 2 pos)))
                          6)
        flip (= (MathUtil/mod rot 2) 1)
        base-px (.rotate (.-pixel coord) rot)]

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

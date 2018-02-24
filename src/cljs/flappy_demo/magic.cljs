(ns flappy-demo.magic
  (:require
   [cljsjs.react]
   [cljsjs.react.dom]
   [sablono.core :as sab :include-macros true]
   [cljs.core.async :refer [<! chan sliding-buffer put! close! timeout]]
   [flappy-demo.config :refer [starting-state pillar-width pillar-gap pillar-spacing
                               flappy-height bottom-y start-y jump-vel gravity horiz-vel]]
   [flappy-demo.flappy :refer [collision? update-flappy]]) 
  (:require-macros
                   [cljs.core.async.macros :refer [go-loop go]]))

(defn floor [x] (.floor js/Math x))

(defn translate [start-pos vel time]
  (floor (+ start-pos (* time vel))))

(defn reset-state [_ cur-time]
  (-> starting-state
      (update-in [:pillar-list] (fn [pls] (map #(assoc % :start-time cur-time) pls)))
      (assoc
          :start-time cur-time
          :flappy-start-time cur-time
          :timer-running true)))

(defn curr-pillar-pos [cur-time {:keys [pos-x start-time]}]
  (translate pos-x horiz-vel (- cur-time start-time)))

(defn new-pillar [cur-time pos-x]
  {:start-time cur-time
   :pos-x      pos-x
   :cur-x      pos-x
   :gap-top    (+ 60 (rand-int (- bottom-y 120 pillar-gap)))})

(defn update-pillars [{:keys [pillar-list cur-time] :as st}]
  (let [pillars-with-pos (map #(assoc % :cur-x (curr-pillar-pos cur-time %)) pillar-list)
        pillars-in-world (sort-by
                          :cur-x
                          (filter #(> (:cur-x %) (- pillar-width)) pillars-with-pos))]
    (assoc st
      :pillar-list
      (if (< (count pillars-in-world) 3)
        (conj pillars-in-world
              (new-pillar
               cur-time
               (+ pillar-spacing
                  (:cur-x (last pillars-in-world)))))
        pillars-in-world))))


(defn score [{:keys [cur-time start-time] :as st}]
  (let [score (- (.abs js/Math (floor (/ (- (* (- cur-time start-time) horiz-vel) 544)
                                       pillar-spacing)))
                 4)]
   (assoc st :score (if (neg? score) 0 score))))

(defn time-update [timestamp state]
  (-> state
      (assoc
          :cur-time timestamp
          :time-delta (- timestamp (:flappy-start-time state)))
      update-flappy
      update-pillars
      collision?
      score))

;; derivatives

(defn border [{:keys [cur-time] :as state}]
  (-> state
      (assoc :border-pos (mod (translate 0 horiz-vel cur-time) 23))))

(defn pillar-offset [{:keys [gap-top] :as p}]
  (assoc p
    :upper-height gap-top
    :lower-height (- bottom-y gap-top pillar-gap)))

(defn pillar-offsets [state]
  (update-in state [:pillar-list]
             (fn [pillar-list]
               (map pillar-offset pillar-list))))

(defn world [state]
  (-> state
      border
      pillar-offsets))

(defn px [n] (str n "px"))

(defn pillar [{:keys [cur-x pos-x upper-height lower-height]}]
  [:div.pillars {:key cur-x}
   [:div.pillar.pillar-upper {:style {:left (px cur-x)
                                       :height upper-height}}]
   [:div.pillar.pillar-lower {:style {:left (px cur-x)
                                       :height lower-height}}]])


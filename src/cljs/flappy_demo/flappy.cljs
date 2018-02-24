(ns flappy-demo.flappy
  (:require [flappy-demo.config
             :refer [starting-state flappy-x flappy-width
                     pillar-width pillar-gap flappy-height
                     bottom-y jump-vel start-y]]))

(defn in-pillar? [{:keys [cur-x]}]
  (and (>= (+ flappy-x flappy-width)
           cur-x)
       (< flappy-x (+ cur-x pillar-width))))

(defn in-pillar-gap? [{:keys [flappy-y]} {:keys [gap-top]}]
  (and (< gap-top flappy-y)
       (> (+ gap-top pillar-gap)
          (+ flappy-y flappy-height))))

(defn bottom-collision? [{:keys [flappy-y]}]
  (>= flappy-y (- bottom-y flappy-height)))

(defn collision? [{:keys [pillar-list] :as st}]
  (if (some #(or (and (in-pillar? %)
                      (not (in-pillar-gap? st %)))
                 (bottom-collision? st)) pillar-list)
    (assoc st :timer-running false)
    st))

(defn jump [{:keys [cur-time jump-count] :as state}]
  (-> state
      (assoc
          :jump-count (inc jump-count)
          :flappy-start-time cur-time
          :initial-vel jump-vel)))

(defn sine-wave
  "Modifies flappy state to add periodic vertial oscillations"
  [st]
  (assoc st
    :flappy-y
    (+ start-y (* 30 (.sin js/Math (/ (:time-delta st) 300))))))

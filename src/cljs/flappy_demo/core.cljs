(ns flappy-demo.core
  (:require
   [cljsjs.react]
   [cljsjs.react.dom]
   [sablono.core :as sab :include-macros true]
   [cljs.core.async :refer [<! chan sliding-buffer put! close! timeout]]
   [flappy-demo.magic :refer [reset-state world time-update pillar px]]
   [flappy-demo.config :refer [starting-state]]
   [flappy-demo.flappy :refer [jump]])
  (:require-macros
                   [cljs.core.async.macros :refer [go-loop go]]))

(enable-console-print!)

(defonce flap-state (atom starting-state))

(defn time-loop [time]
  (let [new-state (swap! flap-state (partial time-update time))]
    (when (:timer-running new-state)
      (go
       (<! (timeout 30))
       (.requestAnimationFrame js/window time-loop)))))

(defn start-game []
  (.requestAnimationFrame
   js/window
   (fn [time]
     (reset! flap-state (reset-state @flap-state time))
     (time-loop time))))

(defn main-template [{:keys [score cur-time jump-count
                             timer-running border-pos
                             flappy-y pillar-list]}]
  (sab/html [:div.board { :onMouseDown (fn [e]
                                         (.preventDefault e)
                                         (swap! flap-state jump))}
             
             [:h1.score score]
             (if-not timer-running
               [:a.start-button {:onClick #(start-game)}
                (if (< 1 jump-count) "RESTART" "START")]
               [:span])
             [:div (map pillar pillar-list)]
             [:div.flappy {:style {:top (px flappy-y)}}]
             [:div.scrolling-border {:style { :background-position-x (px border-pos)}}]]))

(defn init []  
  (def node (.getElementById js/document "board-area"))
  
  (defn renderer [full-state]
    (.render js/ReactDOM (main-template full-state) node))
  
  (add-watch flap-state
    :renderer (fn [_ _ _ n]
                (renderer (world n))))
  
  (reset! flap-state @flap-state))

(init)

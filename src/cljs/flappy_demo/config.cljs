(ns flappy-demo.config)

(def horiz-vel -0.15)
(def gravity 0.05)
(def jump-vel 21)
(def start-y 312)
(def bottom-y 561)
(def flappy-x 212)
(def flappy-width 57)
(def flappy-height 41)
(def pillar-spacing 324)
(def pillar-gap 158) ;; 158
(def pillar-width 86)

(def starting-state { :timer-running false
                      :jump-count 0
                      :initial-vel 0
                      :start-time 0
                      :flappy-start-time 0
                      :flappy-y   start-y
                      :pillar-list
                      [{ :start-time 0
                         :pos-x 900
                         :cur-x 900
                         :gap-top 200}]})

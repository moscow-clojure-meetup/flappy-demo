(set-env!
  :source-paths #{"src/clj" "src/cljs"}
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                  [adzerk/boot-reload "0.5.2" :scope "test"]
                  [javax.xml.bind/jaxb-api "2.3.0" :scope "test"] ; necessary for Java 9 compatibility
                  ; project deps
                  [org.clojure/clojure "1.9.0"]
                  [org.clojure/clojurescript "1.9.946" :scope "test"]
                  [reagent "0.7.0" :scope "test"]
                  [ring "1.6.3"]
                  [org.clojure/core.async "0.2.395"]
                  [cljsjs/react-dom-server "15.3.1-0"]
                  [cljsjs/react-dom "15.3.1-0"] ;; for sablono
                  [cljsjs/react "15.3.1-0"] ;; for sablono
                  [sablono "0.7.5"]])
                  
                  

(task-options!
  pom {:project 'flappy-demo
       :version "1.0.0-SNAPSHOT"
       :description "FIXME: write description"}
  aot {:namespace #{'flappy-demo.core}}
  jar {:main 'flappy-demo.core})

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  'flappy-demo.core)

(deftask run []
  (comp
    (with-pass-thru _
      (flappy-demo.core/dev-main))
    (watch)
    (reload :asset-path "public")
    (cljs
      :source-map true
      :optimizations :none
      :compiler-options {:asset-path "main.out"})
    (target)))

(deftask build []
  (comp
    (cljs :optimizations :advanced)
    (aot)
    (pom)
    (uber)
    (jar)
    (sift :include #{#"\.jar$"})
    (target)))


(ns test.cor 
  (:require [seesaw.dev :as d]
            [seesaw.core :as s] [seesaw.config]))

(def main-frame (s/frame))

(defn display [content]
  (s/config! main-frame :content content)
  (s/pack! main-frame)
  (s/show! main-frame)
  content)
(ns test.core 
  (:require [seesaw.core :as s] [seesaw.table :as t] [test.database :as db] [clojure.java.jdbc :refer :all] [seesaw.dev :as d])
  (:import [java.awt Window]
           [java.awt.event WindowAdapter]
           [com.formdev.flatlaf FlatDarkLaf]) (:gen-class))

;; мета

(defn set-theme  []
  ;; ставим тёмную тему
  
  (javax.swing.UIManager/setLookAndFeel (FlatDarkLaf.))
  ;; ставим антиалиасинг
  (.put (javax.swing.UIManager/getLookAndFeelDefaults) java.awt.RenderingHints/KEY_TEXT_ANTIALIASING
        java.awt.RenderingHints/VALUE_TEXT_ANTIALIAS_ON))

;; чтобы все глобальные объявления были с темой
(set-theme)


;; главный фрейм


(def f (s/frame :title "Погода"
                :minimum-size [640 :by 480]
                :content (s/flow-panel
                          :align :left
                          :hgap 20
                          :items [(s/vertical-panel :items [(s/label :text "hello gordon") (s/label :text "hello gordon")])
                                  (s/table
                                   :selection-mode :single
                                   :size [540 :by 480]
                                   :column-widths [30 100 100 100 100 100]
                                   :model [:columns
                                           [{:key :id :text "id"}
                                            {:key :city :text "city"}
                                            {:key :direction :text "direction"}
                                            {:key :temp :text "temp"}
                                            {:key :wind :text "wind"}
                                            {:key :pressure :text "pressure"}]
                                           :rows (db/get-all-records)])])
                :on-close :exit)
  )




;; точка входа
#_{:clj-kondo/ignore [:unused-binding]}
(defn -main [& args]
  (s/invoke-later
   (-> f
    s/pack!
    s/show!)))










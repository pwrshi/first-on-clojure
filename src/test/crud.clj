(ns test.crud
  (:use [seesaw core border mig]
        [clojure.string :only [split trim join]]
        test.cor)
  (:require [seesaw.bind :as b]  [test.database :as db]) (:import [com.formdev.flatlaf FlatDarkLaf]))

(defn set-theme  []
  ;; ставим тёмную тему

  (javax.swing.UIManager/setLookAndFeel (FlatDarkLaf.))
  ;; ставим антиалиасинг
  (.put (javax.swing.UIManager/getLookAndFeelDefaults) java.awt.RenderingHints/KEY_TEXT_ANTIALIASING
        java.awt.RenderingHints/VALUE_TEXT_ANTIALIAS_ON))

;; чтобы все глобальные объявления были с темой
(set-theme)

(defn fetch-database [] (mapv  (fn [x] (join " " (mapv val x))) (db/get-all-records)))

(def database
  (fetch-database)
  )

(defn create-entry [db entry] 
  
  (let [[id-str city-str direction-str temp-str wind-str pressure-str] (split entry #" ")]
    (println {:city city-str,
              :direction direction-str ,
              :temp temp-str,
              :wind wind-str,
              :pressure pressure-str})
    (db/add-record 
  {:city city-str,
   :direction direction-str ,
   :temp temp-str,
   :wind wind-str,
   :pressure pressure-str}))
  (fetch-database)
)

(defn update-entry [db index entry]
  (let [[id-str city-str direction-str temp-str wind-str pressure-str] (split entry #" ")]
    (println {:city city-str,
              :direction direction-str ,
              :temp temp-str,
              :wind wind-str,
              :pressure pressure-str})
    (db/update-record-by-id
     id-str
     {:city city-str,
      :direction direction-str ,
      :temp temp-str,
      :wind wind-str,
      :pressure pressure-str}))
(fetch-database))

(defn delete-entry [db index entry]
  (let [[id-str city-str direction-str temp-str wind-str pressure-str] (split entry #" ")]
    (println {:city city-str,
              :direction direction-str ,
              :temp temp-str,
              :wind wind-str,
              :pressure pressure-str})
    (db/delete-record
     id-str
     ))
(fetch-database))

(defrecord Filtered-db [filtered mapping])

(defn filter-db [db f]
  (let [db+is (filter (comp f first) (map vector db (range)))
        db'   (map first db+is)
        is    (map second db+is)]
    (Filtered-db. db' is)))

(defn prefix? [prefix string]
  (= (subs string 0 (count prefix)) prefix))

(defn crud-panel [db]
  (let [prefix  (text :columns 5)
        id (text :columns 3 :editable? false)
        city    (text :columns 8)
        direction (text :columns 8) 
        temp (text :columns 8)
        wind (text :columns 8)
        pressure (text :columns 8) 
        create  (button :text "Create")
        update  (button :text "Update" :enabled? false)
        delete  (button :text "Delete" :enabled? false)
        entries (listbox :model @db)

        filterf       #(prefix? (value prefix) %)
        db-view       (atom (filter-db @db filterf))
        reset-db-view #(reset! db-view (filter-db % filterf))
        read-name     #(str (value id) " " (value city) " " (value direction) " " (value temp) " " (value wind) " " (value pressure))]
    (add-watch db :key (fn [_ _ _ new-db]
                         (reset-db-view new-db)))
    (add-watch db-view :key (fn [_ _ _ new-db-view]
                              (config! entries :model (:filtered new-db-view))))
    (listen prefix :document (fn [e]
                               (reset-db-view @db)))
    (listen entries :selection (fn [e]
                                 (when-let [sel (selection e)]
                                   (let [[id-str city-str direction-str temp-str wind-str pressure-str] (split sel #" ")]
                                     (config! id :text id-str)
                                     (config! city :text city-str)
                                     (config! direction :text direction-str)
                                     (config! temp :text temp-str)
                                     (config! wind :text wind-str)
                                     (config! pressure :text pressure-str)
                                     ))))
    (listen create :action (fn [e]
                             (swap! db #(create-entry % (read-name)))))
    (listen update :action (fn [e]
                             (let [orig-i (nth (:mapping @db-view) (.getSelectedIndex entries))]
                               (swap! db #(update-entry % orig-i (read-name))))))
    (listen delete :action (fn [e]
                             (let [orig-i (nth (:mapping @db-view) (.getSelectedIndex entries))]
                               (swap! db #(delete-entry % orig-i (read-name))))))
    (b/bind
     (b/selection entries)
     (b/transform (complement nil?))
     (b/property update :enabled?)
     (b/property delete :enabled?))
    (border-panel
     :preferred-size [400 :by 400]
     :border (empty-border :left 10 :right 10 :bottom 5)
     :north  (flow-panel :align :left :items ["Filter prefix: " prefix])
     :center (border-panel
              :hgap   10
              :border (empty-border :left 5 :bottom 5)
              :center (scrollable entries)
              :east   (mig-panel
                       :constraints ["wrap 2, insets 0"]
                       :items [["ID: "] [id]
                               ["City: "] [city]
                               ["Direction: "] [direction]
                               ["Temperature: "] [temp]
                               ["Wind: "] [wind]
                               ["Pressure: "] [pressure]
                               ]))
     :south  (horizontal-panel :items [create update delete]))))

(defn -main [& args]
  (invoke-later
   (-> (frame :title "CRUD" :content (crud-panel (atom database)) :on-close :exit)
       pack!
       show!)))
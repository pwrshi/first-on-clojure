(ns test.database (:require [seesaw.core :as s] [clojure.java.jdbc :refer :all])
    (:import [java.awt Window]
             [java.awt.event WindowAdapter]
             [com.formdev.flatlaf FlatDarkLaf]) (:gen-class))






(def testdata
  {:city "Ростов-на-Дону",
   :direction "В",
   :temp -8,
   :wind 12,
   :pressure 12})





(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(defn create-db
  []
  (try (db-do-commands db
                       (create-table-ddl :records
                                         [[:id :integer "PRIMARY KEY" "AUTOINCREMENT"]
                                          [:city :text]
                                          [:direction :text]
                                          [:temp :int]
                                          [:wind :int]
                                          [:pressure :int]]))
       (catch Exception e
         (println (.getMessage e)))))

(defn get-record-by-id [id]
  (get-by-id db :records id))

(defn get-all-records 
  []
  (query db ["select * from records"]))

(defn update-record-by-id [id set-map] (update! db :records set-map ["id = ?", id]))

(defn add-record [set-map] (insert! db :records set-map))

(defn delete-record [id] (delete! db :records ["id = ?", id]))

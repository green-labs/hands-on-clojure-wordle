(ns wordle.db)

(def word-list [])

(def default-db
  {:name   "re-frame"
   :row    6
   :column 5
   :curr   {:r 0 :c 0}
   :status :playing
   :answer "POWER"})



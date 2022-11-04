(ns wordle.views
  (:require
   [re-frame.core :as re-frame]
   [wordle.subs :as subs]))


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Wordle" @name]]))


(ns wordle.events
  (:require
    [re-frame.core :as re-frame]
    [wordle.db :as db]))


(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  ::next-position
  (fn [db [_ _]]
    (let [[r c] ((juxt :r :c) (:curr db))
          {:keys [row column]} db]
      (if (> column c)
        (update-in db [:curr :c] inc)
        (-> db
            (assoc-in [:curr :r] (inc r))
            (assoc-in [:curr :c] 0))))))

(re-frame/reg-event-db
  ::backspace
  (fn [db [_ _]]
    (if (> (get-in db [:curr :c]) 0)
      (update-in db [:curr :c] dec)
      db)))

(re-frame/reg-event-db
  ::update-status
  (fn [db [_ status]]
    (assoc db :status status)))
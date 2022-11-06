(ns wordle.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))


(re-frame/reg-sub
  ::curr
  (fn [db]
    (:curr db)))

(re-frame/reg-sub
  ::row
  (fn [db]
    (:row db)))

(re-frame/reg-sub
  ::column
  (fn [db]
    (:column db)))

(re-frame/reg-sub
  ::answer
  (fn [db]
    (:answer db)))

(re-frame/reg-sub
  ::status
  (fn [db]
    (:status db)))
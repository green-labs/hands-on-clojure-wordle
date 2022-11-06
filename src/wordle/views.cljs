(ns wordle.views
  (:require
    [clojure.set]
    [re-frame.core :as re-frame :refer [dispatch]]
    [wordle.subs :as subs]
    [wordle.events :as events]))

(defn gen-key []
  (gensym "key-"))

(defn letter-input [i j]
  [:span.tile
   {:id  (str i "-" j)
    :key (gen-key)}])

(defn get-id [r c]
  (str r "-" c))

(defn wordle? [curr answer]
  (let [r               (:r @curr)
        c               (:c @curr)
        ax              (map char answer)
        wx              (for [i (range c)]
                          (.-innerHTML (.getElementById js/document (get-id r i))))
        common-letters  (clojure.set/intersection (set ax) (set wx))
        correct-letters (map = ax wx)]
    (doseq [i (range c)]
      (let [contains (contains? common-letters (.-innerHTML (.getElementById js/document (get-id r i))))
            correct  (nth correct-letters i)]
        (cond
          correct
          (-> (.getElementById js/document (get-id r i))
              (.-classList)
              (.add "correct"))

          contains
          (-> (.getElementById js/document (get-id r i))
              (.-classList)
              (.add "present"))

          :else
          (-> (.getElementById js/document (get-id r i))
              (.-classList)
              (.add "absent")))))
    (cond
      (every? true? correct-letters)
      (dispatch [::events/update-status :solved])

      (>= r 5)
      (dispatch [::events/update-status :failed]))))

(defn key-up-event [e]
  (let [curr      (re-frame/subscribe [::subs/curr])
        column    (re-frame/subscribe [::subs/column])
        answer    (re-frame/subscribe [::subs/answer])
        curr-tile (.getElementById js/document (get-id (:r @curr) (:c @curr)))
        input     (-> e .-code)]

    (cond
      (and
        (contains? (set (map char (range 65 90))) (last input))
        (nil? (-> curr-tile .-value)))
      (do
        (set! (.-innerHTML curr-tile) (last input))
        (dispatch [::events/next-position]))

      (= input "Backspace")
      (when (pos? (:c @curr))
        (dispatch [::events/backspace])
        (set! (.-innerHTML (.getElementById js/document (get-id (:r @curr) (dec (:c @curr))))) nil))

      (and (= input "Enter")
           (>= (:c @curr) @column))
      (do
        (dispatch [::events/next-position])
        (wordle? curr @answer))

      :else
      (js/alert "Invalid!"))))


(defn main-panel []
  (let [column (re-frame/subscribe [::subs/column])
        row    (re-frame/subscribe [::subs/row])
        status (re-frame/subscribe [::subs/status])
        answer (re-frame/subscribe [::subs/answer])]
    [:div
     {:style     {:text-align  "center"
                  :font-family ["Arial" "Helvetica" "sans-serif"]}
      :tab-index 0                                            ;for div to be able to be focused
      :on-key-up key-up-event}
     [:h1 {:id "title"} "WORDLE"]
     [:hr]
     [:div.board
      (for [i (range @row)]
        (for [j (range @column)]
          (letter-input i j)))]
     [:br]
     (cond
       (= @status :solved)
       (js/alert "Congratz!")

       (= @status :failed)
       [:h1 {:id "answer"} @answer])]))







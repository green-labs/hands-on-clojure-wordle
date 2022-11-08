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

(defn update-result
  "input : [`w` `a` `t` `e` `r`]
   answer : [`p` `o` `w` `e` `r`]
   input과 answer가 완벽히 일치하면 아래의 코드를 실행.
   (dispatch [::events/update-status :solved])"
  [input answer]
  (when (= input answer)
    (dispatch [::events/update-status :solved])))

(defn wordle-match?
  "input : [`w` `a` `t` `e` `r`]
   answer : [`p` `o` `w` `e` `r`]
   output : [:contain :none :none :correct :correct]
   포함하지만 같은 위치가 아닌 경우 -> :contain
   포함하고 위치가 같은 경우 -> :correct
   포함하지 않는 경우 -> :none"
  [input answer]
  (let [answer-set (set answer)]
    (->> input
         (map (fn [c c']
                (cond
                  (= c c') :correct
                  (contains? answer-set c') :contain
                  :else :none)) answer))))

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
      (let [r               (:r @curr)
            c               (:c @curr)
            ax              (map char @answer)
            wx              (for [i (range c)]
                              (.-innerHTML (.getElementById js/document (get-id r i))))
            result (wordle-match? wx ax)]
        (doseq [i (range c)]
          (cond
            (= :correct (nth result i))
            (-> (.getElementById js/document (get-id r i))
                (.-classList)
                (.add "correct"))

            (= :contain (nth result i))
            (-> (.getElementById js/document (get-id r i))
                (.-classList)
                (.add "present"))

            :else
            (-> (.getElementById js/document (get-id r i))
                (.-classList)
                (.add "absent"))))
        (update-result wx ax)
        (if (>= r 5)
          (dispatch [::events/update-status :failed])
          (dispatch [::events/next-position])))

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
     (cond
       (= @status :solved)
       (js/alert "Congratz!")

       (= @status :failed)
       [:h1 {:id "answer"} @answer])]))


(comment
  (def input (vec (map char "WATER")))
  (def answer (vec (map char "POWER")))

  (->> input
       (map (fn [c c']
              (cond
                (= c c') :correct
                (contains? (set answer) c') :contains
                :else :none)) answer))
  (cond
    (> 1 2) "correct"
    (> 2 1) "yes!")

  (contains? (set answer) "T")

  ;; :contain, :none, :correct
  (->> input
       (map (fn [c c']
              (cond
                false :contain
                false :none
                (= c c') :correct)) answer)))






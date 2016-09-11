(ns number-guess.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

;; Possible game-states
;; TODO - can these be defined as a list?
(keyword :show-game)
(keyword :show-success)
(keyword :show-comparison)
(keyword :show-failure)

(defonce app-state (atom {:rand-value (rand-int 100)
                          :game-state :show-game
                          :guess 0
                          :max-turns 7
                          :current-turn 1}))

;; TOOD - make a "default" value and just copy it here?
(defn reset-game []
  [:div
    [:input {:type "button"
             :class "btn btn-default"
             :value "Reset Game"
             :on-click #(swap! app-state assoc
                               :game-state :show-game
                               :current-turn 1
                               :rand-value (rand-int 100))}]])

(defn display-message
  "Displays the 'too high' or 'too low' message."
  []
  (let [{game-state :game-state
         rand-value :rand-value
         guess :guess} @app-state]
    (when (= game-state :show-comparison)
      (cond
        (> guess rand-value) [:div {:class "alert alert-info"} "Guess is too high"]
        (< guess rand-value) [:div {:class "alert alert-info"} "Guess is too low"]))))

(defn remaining-turns
  "Determins remaining turns."
  []
  (let [{max-turns :max-turns
         current-turn :current-turn} @app-state]
    (- max-turns current-turn)))

;; TODO - Logic for displaying the message/end game should be here.
(defn make-guess
  "On guess updates the game-state for the new state."
  []
  (let [{max-turns :max-turns
         current-turn :current-turn
         rand-value :rand-value
         guess :guess} @app-state]

    (if (>= current-turn max-turns)
      (swap! app-state assoc
             :game-state :show-failure)
      (if (= guess rand-value)
        (swap! app-state assoc
               :game-state :show-success)
        (swap! app-state assoc
               :game-state :show-comparison
               :current-turn (inc current-turn))))))

(defn show-success-message
  "Displays the success message when state is 'show-success'."
  []
  (let [{game-state :game-state
         rand-value :rand-value} @app-state]
    (when (= game-state :show-success)
      [:div {:class "alert alert-success"}
       "Correct! Number was " rand-value])))

(defn show-failure-message
  "Displays the failure message when state is 'show-failure'."
  []
  (let [{game-state :game-state
         rand-value :rand-value} @app-state]
    (when (= game-state :show-failure)
      [:div {:class "alert alert-danger"}
       "Failure. Number was " rand-value])))

;; TODO - validate that the number is a number
(defn validate-guess []
  ())

(defn update-guess
  "Updates the app-state guess on change."
  [e]

  ;; Reset message, so it's not updated live. Or you could get obvious clues
  ;; as to whether what you are currently typing is correct.
  (swap! app-state assoc :game-state :show-game)

  (let [raw-guess (.-target.value e)
        guess (js/parseInt raw-guess)]
    (swap! app-state assoc :guess guess)))

(defn show-main-game
  "Displays the game-state when not in a restart, success, or failure state."
  []
  (let [{game-state :game-state} @app-state]
    (when (or (= game-state :show-game) (= game-state :show-comparison))
      [:div {:class "form"}

        [:h2 "Remaining Turns " (remaining-turns)]

        [:div {:class "form-group"}
          [:label {:for "guess"} "Guess:"]

          [:input {:type "input"
                   :class "form-control"
                   :id "guess"
                   :on-change update-guess}]

          [:br]
          [:input {:type "button"
                   :class "btn btn-default"
                   :value "Make Guess"
                   :on-click make-guess}]]

        [:div [display-message]]])))

(defn number-guess
  "Creates the main game form."
  []
  [:div {:class "container-fluid"}
    [:div {:class "col-md-6 col-md-offset-3 content"}
      [:h1 {:class "text-center"} "Number Guess"]

       [:div [reset-game]]

       [:div [show-success-message]]
       [:div [show-failure-message]]

       [:div [show-main-game]]

       ; Debug only
       ;[:div (:rand-value @app-state)]

       [:div {:class "padding"}]]])

(reagent/render-component [number-guess]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

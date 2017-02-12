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
    [:input.btn.btn-default {:type "button"
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
        (> guess rand-value) [:div.alert.alert-info "Guess is too high"]
        (< guess rand-value) [:div.alert.alert-info "Guess is too low"]))))

(defn remaining-turns
  "Determines remaining turns."
  []
  (let [{max-turns :max-turns
         current-turn :current-turn} @app-state]
    ;; Add one for display, so it's not zero indexed.
    (+ 1 (- max-turns current-turn))))

(defn validate-guess
  "Validates that the guess value."
  [guess]

  ;; Check if it's a valid number, then check if it's in the correct ranges
  ;; TODO - make the ranges constants.
  (if (re-find #"^[0-9]*$" (str guess))
    (let [numeric-guess (js/parseInt guess)]
      (and (< 0 numeric-guess) (> 100 numeric-guess)))
    false))

;; TODO - Logic for displaying the message/end game should be here.
(defn make-guess
  "On guess updates the game-state for the new state."
  []
  (let [{max-turns :max-turns
         current-turn :current-turn
         rand-value :rand-value
         guess :guess} @app-state]

    (when (validate-guess guess)
      (if (>= current-turn max-turns)
        (swap! app-state assoc
               :game-state :show-failure)
        (if (= guess rand-value)
          (swap! app-state assoc
                 :game-state :show-success)
          (swap! app-state assoc
                 :game-state :show-comparison
                 :current-turn (inc current-turn)))))))

(defn show-success-message
  "Displays the success message when state is 'show-success'."
  []
  (let [{game-state :game-state
         rand-value :rand-value} @app-state]
    (when (= game-state :show-success)
      [:div.alert.alert-success
       "Correct! Number was " rand-value])))

(defn show-failure-message
  "Displays the failure message when state is 'show-failure'."
  []
  (let [{game-state :game-state
         rand-value :rand-value} @app-state]
    (when (= game-state :show-failure)
      [:div.alert.alert-danger
       "Failure. Number was " rand-value])))

(defn update-guess
  "Updates the app-state guess on change."
  [e]

  ;; Reset message, so it's not updated live. Or you could get obvious clues
  ;; as to whether what you are currently typing is correct.
  (swap! app-state assoc :game-state :show-game)

  (let [raw-guess (.-target.value e)]
    ;; Only want to allow numeric entries, although there's no real reason to
    ;; set the value here, that should probably be done in the submit. That
    ;; might be difficult without a reference to the element, though.
    (if-not (validate-guess raw-guess)
      (let [cleaned (clojure.string/replace raw-guess #"[^0-9]" "")
            numeric-guess (js/parseInt cleaned)]
        ;; Really should show an error and not submit for invalid ranges...
        ;; Well, the entire thing really. That's a next step.
        (cond
          (< numeric-guess 0) (set! (.-target.value e) 0)
          (> numeric-guess 100) (set! (.-target.value e) 100)
          (js/isNaN numeric-guess) (set! (.-target.value e) "")
          :else (set! (.-target.value e) numeric-guess)))
      (swap! app-state assoc :guess (js/parseInt raw-guess)))))

(defn submit-on-enter
  "Runs the guess on hitting enter."
  [e]

  (when (= (.-keyCode e) 13)
    (make-guess)))

(defn show-main-game
  "Displays the game-state when not in a restart, success, or failure state."
  []
  (let [{game-state :game-state} @app-state]
    (when (or (= game-state :show-game) (= game-state :show-comparison))
      [:div.form

        [:h2 "Remaining Turns " (remaining-turns)]

        [:div.form-group
          [:label {:for "guess"} "Guess:"]

          [:input.form-control {:type "input"
                                :id "guess"
                                :on-change update-guess
                                :on-key-up submit-on-enter}]

          [:br]
          [:input.btn.btn-default {:type "button"
                                   :value "Make Guess"
                                   :on-click make-guess}]]

        [:div [display-message]]])))

(defn number-guess
  "Creates the main game form."
  []
  [:div.container-fluid
    [:div.col-md-6.col-md-offset-3.content
      [:h1.text-cetner "Number Guess"]

        [:div [reset-game]]

        [:div.messages
          [:div [show-success-message]]
          [:div [show-failure-message]]
        ]

        [:div [show-main-game]]

        ; Debug only
        ;[:div (:rand-value @app-state)]

        [:div.padding]]])

(reagent/render-component [number-guess]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

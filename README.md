# Clojure/Reagent Number Guess

A game to guess a random number using clojure and reagent.

## Overview

This will generate a number and then allow the user to guess a number of times
telling them whether the guess was higher or lower. Until you guess the correct
number or run out of guesses.

This is a simple test of the technology. Om is probably more popular to use with
clojurescript, but reagent seemed simpler to get up and running.

This is run using figwheel. See the setup below on how to run the project.

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

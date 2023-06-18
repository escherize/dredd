# Dredd

The self-modifying test tool. 

Inspired by [judge](https://github.com/ianthehenry/judge).

## Usage

1. Include dredd in your project.

`io.github.escherize/dredd {:git/sha "8979b4cc84503da7ac97a3ca5f16df8bab292762"}`

2. Start `judge`ing.

A `judge` form takes 2 or 3 things. 
First a `name`, like any defmethod. 
Second an actual value to evaluate.
Third (optional): the expected value of the evaluated actual value.

Example:
``` clojure
(ns here
  (:require [dredd.core :as dredd]))

(defn my-fxn [x] (if (= x 3) 5 x))

(dredd/judge a (my-fxn 1))
(dredd/judge c (my-fxn 3))
```


3. Ensure they're right with `fix!`

Automatically:

``` clojure
(dredd/fix!)
```

Interactively:

``` clojure
(dredd/fix! :ask)
```

4. `fix!` updates actual forms to match their actually correct values:

``` clojure
(ns here
  (:require [dredd.core :as dredd]))

(defn my-fxn [x] (if (= x 3) 5 x))

(dredd/judge a (my-fxn 1) 1)
(dredd/judge c (my-fxn 3) 5)
```

# Dredd 🤖✏️🟩

Automatic Test Updates (and Enforcement someday)

_Inspired by [judge](https://github.com/ianthehenry/judge)._

## Usage

1. Include dredd in your project.

`io.github.escherize/dredd {:git/sha "eb72b86b4861276485acbcab3f1374ec11e01592"}`

``` clojure
(require '[dredd.core :refer [j]])
```

2. As you work, put in some `j` forms.

A `j` form takes one or two values:

Just one value: it will always eval to `:fail`.
``` clojure
(j (* 10 11))
;;=> :fail
```

The wrong expected value will eval to `:fail`.
``` clojure
(j (* 10 11) "wrong")
;;=> :fail
```

The right expected value evals to `:true` (🎉).
```clojure
(j (* 10 11) 110)
;;=> :pass
```

## Automatic-Rewrite API

There are 3 public entrypoints: `check`, `fix` and `ask`.

### `fix`

Calling `fix` on a namespace will rewrite all j forms to be correct.

``` clojure
(ns my-ns (:require [dredd.dredd :refer [j]]))
;; blank
(j (* 234 593 -3))
;; wrong
(j (first "hi") \i)
(comment (dredd/fix))
```
Evaluating `(dredd/fix)` in this namespace will _rewrite it to be_:
``` clojure
(ns my-ns (:require [dredd.dredd :refer [j]]))
;; blank
(j (* 234 593 -3) -416286)
;; wrong
(j (first "hi") \h)
(comment (dredd/fix))
```

### `ask`

`ask` is similar to `fix`, but will show a prompt where you get shown
information about the change you're about to make. Accept the change by entering
y or just hitting enter, decline with n.

Here's the output of me running ask from `dredd.dredd-test`, and sending <kbd>y</kbd> + <kbd>Enter</kbd> to stdin twice:

``` text
*==================================================
*       Fixing judge at: dredd.dredd-test:23
*       When evaluating: (* 10 10)
*              Expected: No value was provided
*      But it should be: 100
*                    ---------
* Do you want me to fix it? [Y/n]
Dredd is fixing j_23 to be 100

*==================================================
*       Fixing judge at: dredd.dredd-test:25
*       When evaluating: (* 2 3)
*              Expected: "wrong"
*      But it should be: 6
*                    ---------
* Do you want me to fix it? [Y/n]
Dredd is fixing j_25 to be 6
```

 And it's fixed.

### `check`

To see the result of judges in your namespace without editing anything.

``` clojure

(dredd/check)
;;=> {:pass [test-one] :fail [test-two test-three]}
```

## Caveats

- I wouldn't try it with think this with non-deterministic functions.

- Your editor might not be happy having file contents ripped out from under it. Sorry!

## Roadmap / TODOs / Ideas

- [ ] check stdout of an expression
- [ ] transform dredd to a simple deftest
- [ ] transform a simple deftest to dredd
- [ ] detect dredd forms if they aren't aliased as j or refer'd to directly
- [ ] how to run as tests
- [ ] say thanks to ianthehenry

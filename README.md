# Dredd

The self-modifying test tool. 

Inspired by [judge](https://github.com/ianthehenry/judge).

## Usage

1. Include dredd in your project.

`io.github.escherize/dredd {:git/sha "70d811bf9925a545421a26dde4a33755531c5fb4"}`

2. Start `judge`ing.

A `judge` form takes 1, 2 or 3 things. 

``` clojure
(require '[dredd.core :as dredd])

;; one arg
(dredd/judge (* 10 10))
;;           ^ expression to check 

;; two args
(dredd/judge hard-math (* 234 593 -3))
;;           ^ added name

;; three args
(dredd/judge two-plus-2 (+ 2 2) 7)
;;         added expected value ^
```

There are 2 public entrypoints to make things happen: `fix` and `ask`. Both of them work basically the same way, which is they will look for `dredd/judge` or `judge` forms AT THE TOP LEVEL of your namespace and fix and fill in answers for them.

### fix

Calling fix on a namespace with the above judge forms will "fix" them:

#### three args
``` clojure
;; three
(dredd/judge two-plus-2 (+ 2 2) 7)
;;         added expected value ^
```
Evaluating `(dredd/fix)` in the same ns will _rewrite your test to be:_
``` clojure
;; three
(dredd/judge two-plus-2 (+ 2 2) 4)
;;         added expected value ^
```
Yeah this uses rewrite-clj so the indentation and comments will be preserved.

#### two args
Consider the 2 arg variant. We don't want to be _corrected_ we just want the answer filled in. That's what fix will do: 
``` clojure
;; two args
(dredd/judge hard-math (* 234 593 -3))
```
Evaluating `(dredd/fix)` will _rewrite your test to be:_
``` clojure
;; two args
(dredd/judge hard-math (* 234 593 -3) -416286)
```

#### one arg
Similar to 2 arg version, we just want the answer, dangit!

``` clojure
;; one arg
(dredd/judge (* 10 10))
```
Evaluating `(dredd/fix)` will _rewrite your test to be:_
``` clojure
;; one arg
(dredd/judge green-apple (* 10 10) 100)
```


### ask

Ask is similar to fix, but will show a prompt where you get shown information
about the change you're about to make (by entering y or just hitting enter).

Here's the output of me running ask from `dredd.dredd-test` on line 34:

```

*==================================================
*      Fixing judge for:  copper-mangosteen (autgen'd name)
*       When evaluating:  (* 10 10)
*                   Got:  (No value was there)
*      But it should be:  100
*                    ---------
* Do you want me to fix it? [Y/n]

*==================================================
*      Fixing judge for:  hard-math 
*       When evaluating:  (* 234 593 -3)
*                   Got:  (No value was there)
*      But it should be:  -416286
*                    ---------
* Do you want me to fix it? [Y/n]

*==================================================
*      Fixing judge for:  two-plus-2 
*       When evaluating:  (+ 2 2)
*                   Got:  7
*      But it should be:  4
*                    ---------
* Do you want me to fix it? [Y/n]
```

After this, it was fixed. Yay!

## Caveats

- I don't think this will work very well with stateful functions, ymmv. When using
  this its best to split up the I/O sort of things from the difficult to reason
  about yet pure pieces.

- Your editor might not be happy having file contents ripped out from under it. Sorry!

## Roadmap / TODOs

- [ ] check stdout of an expression
- [ ] say thanks to ianthehenry



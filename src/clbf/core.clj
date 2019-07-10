(ns clbf.core
  (:gen-class))

; A description of the BF language
(comment "
  
>	increment the data pointer (to point to the next cell to the right).
<	decrement the data pointer (to point to the next cell to the left).
+	increment (increase by one) the byte at the data pointer.
-	decrement (decrease by one) the byte at the data pointer.
.	output the byte at the data pointer.
,	accept one byte of input, storing its value in the byte at the data pointer.
[	if the byte at the data pointer is zero, then instead of moving the instruction pointer forward to the next command, jump it forward to the command after the matching ] command.
]	if the byte at the data pointer is nonzero, then instead of moving the instruction pointer forward to the next command, jump it back to the command after the matching [ command.

"
)

; Mapping from characters to keywords for lexing
(def bf-symbols {\> :rmov
                 \< :lmov
                 \+ :add
                 \- :sub
                 \[ :while
                 \] :end
                 \. :print
                 \, :input})

(defn any-nil-symbols? [symbols] (not (every? (complement nil?) symbols)))

(defn check-for-errors
  "Check for any nil symbols or mismatched braces"
  [symbols]
  (if (any-nil-symbols? symbols)
    (throw (Exception. "Invalid character in source code."))
    symbols))

(defn clbf-read
  "Repl: Reader - Applies symbol map onto source code char by char"
  [src-code]
  (check-for-errors 
    (map bf-symbols 
      (seq src-code))))

(defn clbf-eval
  "Repl: Evaluator"
  [arg] ; do nothing with this
  arg)

(defn repl
  "Repl Loop"
  []
  (println (clbf-eval (clbf-read (read-line))))
  (recur))

(defn -main
  "Repl for Brainf*: Main entry"
  [& args]
  (println "Brainf* Interpreter: ")
  (repl))
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

")

; Mapping from characters to keywords for lexing
(def bf-symbols {\> :rmov
                 \< :lmov
                 \+ :add
                 \- :sub
                 \[ :while
                 \] :end
                 \. :print
                 \, :input})

; Helper functions
(defn any-nil?
  "Checks for any nil values in sequence"
  [symbols]
  (some nil? symbols))

(defn is-bracket-symbol?
  [sym]
  (or (= sym :while) (= sym :end)))

(defn not-empty?
  [coll]
  (not (empty? coll)))

; Data types
; BF code symbols, index to next symbol to execute, index of last open bracket,
; array of integers (data), current data pointer
(defrecord BFState [code code-idx data data-idx])

(defn program-complete?
  "Checks if the code pointer is past the end of the code symbol vector"
  [bfstate]
  (>= (get bfstate :code-idx) (count (get bfstate :code))))

; Primary functions for interpreter
(defn mismatched-brackets?
  "Checks if any brackets don't form a matching pair"
  [symbols]
  (let [brackets (filter is-bracket-symbol? symbols)]
    (loop [brackets brackets
           bracket-stack '()]
      (if (empty? brackets) ; Nothing left to process, return val depends on stack emptiness
        (not-empty? bracket-stack)
        (let [bracket (first brackets)] ; Still have brackets
          (if (= bracket :while)
            ; If it's an open bracket, push on stack and recur
            (recur (rest brackets) (conj bracket-stack bracket))
            ; Otherwise, pop off stack. If stack is empty, return true (mismatch)
            (if (nil? (peek bracket-stack))
              true
              (recur (rest brackets) (pop bracket-stack)))))))))

(defn check-for-errors
  "Check for any nil symbols or mismatched braces"
  [symbols]
  (cond
    ; Unrecognized characters are just ignored - this is for debugging only
    ; (any-nil? symbols)
    ; (throw (Exception. "Invalid character in source code."))
    (mismatched-brackets? symbols)
    (throw (Exception. "Mismatched brackets in source code."))
    :else
    symbols))

(defn clbf-read
  "Repl: Reader - Transform all source code chars into internal symbol representation"
  [src-code]
  (filter (complement nil?)
          (check-for-errors
           (map bf-symbols
                (seq src-code)))))

; Evaluation functions
(defn bfstate-advance
  "Increase the code index pointer by 1"
  [bfstate]
  (assoc bfstate :code-idx (inc (get bfstate :code-idx))))

(defn bfstate-rmov
  "Increment the data pointer"
  [bfstate]
  (bfstate-advance (assoc bfstate :data-idx (inc (get bfstate :data-idx)))))

(defn bfstate-lmov
  "Decrement the data pointer"
  [bfstate]
  (bfstate-advance (assoc bfstate :data-idx (dec (get bfstate :data-idx)))))

(defn bfstate-add
  "Increment the byte at the data pointer"
  [bfstate]
  (let [old-data (get bfstate :data)
        data-idx (get bfstate :data-idx)
        old-data-val (nth old-data data-idx)
        new-data-val (inc old-data-val)
        new-data (assoc old-data data-idx new-data-val)]
    (bfstate-advance (assoc bfstate :data new-data))))

(defn bfstate-sub
  "Decrement the byte at the data pointer"
  [bfstate]
  (let [old-data (get bfstate :data)
        data-idx (get bfstate :data-idx)
        old-data-val (nth old-data data-idx)
        new-data-val (dec old-data-val)
        new-data (assoc old-data data-idx new-data-val)]
    (bfstate-advance (assoc bfstate :data new-data))))

(defn bfstate-print
  "Print the char code of the byte at the data pointer"
  [bfstate]
  (let [old-data (get bfstate :data)
        data-idx (get bfstate :data-idx)
        data-val (nth old-data data-idx)]
    (print (char data-val))
    (bfstate-advance bfstate)))

(defn bfstate-input
  "Read the current character into the current data cell"
  [bfstate]
  (let [old-data (get bfstate :data)
        data-idx (get bfstate :data-idx)
        ;new-data-val (.read *in*)
        new-data-val (int (nth (seq (read-line)) 0))
        new-data (assoc old-data data-idx new-data-val)]
    ; (print new-data-val)
    (bfstate-advance (assoc bfstate :data new-data))))

(defn bfstate-jump-forward
  "Find the matching :end and jump to the symbol after it (matching => no more unmatched whiles)"
  [bfstate]
  (loop [idx (get bfstate :code-idx)
         active-whiles -1]
    (let [code (get bfstate :code)
          code-val (nth code idx)]
      (case code-val
        :while (recur (inc idx) (inc active-whiles))
        :end (if (zero? active-whiles)
               (assoc bfstate :code-idx (inc idx))
               (recur (inc idx) (dec active-whiles)))
        (recur (inc idx) active-whiles)))))

(defn bfstate-while
  "If the byte at the data pointer is zero, then instead of moving the instruction pointer forward to the next command, jump it forward to the command after the matching ] command."
  [bfstate]
  (let [old-data (get bfstate :data)
        data-idx (get bfstate :data-idx)
        data-val (nth old-data data-idx)]
    (if (zero? data-val)
      (bfstate-jump-forward bfstate)
      (bfstate-advance bfstate))))

(defn bfstate-jump-backward
  "Find the matching :while and jump to the symbol after it (matching => no more unmatched ends)"
  [bfstate]
  (loop [idx (get bfstate :code-idx)
         active-ends -1]
    (let [code (get bfstate :code)
          code-val (nth code idx)]
      (case code-val
        :end (recur (dec idx) (inc active-ends))
        :while (if (zero? active-ends)
                 (assoc bfstate :code-idx (inc idx))
                 (recur (dec idx) (dec active-ends)))
        (recur (dec idx) active-ends)))))

(defn bfstate-end
  "if the byte at the data pointer is nonzero, then instead of moving the instruction pointer forward to the next command, jump it back to the command after the matching [ command."
  [bfstate]
  (let [old-data (get bfstate :data)
        data-idx (get bfstate :data-idx)
        data-val (nth old-data data-idx)]
    (if (not (zero? data-val))
      (bfstate-jump-backward bfstate)
      (bfstate-advance bfstate))))

(defn clbf-eval-loop
  "Execute symbol by symbol"
  [bfstate]
  (loop [bfstate bfstate]
    (if (program-complete? bfstate)
      bfstate
      (let [current-symbol (nth (get bfstate :code) (get bfstate :code-idx))]
        ; (println current-symbol)
        (case current-symbol
          :rmov (recur (bfstate-rmov bfstate))
          :lmov (recur (bfstate-lmov bfstate))
          :add (recur (bfstate-add bfstate))
          :sub (recur (bfstate-sub bfstate))
          :print (recur (bfstate-print bfstate))
          :input (recur (bfstate-input bfstate))
          :while (recur (bfstate-while bfstate))
          :end (recur (bfstate-end bfstate))
          (throw  (Exception. "Unrecognized symbol in parsed source code")))))))


(defn clbf-eval
  "Runs the evaluator loop, passing it the initial program state"
  [symbols]
  (println "")
  (let [result (clbf-eval-loop (map->BFState {:code (vec symbols)
                                              :code-idx 0
                                              :data (vec (replicate 10 0))
                                              :data-idx 0}))]
    (println "")
    (println "")
    (println result)))

(defn -main
  "Execute Brainfuck code: print output and final internal representation"
  [& args]
  (println "Enter Brainfuck code, then pass EOF character twice (likely Ctrl + D):")
  (clbf-eval (clbf-read (slurp *in*))))
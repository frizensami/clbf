# clbf

Brainfuck interpreter written in Clojure

## Installation

### Pre-requisites
- Java (known to work with Java version 1.8.0_201)
- `java` binary accessible from the command line 

### CLBF Jar File
Download latest jar file (`clbf-0.1.0-SNAPSHOT-standalone.jar`) from **Releases** tab.

## Usage
In the same folder as the downloaded jar file:

    $ java -jar clbf-0.1.0-SNAPSHOT-standalone.jar

## Examples

### Interactive
```
$ java -jar clbf-0.1.0-SNAPSHOT-standalone.jar
Enter Brainfuck code, then pass EOF character twice (likely Ctrl + D):
>+++++++++[<++++++++>-]<.>+++++++[<++++>-]<+.+++++++..+++.[-]
>++++++++[<++++>-] <.>+++++++++++[<++++++++>-]<-.--------.+++
.------.--------.[-]>++++++++[<++++>- ]<+.[-]++++++++++.

Hello world!


#clbf.core.BFState{:code [:rmov :add :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :add :add :add :add :rmov :sub :end :lmov :print :rmov :add :add :add :add :add :add :add :while :lmov :add :add :add :add :rmov :sub :end :lmov :add :print :add :add :add :add :add :add :add :print :print :add :add :add :print :while :sub :end :rmov :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :rmov :sub :end :lmov :print :rmov :add :add :add :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :add :add :add :add :rmov :sub :end :lmov :sub :print :sub :sub :sub :sub :sub :sub :sub :sub :print :add :add :add :print :sub :sub :sub :sub :sub :sub :print :sub :sub :sub :sub :sub :sub :sub :sub :print :while :sub :end :rmov :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :rmov :sub :end :lmov :add :print :while :sub :end :add :add :add :add :add :add :add :add :add :add :print], :code-idx 176, :data [10 0 0 0 0 0 0 0 0 0], :data-idx 0}
```

### From file (try helloworld.bf from this repo)

```
sriram@sriram-G551JK:~/Projects/clbf$ java -jar target/uberjar/clbf-0.1.0-SNAPSHOT-standalone.jar < helloworld.bf 
Enter Brainfuck code, then pass EOF character twice (likely Ctrl + D):

Hello world!


#clbf.core.BFState{:code [:rmov :add :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :add :add :add :add :rmov :sub :end :lmov :print :rmov :add :add :add :add :add :add :add :while :lmov :add :add :add :add :rmov :sub :end :lmov :add :print :add :add :add :add :add :add :add :print :print :add :add :add :print :while :sub :end :rmov :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :rmov :sub :end :lmov :print :rmov :add :add :add :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :add :add :add :add :rmov :sub :end :lmov :sub :print :sub :sub :sub :sub :sub :sub :sub :sub :print :add :add :add :print :sub :sub :sub :sub :sub :sub :print :sub :sub :sub :sub :sub :sub :sub :sub :print :while :sub :end :rmov :add :add :add :add :add :add :add :add :while :lmov :add :add :add :add :rmov :sub :end :lmov :add :print :while :sub :end :add :add :add :add :add :add :add :add :add :add :print], :code-idx 176, :data [10 0 0 0 0 0 0 0 0 0], :data-idx 0}
```
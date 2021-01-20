# Description

This is my implementation of Ukkonen's algorithm in Java. This started as an attempt to solve a particular Hyperskill/Jetbrains Academy project involving calculating the number of distinct substrings of a string; I have been having issues passing the final test on the problem due to exceeding the time limit, and have thus chosen to use this algorithm to try to solve is. 

Notably, Ukkonen's algorithm is an O(n) suffix tree construction algorithm (for constant-sized alphabets). Once a suffix tree is obtained, it is a simple matter to calculate the number of distinct substrings (this is equivalent to the number of characters on all edges of the suffix tree), which is again a O(n) algorithm; so the entire algorithm ought to be O(n), which should hopefully solve my problem. 

I do not presume guarantee correctness, but this implementation does seem to test the simple test cases I have manually run through it. 

The following SO link was particularly helpful: https://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english/9513423#9513423 
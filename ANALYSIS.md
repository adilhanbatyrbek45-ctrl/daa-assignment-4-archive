# Assignment 4 — Analysis

## 1. Longest Common Subsequence (LCS)

For prefixes `a[0..i)` and `b[0..j)`, `dp[i][j]` stores the maximum common subsequence length. Empty prefixes have value zero. If the final characters match, the state extends `dp[i-1][j-1]`; otherwise it takes the larger of `dp[i-1][j]` and `dp[i][j-1]`. Reconstruction walks backwards: matching characters are included, and on a mismatch it moves to a neighboring state with the same optimal value. Reversing the collected characters produces an LCS. Time and memory are O((n+1)(m+1)).

## 2. Adjacent block merging

`dp[l][r]` is the minimum cost to merge original blocks in interval `[l,r]`. Prefix sums provide the interval's total size. The last operation splits the interval at some `k`, so the recurrence is `dp[l][k] + dp[k+1][r] + sum(l,r)`. The stored split gives the witness. The plan is generated postorder: both child intervals are merged before their parent interval, ensuring each operation is valid and chronological. Time is O(n^3), memory O(n^2).

## 3. Maximum-weight independent set on a tree

Root the tree at vertex 0. `take[u]` is the best value in the subtree when `u` is selected; `skip[u]` is the best value when it is not selected. For each child `v`, `take[u]` adds `skip[v]`, while `skip[u]` adds `max(take[v],skip[v])`. An iterative traversal creates a parent-before-child order; processing it backwards is postorder and avoids stack overflow on a long chain. Reconstruction selects `u` only when its parent permits it and `take[u] > skip[u]`. Ties choose skip, which is still optimal. The empty set handles negative weights. Time and memory are O(n).

## 4. Naive string search

Every start from 0 through `n-m` is checked character by character. Matches are emitted in increasing order and overlapping windows are examined. The empty pattern matches all boundaries from 0 through `n`. Time is O(nm+n+1), output storage O(z), and auxiliary storage O(1).

## 5. KMP

The prefix function `pi[i]` is the length of the longest proper prefix of `pattern[0..i]` that is also its suffix. On mismatch, KMP follows `pi[j-1]` instead of restarting. After a full match, the same fallback retains possible overlaps. Empty patterns are handled before indexing. Time is O(n+m+z), auxiliary memory O(m), output memory O(z).

## 6. Rabin–Karp

The pattern and first text window are hashed with polynomial base 31 modulo 101, using ASCII values. The highest power is assigned to the leftmost character. The rolling update removes the outgoing character, multiplies by the base, and adds the incoming character; the remainder is normalized. Equal hashes are verified character by character because collisions are possible. Empty and longer-than-text patterns are handled separately. Worst-case time can be O(nm); the rolling hash uses O(1) auxiliary memory and output uses O(z).

## 7. Suffix array and Kasai LCP

Prefix doubling sorts suffixes by their first `k` characters using pairs of ranks: `(rank[i], rank[i+k])`, with `-1` for a missing second half. Each round assigns new equivalence-class ranks and doubles `k`. The suffix array contains only the `n` nonempty suffixes. Kasai uses the inverse suffix-array position and reuses the previous LCP length minus one, giving O(n) LCP computation. The largest adjacent LCP is the longest repeated substring; its starting index is the corresponding suffix start. Prefix doubling with comparison sorting takes O(n log^2 n) time, and memory is O(n).

## 8. Aho–Corasick

All patterns are inserted into one trie. Breadth-first construction computes failure links and completes transitions for the fixed printable-ASCII alphabet. For each text, the automaton state and visit counters are reset. Each consumed character increments the reached state. Processing BFS states in reverse order propagates each state's visits to its failure parent; consequently suffix-pattern matches are included. Patterns that are duplicates share a terminal state but each original dictionary position receives its own output column. Time per text is O(|text|+S+p), with O(95S+p) automaton storage.

## Validation

The Java implementation was compiled using `javac --release 17` through `python tools/run.py --language Java --setup`. The public checker was run with `python tools/run.py --language Java --check`; all 44 public cases and the repeated mixed-task batch passed.

## Search benchmark

The assignment requires a reproducible comparison of naive, KMP, and Rabin–Karp on common-domain text/pattern pairs, including at least three measurements per algorithm and medians. Record the actual Java runtime version, machine details, generator and seed, and ensure compilation is outside the timed region. Verify all three algorithms return identical positions before timing. Include both random and repetitive data. Do not claim that these measurements prove asymptotic bounds. Add the measured tables here after running the benchmark on the submission machine.

AI Assistance: ChatGPT was used to help understand the assignment requirements
Run instructions:

1. Put the zipeed directory into the "src/projects/" path
2. Compile the code
3. Run SINALGO and choose the "Maman12" option
4. Press on build tree and follow the instructions
5. Run the Algorithm!

Now you can see the MIS of the tree colored in RED.

The algorithm computing the MIS of a tree at O(log*n).



How does it work?

There is two stages to the algorithm:

1. Coloring the tree at constant num of color (8) in O(log*n),
    using the principals of Six_VCol alg.
2. Creating the MIS at constant time (round foreach color) using the VCol_MIS alg.

Therefore, O(log*n) + O(1) = O(log*n)

Here for any problem,
Shoval Cohen
ID: 208748152
Mail: ShovalC@outlook.com
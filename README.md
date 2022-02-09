# Project 1: B-tree Indexing Structure
* name: Xingjian Qian
* NEU ID: 002192922

#### Assumption
##### Usage:
There are two ways to create B-tree.
```
Btree tree = new Btree(); // create a b-tree with the default nodesize 5(Store up to 5 child pointers and 4 data pointers)
Btree tree = new Btree(4);// create a b-tree with nodesize 4
```
Insert
```
tree.Insert(5);
tree.Insert(10);
```
Query
```
tree.Lookup(10); //return true if 10 is in the tree, otherwise return false
```
Display
```
tree.Display(); // print out the indexing tree structure under root node, which means display the whole tree
tree.Display(10); // print out the indexing tree structure under node 10
tree.Display(tree.getRoot()); // print out the indexing tree structure under root node, which means display the whole tree
```

##### Display format:
The display format is like the codes below, it is json format so it could easily visualize in some web json visulizer(like fehelper) in tree format.
```
{
    "node0": {
        "node0": {
            "leafnode0": {
                "datapointer0":1,
                 "datapointer1":2,
             }, 
            "datapointer0":3,
             "leafnode1": {
                "datapointer0":4,
                 "datapointer1":5,
             }, 
            "datapointer1":6,
             "leafnode2": {
                "datapointer0":10,
                 "datapointer1":11,
             }, 
            "datapointer2":12,
             "leafnode3": {
                "datapointer0":13,
                 "datapointer1":15,
                 "datapointer2":20,
                 "datapointer3":22,
             }, 
        }, 
        "datapointer0":30,
         "node1": {
            "leafnode0": {
                "datapointer0":32,
                 "datapointer1":33,
             }, 
            "datapointer0":34,
             "leafnode1": {
                "datapointer0":40,
                 "datapointer1":50,
             }, 
            "datapointer1":60,
             "leafnode2": {
                "datapointer0":85,
                 "datapointer1":95,
                 "datapointer2":100,
             }, 
        }, 
    }, 
}
```

    
#### Any Known Bugs
So far, I did not see any bugs during the test if the input is __correctly(i.e the nodesize 
of the btree should greater than 2)__
#### Limitation
The root position in the node array will change because when I implement the propagation process, 
I use the origin node to be the right child after split and use a temp node to move upward, which means 
when it reach the root, if node overflow happens, I need to init a new node and store the temp node(new root) into it.
So, by this way, the new root will be the last element on nodes array. This may be a limitation since I think it would be better if the root was fixed and with index = 0. But so far, it has not caused any bugs or affected the correctness of the code.

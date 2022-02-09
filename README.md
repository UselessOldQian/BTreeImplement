# Project 1: B-tree Indexing Structure
* name: Xingjian Qian
* NEU ID: 002192922

#### Assumption
在连续几行的文本开头加入1个Tab或者4个空格。

    欢迎到访
    很高兴见到您
    祝您，早上好，中午好，下午好，晚安
    
#### Any Known Bugs
So far, I did not see any bugs during the test if the input is __correctly(i.e the nodesize 
of the btree should greater than 2)__
#### Limitation
The root position in the node array will change because when I implement the propagation process, 
I use the origin node to be the right child after split and use a temp node to move upward, which means 
when it reach the root, if node overflow happens, I need to init a new node and store the temp node(new root) into it.
So, by this way, the new root will be the last element on nodes array. This may be a limitation since I think it would be better if the root was fixed and with index = 0. But so far, it has not caused any bugs or affected the correctness of the code.

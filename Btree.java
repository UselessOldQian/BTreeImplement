/*
 * name: Xingjian Qian
 * NEU ID: 002192922
 * CS7280 Special Topics in Database Management
 * Project 1: B-tree implementation.
 *
 * You need to code for the following functions in this program
 *   1. Lookup(int value) -> nodeLookup(int value, int node)
 *   2. Insert(int value) -> nodeInsert(int value, int node)
 *   3. Display(int node)
 *
 */

import java.util.Arrays;

final class Btree {

  /* Size of Node. */
  private int NODESIZE = 5;

  /* Node array, initialized with length = 1. i.e. root node */
  private Node[] nodes = new Node[1];

  /* Number of currently used nodes. */
  private int cntNodes;

  /* Pointer to the root node. */
  private int root;

  /* Number of currently used values. */
  private int cntValues;

  // the node used in backward insert
  private Node tempNode;

  /*
   * B+ tree Constructor.
   */
  public Btree() {
    root = createLeaf();
    //nodes[root].children[0] = createLeaf();
  }

  /**
   * init a btree with specified nodesize;
   * @param nodesize the specified nodesize
   */
  public Btree(int nodesize) {
    this.NODESIZE = nodesize;
    root = createLeaf();
    //nodes[root].children[0] = createLeaf();
  }

  /*********** B tree functions for Public ******************/

  /*
   * Lookup(int value)
   *   - True if the value was found.
   */
  public boolean Lookup(int value) {
    return nodeLookup(value, root);
  }

  /*
   * Insert(int value)
   *    - If -1 is returned, the value is inserted and increase cntValues.
   *    - If -2 is returned, the value already exists.
   */
  public void Insert(int value) {
    int insertStatus = nodeInsert(value, root);
    // return -1 means insert successfully
    if (insertStatus == -1) {
      cntValues++;
      System.out.println("Insert successfully");
    } else if (insertStatus == -2) {
      //return -2 mean index already exist
      System.out.println("This index already exist");
    }
    else{
      // if return 1, that means there is still a new node need to be added, this situation will
      // happen when then root node merge with a propagate new node and caused overflow,
      // so init a new node to store this new root and make the count of nodes++
      int newRoot = initNode();
      this.nodes[newRoot] = this.tempNode;
      this.root = newRoot;
      cntValues++;
      System.out.println("Insert successfully");
    }
  }

  /**
   * display the tree structure from root node, the display format is like json file,
   * which can easily visualize the structure of the the tree
   */
  public void Display(){
    String displayStr = displayHelper(root,0,1);
    System.out.println("********* The indexing tree structure under ROOT**********");
    System.out.println("{\n"+displayStr+"}");
  }

  /**
   * display the tree structure from specified node
   * @param node
   */
  public void Display(int node){
    String displayStr = displayHelper(node,0,1);
    System.out.println(
        String.format("********* The indexing tree structure under node %d **********", node));
    System.out.println("{\n"+displayStr+"}");
  }

  /**
   * display the structure of btree recursively
   * @param nodeInd current node index to display
   * @return
   */
  String displayHelper(int nodeInd, int displayid, int depth){
    // the indentation needed to format the json file displayed, for every depth increase, add 4*" " which is like one tab
    String indentation = new String(new char[4 * depth]).replace('\0', ' ');
    // the indentation needed to add before the data pointer in this node
    String singleIndentation = new String(new char[4]).replace('\0', ' ');

    String resultStr = "";
    // get the current node
    Node curNode = this.nodes[nodeInd];
    // check whether current node is leaf node
    boolean isleaf = this.isLeaf(curNode);
    // if current node is leaf node, the first line should like [ "node0": { " ], otherwise it should
    // like [ "leafnode0": { ]
    resultStr += indentation + (isleaf ? "\"leafnode"+displayid+"\": ": "\"node"+displayid+"\": ")+"{\n";
    for(int i = 0;i<curNode.size;i++){
      if(!isleaf) {
        //if current node is not leaf node, then go down recursively, and the indentation should plus " "*4
        resultStr += displayHelper(curNode.children[i],i,depth+1);
      }
      // display the data pointer in current node
      resultStr += indentation + singleIndentation + "\"datapointer" + i + "\":";
      resultStr += curNode.values[i];
      resultStr += ",\n ";
    }

    // because in the node which is not leaf, the children number is greater than the data pointers number by 1,
    // we need to display the last child information by this way
    if(!isleaf) {
      resultStr += displayHelper(curNode.children[curNode.size],curNode.size,depth+1);
    }
    resultStr += indentation + "}, \n";
    return resultStr;
  }

  /*
   * CntValues()
   *    - Returns the number of used values.
   */
  public int CntValues() {
    return cntValues;
  }

  /*********** B-tree functions for Internal  ******************/

  /*
   * nodeLookup(int value, int pointer)
   *    - True if the value was found in the specified node.
   *
   */

  /**
   *
   * @param value the value need to find
   * @param pointer the index on nodes array.
   * @return
   */
  private boolean nodeLookup(int value, int pointer) {
    Node curNode = this.nodes[pointer];

    // the return value of binary search could be either the exact index of the value we search or
    // the child node index that is possible to have the searched value.
    int potentialIndex = binarySearch(curNode.values, curNode.size, value);
    boolean isleaf = this.isLeaf(curNode);

    // this check is to avoid index out of bound exception, if the potentialIndex == curNode.size,
    // it means the value we search is larger than the max value of curNode.values, so if it is a leaf
    // node, it indicates that the value is not exist in the tree, otherwise we go down to the last child
    // to find the value
    if(potentialIndex==curNode.size){
      if(isleaf){
        return false;
      }else {
        return nodeLookup(value,curNode.children[potentialIndex]);
      }
    }

    //check whether the potential index we get from binary search is exactly the value we search
    if(curNode.values[potentialIndex] == value)
      return true;
    //otherwise, we need to go done to the child with the potential index, if the current node is
      // already the leaf node, it means the search value is not exist.
    else {
      if(isleaf){
        return false;
      }else{
        return nodeLookup(value,curNode.children[potentialIndex]);
      }
    }
  }

  /*
   * nodeInsert(int value, int pointer)
   *    - -2 if the value already exists in the specified node
   *    - -1 if the value is inserted into the node or
   *            something else if the parent node has to be restructured
   */
  private int nodeInsert(int value, int pointer) {
    Node curNode = this.nodes[pointer];
    // check whether current node size is 0, this could happen when the first node is being inserted into the tree
    if (curNode.size == 0) {
      // simply make the first value of current node = value, then make the current node size++,
      // BTW, I use node.size to indicate the number of data pointers in the node, NOT the children number of current node
      curNode.values[0] = value;
      curNode.size++;
      return -1;
    }
    // just like the way in nodelookup, use binary search to find the potential index to insert the value
    int potentialIndex = this.binarySearch(curNode.values,curNode.size,value);

    //check whether the value is already in the current node, if true, return -2 indicating it already exists
    if(potentialIndex < curNode.size && curNode.values[potentialIndex]== value){
      return -2;
    }

    //if we reach the leaf node
    if (this.isLeaf(curNode)) {
      // cause this node is the leaf node, we should insert value exactly into current node's values array
      insertValue(curNode.values,value,potentialIndex);
      curNode.size++;

      // if the current node size(the number of values) is equal to the maximum node size, we need to
      // split the node and use a temp node to store this new node then return 1, which means need to add the
      // mid point to the parent node,
      // otherwise we only need to return -1, which means insert successfully
      if(curNode.size<this.NODESIZE){
        return -1;
      }else{
        this.tempNode = this.split(pointer);
        return 1;
      }
    }

    int insertStatus = nodeInsert(value, curNode.children[potentialIndex]);
    if(insertStatus >= 0){
      return merge(pointer, this.tempNode);
    }
    return insertStatus;
  }

  /**
   * merge two node, return -1 if the merge operation does not exceed the max node size, otherwise return 1
   * @param originNodeIndex
   * @param newNode
   * @return
   */
  int merge(int originNodeIndex, Node newNode) {
    Node originNode = this.nodes[originNodeIndex];
    int pos = this.binarySearch(originNode.values,originNode.size,newNode.values[0]);
    this.insertValue(originNode.values,newNode.values[0],pos);
    this.insertValue(originNode.children,newNode.children[0],pos);
    originNode.children[pos+1] = newNode.children[1];
    originNode.size++;
    if (originNode.size >= this.NODESIZE) {
      this.tempNode = split(originNodeIndex);
      return 1;
    }
    return -1;
  }

  /**
   * split the node
   * @param nodeIndex
   * @return
   */
  Node split(int nodeIndex) {
    Node curNode = this.nodes[nodeIndex];
    // use the tempNode to temperary store the data we need to pass to the parent and merge,
    // so we only need to init its first value as the current node's midind value and init its
    // left and right children
    int midInd = (this.NODESIZE-1) / 2;
    Node temp = new Node();
    temp.size = 1;
    temp.values = new int[this.NODESIZE];
    // init the first value as the midInd value of origin node
    temp.values[0] = curNode.values[midInd];
    temp.children = new int[this.NODESIZE+1];

    // create a new node which represent the right node
    int newRightNodeInd;
    // if current node is leaf node, then we do not need to init the children,
    // otherwise we need init the children array and copy the right part of children from origin node to the new node
    if (this.isLeaf(curNode)){
      newRightNodeInd = this.createLeaf();
    }else {
      // get the mid index of children array, and copy the range from midindForChild to nodesize+1 of origin node to the right child
      int midIndForChild = (this.NODESIZE + 1) / 2;
      newRightNodeInd = this.initNode();
      System.arraycopy(curNode.children, midIndForChild, this.nodes[newRightNodeInd].children,0, this.NODESIZE+1-midIndForChild);
    }

    //init the values, size, and parents' pointer for the new right child
    System.arraycopy(curNode.values, midInd+1, this.nodes[newRightNodeInd].values, 0, this.NODESIZE-midInd-1);
    this.nodes[newRightNodeInd].size = this.NODESIZE-midInd-1;
    temp.children[1] = newRightNodeInd;

    // because we use the new node to represent the right child, and the temp node to store the parent,
    // the origin node could reuse to represent the left child, the advantage of doing so is that
    // we only need to change the current node's size to midInd
    curNode.size = midInd;
    temp.children[0] = nodeIndex;
    return temp;
  }


  /**
   * binary search an array
   * @param values array need to search
   * @param size the number of key stored in node(not tree pointers)
   * @param searchValue value wanted
   * @return this function will return an index of potential position of searchvalue,
   * which means it could be values with the return index equal to the search value or the search
   * value is in the node's chilren with the return index(possibly)
   */
  int binarySearch(int[] values, int size, int searchValue){
    int left = 0;
    int right = size - 1;
    // when the search value is greater than the max number of values,
    // it is possible that the search value is in the node's last child(the biggest one).
    if(searchValue > values[right])
      return size;

    int mid;
    while(left<right){
      // get the mid value
      mid = left + (right - left)/2;
      if(values[mid]>searchValue){
        // don't use right = mid-1 because it is possible that search value is in the children[mid]
        right = mid;
      }else if(values[mid]<searchValue){
        //if the mid value is smaller than the search value, that means the search value is impossible
        // to be the values[mid] or in children[mid]
        left = mid+1;
      }else{
        return mid;
      }
    }
    return left;
  }

  /**
   * insert value into values at ind, all elements with its origin index after ind(include the one
   * on ind) will move 1 position backward
   * @param values the array need to insert value
   * @param value the value need insert
   * @param ind the position to insert
   */
  void insertValue(int[] values, int value, int ind){
    System.arraycopy(values, ind, values, ind+1, values.length-ind-1);;
    values[ind] = value;
  }


  /*********** Functions for accessing node  ******************/

  /*
   * isLeaf(Node node)
   *    - True if the specified node is a leaf node.
   *         (Leaf node -> a missing children)
   */
  boolean isLeaf(Node node) {
    return node.children == null;
  }

  /*
   * initNode(): Initialize a new node and returns the pointer.
   *    - return node pointer
   */
  int initNode() {
    Node node = new Node();
    node.values = new int[this.NODESIZE];
    node.children =  new int[this.NODESIZE + 1];

    checkSize();
    nodes[cntNodes] = node;
    return cntNodes++;
  }

  /*
   * createLeaf(): Creates a new leaf node and returns the pointer.
   *    - return node pointer
   */
  int createLeaf() {
    Node node = new Node();
    node.values = new int[NODESIZE];

    checkSize();
    nodes[cntNodes] = node;
    return cntNodes++;
  }

  /*
   * checkSize(): Resizes the node array if necessary.
   */
  private void checkSize() {
    if(cntNodes == nodes.length) {
      Node[] tmp = new Node[cntNodes << 1];
      System.arraycopy(nodes, 0, tmp, 0, cntNodes);
      nodes = tmp;
    }
  }

  /**
   * get the index of root on this.nodes
   * @return the index of root on this.nodes
   */
  public int getRoot() {
    return root;
  }
}

/*
 * Node data structure.
 *   - This is the simplest structure for nodes used in B-tree
 *   - This will be used for both internal and leaf nodes.
 */
final class Node {
  /* Node Values (Leaf Values / Key Values for the children nodes).  */
  int[] values;

  /* Node Array, pointing to the children nodes.
   * This array is not initialized for leaf nodes.
   */
  int[] children;

  /* Number of entries
   * (Rule in B Trees:  d <= size <= 2 * d).
   */
  int size;
}

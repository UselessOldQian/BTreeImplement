import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Xingjian Qian 002192922
 * @create 2022-02-08 7:40 pm
 */
public class BtreeFuncTest {

  @Test
  public void binarySearch() {
    Btree btree = new Btree();
    Assert.assertEquals(btree.binarySearch(new int[]{1, 2, 3, 4, 5, 0, 0, 0}, 5, 4),3 );
    Assert.assertEquals(btree.binarySearch(new int[]{1, 2, 3, 4, 5, 6, 0, 0}, 5, 6),5 );
    Assert.assertEquals(btree.binarySearch(new int[]{1, 2, 3, 4, 5, 0, 0, 0}, 5, 8),5 );
    Assert.assertEquals(btree.binarySearch(new int[]{1, 2, 3, 4, 5, 0, 0, 0}, 5, 0),0 );
    Assert.assertEquals(btree.binarySearch(new int[]{1, 3, 4, 5, 9, 0, 0}, 5, 2),1 );
    Assert.assertEquals(btree.binarySearch(new int[]{1, 3, 4, 5, 9, 0, 0}, 5, 8),4 );
  }

  @Test
  public void insertValue(){
    Btree btree = new Btree();
    int[] arr = new int[]{1,2,5,6,7,0};
    btree.insertValue(arr,3,5);
    System.out.println(Arrays.toString(arr));
  }

  @Test
  public void insert(){
    Btree btree = new Btree(3);
    btree.Insert(1);
    btree.Insert(2);
    btree.Insert(3);
    btree.Insert(4);
    btree.Insert(5);
    btree.Insert(6);
    btree.Insert(7);
    btree.Insert(0);
    btree.Insert(5);
    btree.Insert(18);
    btree.Insert(12);

    btree.Display(5);
  }
}
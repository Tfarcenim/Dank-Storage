package com.tfar.dankstorage.network;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class SortingData implements Comparable<SortingData>{
  public final ItemStack stack;

  public SortingData(ItemStack stack) {
    this.stack = stack;
  }

  public boolean matches(ItemStack otherStack){
    return stack.getCount() < Integer.MAX_VALUE && stack.getItem() == otherStack.getItem() && ItemStack.areItemStackTagsEqual(stack,otherStack);
  }

  public int add(int add){
    //nothing was gained
    if (add == 0)return 0;
    //is full, can't add more
    if (stack.getCount() == Integer.MAX_VALUE)return add;
    //integer overflow
    if (this.stack.getCount() + add <= 0){
      //return the overflow
      int overflowed = stack.getCount() + add + 1;
      stack.setCount(Integer.MAX_VALUE);
      return overflowed - Integer.MIN_VALUE;
    }
    //all should be good
    stack.setCount(stack.getCount() + add);
    return 0;
  }
  public static boolean exists(List<SortingData> existing,ItemStack stackToCheck){
    for (SortingData data : existing) {
      if (data.matches(stackToCheck)) {
        return true;
      }
    }
    return false;
  }

  public static int addToList(List<SortingData> existing,ItemStack newStack){
    for (SortingData sortingData : existing){
      if (sortingData.matches(newStack)) {
        return sortingData.add(newStack.getCount());
      }
    }
    return 0;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
   * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)
   *
   * <p>The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.
   *
   * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
   * all <tt>z</tt>.
   *
   * <p>It is strongly recommended, but <i>not</i> strictly required that
   * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
   * class that implements the <tt>Comparable</tt> interface and violates
   * this condition should clearly indicate this fact.  The recommended
   * language is "Note: this class has a natural ordering that is
   * inconsistent with equals."
   *
   * <p>In the foregoing description, the notation
   * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
   * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
   * <tt>0</tt>, or <tt>1</tt> according to whether the value of
   * <i>expression</i> is negative, zero or positive.
   *
   * @param data the object to be compared.
   * @return a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object.
   * @throws NullPointerException if the specified object is null
   * @throws ClassCastException   if the specified object's type prevents it
   *                              from being compared to this object.
   */
  @Override
  public int compareTo(@Nonnull SortingData data) {
    return data.stack.getCount() - this.stack.getCount();
  }
}

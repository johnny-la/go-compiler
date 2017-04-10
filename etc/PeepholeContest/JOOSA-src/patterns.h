/*
 * JOOS is Copyright (C) 1997 Laurie Hendren & Michael I. Schwartzbach
 *
 * Reproduction of all or part of this software is permitted for
 * educational or research use on condition that this copyright notice is
 * included in any copy. This software comes with no warranty of any
 * kind. In no event will the authors be liable for any damages resulting from
 * use of this software.
 *
 * email: hendren@cs.mcgill.ca, mis@brics.dk
 */

/* iload x        iload x        iload x
 * ldc 0          ldc 1          ldc 2
 * imul           imul           imul
 * ------>        ------>        ------>
 * ldc 0          iload x        iload x
 *                               dup
 *                               iadd
 */

int simplify_multiplication_right(CODE **c)
{ int x,k;
  if (is_iload(*c,&x) && 
      is_ldc_int(next(*c),&k) && 
      is_imul(next(next(*c)))) {
     if (k==0) return replace(c,3,makeCODEldc_int(0,NULL));
     else if (k==1) return replace(c,3,makeCODEiload(x,NULL));
     else if (k==2) return replace(c,3,makeCODEiload(x,
                                       makeCODEdup(
                                       makeCODEiadd(NULL))));
     return 0;
  }
  return 0;
}

/* dup
 * astore x
 * pop
 * -------->
 * astore x
 */
int simplify_astore(CODE **c)
{ int x;
  if (is_dup(*c) &&
      is_astore(next(*c),&x) &&
      is_pop(next(next(*c)))) {
     return replace(c,3,makeCODEastore(x,NULL));
  }
  return 0;
}

/* goto L1
 * ...
 * L1:
 * goto L2
 * ...
 * L2:
 * --------->
 * goto L2
 * ...
 * L1:    (reference count reduced by 1)
 * goto L2
 * ...
 * L2:    (reference count increased by 1)  
 */
int simplify_goto_goto(CODE **c)
{ int l1,l2;
  if (is_goto(*c,&l1) && is_goto(next(destination(l1)),&l2) && l1>l2) {
     droplabel(l1);
     copylabel(l2);
     return replace(c,1,makeCODEgoto(l2,NULL));
  }
  return 0;
}


/* iload x
 * ldc k   (0<=k<=127)
 * iadd
 * istore x
 * --------->
 * iinc x k
 */ 
int positive_increment(CODE **c)
{ int x,y,k;
  if (is_iload(*c,&x) &&
      is_ldc_int(next(*c),&k) &&
      is_iadd(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0 <= k && k <= 127) {
     return replace(c,4,makeCODEiinc(x,k,NULL));
  }
  return 0;
}


/*
* CUSTOM PATTERNS
*/

/* dup
 * istore x
 * pop
 * -------->
 * istore x
 *
 * Explanation: Since the duplicated value will be popped off the stack
 * anyways, we remove the dup and pop operations
 */
int simplify_istore(CODE **c)
{
  int x;
  if (is_dup(*c) &&
      is_istore(next(*c),&x) &&
      is_pop(next(next(*c))))
  return replace(c,3,makeCODEistore(x,NULL));

  return 0;
}

/* ldc k   (0<=k<=127)
 * iload x 
 * iadd
 * istore x
 * --------->
 * iinc x k
 *
 * Explanation: We can condense the loading and addition of a constant and a local
 * into the more lightweight "iinc" command
 */
int positive_increment_left(CODE **c)
{
  int x,y,k;
  if (is_ldc_int(*c,&k) &&
      is_iload(next(*c),&x) &&
      is_iadd(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0 <= k && k <= 127)
    return replace(c, 4, makeCODEiinc(x,k,NULL));
  
  return 0;
}

/* iload x
 * ldc k   (0<=k<=127)
 * isub
 * istore x
 * --------->
 * iinc x -k
 *
 * Explanation: Same as above, except the constant is negative
 */ 
int simplify_negative_increment(CODE **c)
{ int x,y,k;
  if (is_iload(*c,&x) &&
      is_ldc_int(next(*c),&k) &&
      is_isub(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0 <= k && k <= 127) {
     return replace(c,4,makeCODEiinc(x,-k,NULL));
  }
  return 0;
}

/* ldc k   (0<=k<=127)
 * iload x
 * isub
 * istore x
 * --------->
 * iinc x -k
 * ineg
 * 
 * Explanation: Same as above, except the constant is loaded first. 
 * The operation performs k - x = -(x-k), so we decrement x
 * by k, then we negate the result 
 */ 
int simplify_negative_increment_left(CODE **c)
{ int x,y,k;
  if (is_ldc_int(*c,&k) &&
      is_iload(next(*c),&x) &&
      is_isub(next(next(*c))) &&
      is_istore(next(next(next(*c))),&y) &&
      x==y && 0 <= k && k <= 127) {
     return replace(c,4,makeCODEineg(makeCODEiinc(x,-k,NULL)));
  }
  return 0;
}


/* iinc x 0
 * --------->
 * null
 * Explanation: an increment by zero does not modify x's value (it is a non-operation)
 * 
 */ 
int simplify_increment_0(CODE **c)
{ 
  int x, k;
  if (is_iinc(*c, &x, &k) &&
      (k == 0)) {
     return kill_line(c);
  }
  return 0;
}

/* iload x
 * ldc 0
 * iadd
 * --------->
 * iload x
 *
 * Explanation: an increment by zero does not modify x's value (it is a non-operation)
 * 
 */ 
int simplify_add_0(CODE **c)
{ 
  int x, k;
  if (is_iload(*c, &x) &&
      is_ldc_int(next(*c), &k) &&
      (k == 0)) {
     return replace(c,2,makeCODEiload(x,NULL));
  }
  return 0;
}

/* ldc 0
 * iload x
 * iadd
 * --------->
 * iload x
 *
 * Explanation: an increment by zero does not modify x's value (it is a non-operation)
 * 
 */ 
int simplify_add_0_left(CODE **c)
{ 
  int x, k;
  if (is_ldc_int(*c, &k) &&
      is_iload(next(*c), &x) &&
      (k == 0)) {
     return replace(c,2,makeCODEiload(x,NULL));
  }
  return 0;
}

/* astore x
 * aload x
 * 
 * --------->
 * dup
 * astore x
 *
 * Explanation: Instead of storing a local and loading it back, we can
 * duplicate the value on the stack and store that duplicate
 */
int simplify_astore_aload(CODE **c)
{
  int x, y;
  if (is_astore(*c,&x) &&
      is_aload(next(*c),&y) &&
      x == y)
      return replace(c, 2, makeCODEdup(
                makeCODEastore(x,NULL)));
  return 0;
}

/* istore x
 * iload x
 * 
 * --------->
 * dup
 * istore x
 *
 * Explanation: Same as above
 */
int simplify_istore_iload(CODE **c)
{
  int x, y;
  if (is_istore(*c,&x) &&
      is_iload(next(*c),&y) &&
      x == y)
      return replace(c, 2, makeCODEdup(
                makeCODEistore(x,NULL)));
  return 0;
}

/* ldc 0          ldc 1          ldc 2
 * iload x        iload x        iload x
 * imul           imul           imul
 * ------>        ------>        ------>
 * ldc 0          iload x        iload x
 *                               dup
 *                               iadd
 *
 * Explanation: We can convert small multiplications to their
 * with corresponding additions, omit multiplications
 * by ones by removing the multiplication, and replace
 * multiplications by 0 by the constant zero
 */
int simplify_multiplication_left(CODE **c)
{ int x,k;
  if (is_ldc_int(*c,&k) && 
      is_iload(next(*c),&x) && 
      is_imul(next(next(*c)))) {
     if (k==0) return replace(c,3,makeCODEldc_int(0,NULL));
     else if (k==1) return replace(c,3,makeCODEiload(x,NULL));
     else if (k==2) return replace(c,3,makeCODEiload(x,
                                       makeCODEdup(
                                       makeCODEiadd(NULL))));
     return 0;
  }
  return 0;
}

/* if_icmpeq true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_icmpne false_2
 * ...
 *
 * Explanation: We don't need to add multiple labels to handle
 * the true/false case in a condition. We can simply branch
 * to the "false_2" label if the original condition is false, or
 * execute the "..." statements if the condition is true
 */
int simplify_if_icmpeq(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_ificmpeq(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3))
      return replace(c, 7, makeCODEdup(
                makeCODEif_icmpne(false_label_3,NULL)));
  return 0;
}

void init_patterns(void) {
  ADD_PATTERN(simplify_multiplication_right);
  ADD_PATTERN(simplify_astore);
  ADD_PATTERN(positive_increment);
  ADD_PATTERN(simplify_goto_goto);
  
  /* Custom patterns */
  ADD_PATTERN(simplify_istore);
  ADD_PATTERN(positive_increment_left);
  ADD_PATTERN(simplify_increment_0);
  ADD_PATTERN(simplify_negative_increment);
  ADD_PATTERN(simplify_negative_increment_left);
  ADD_PATTERN(simplify_astore_aload);
  ADD_PATTERN(simplify_istore_iload);
  ADD_PATTERN(simplify_multiplication_left);
  ADD_PATTERN(simplify_add_0);
  ADD_PATTERN(simplify_add_0_left);
}

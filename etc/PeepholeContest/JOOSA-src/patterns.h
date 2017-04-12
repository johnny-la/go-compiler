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



/* iload x
 * ldc 1
 * idiv
 * -------->
 * iload x
 *
 * Explanation: Dividing by 1 does not modify x's value. Thus,
 * the division can be omitted
 */
int simplify_division(CODE **c)
{
  int x,k;
  if (is_iload(*c,&x) &&
      is_ldc_int(next(*c),&k) &&
      is_idiv(next(next(*c))) &&
      k == 1)
      return replace(c,3,makeCODEiload(x,NULL));
  return 0;
}

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

/* nop
 * ----->
 * {empty}
 *
 * Explanation: Nops perform no operations. Thus, they can be
 * safely removed from the program 
 */
int simplify_noop(CODE **c)
{
  if (is_nop(*c))
    return replace(c,1,NULL);
  return 0;
}

int simplify_swaps(CODE **c)
{
  /* aload x
   * aload y
   * swap
   * ------->
   * aload y
   * aload x
   *
   * Explanation: The swap is useless since we can simply
   * reverse the loading order
   */
  int x,y;
  if (is_aload(*c,&x) &&
      is_aload(next(*c),&y) &&
      is_swap(next(next(*c))))
      return replace(c, 3, makeCODEaload(y,makeCODEaload(x,NULL)));

  /* iload x
   * iload y
   * swap
   * ------->
   * iload y
   * iload x
   *
   * Explanation: Same as above, except for iload
   */
  if (is_iload(*c,&x) &&
      is_iload(next(*c),&y) &&
      is_swap(next(next(*c))))
      return replace(c, 3, makeCODEiload(y,makeCODEiload(x,NULL)));

  /* iload x
   * aload y
   * swap
   * ------->
   * aload y
   * iload x
   *
   * Explanation: Same as above, except for a combination of iload and aload
   */
  if (is_iload(*c,&x) &&
      is_aload(next(*c),&y) &&
      is_swap(next(next(*c))))
      return replace(c, 3, makeCODEaload(y,makeCODEiload(x,NULL)));

  /* aload x
   * iload y
   * swap
   * ------->
   * iload y
   * aload x
   *
   * Explanation: Same as above, except with iload and aload swapped
   */
  if (is_aload(*c,&x) &&
      is_iload(next(*c),&y) &&
      is_swap(next(next(*c))))
      return replace(c, 3, makeCODEiload(y,makeCODEaload(x,NULL)));
  return 0;
}

/* goto L 
 * non-label
 * ----------->
 * goto L
 *
 * Explanation: Since the operation following the goto is not a label,
 * it will never be reached, and can be removed
 */
int simplify_goto_label(CODE **c)
{
  int x,y;
  if (is_goto(*c,&x) &&
      !is_label(next(*c),&y))
      return replace(c, 2, makeCODEgoto(x,NULL));
  return 0;
}

/* goto label
 * ...
 * label:
 * return
 * --------->
 * return
 * ...
 * label:  
 * return
 * Explanation: The number of instructions are not decreased but the 
 * reference count is reduced by 1 since by removing the goto to a label, 
 * we are decreasing the reference to that label by 1
 */
int simplify_goto_label_return(CODE **c)
{ int label;
  if (is_goto(*c,&label) &&
      is_return(next(destination(label)))) {
    droplabel(label);
    return replace(c,1,makeCODEreturn(NULL));
  }
  return 0;
}

/*
 * goto label
 * label:
 * --------->
 * label:
 * Explanation: If the goto is branching to a label that is the same as the label 
 * right after the goto instruction, this is redundant and
 * the labels could be condensed to a single label.
 */
int delete_unnecessary_goto(CODE **c)
{ int label1, label2;
  if (is_goto(*c, &label1) &&
      is_label(next(*c), &label2) &&
      label1 == label2) {
    return replace(c, 2, makeCODElabel(label1, NULL));
  }
  return 0;
}

/*
 * return 
 * ...
 * label
 * --------->
 * return 
 * label
 * Explanation: code after a return instruction that is not a label cannot be reached and therefore
 * could be completely neglected
 */

int delete_unreachable_code_return(CODE **c)
{ int label1, label2;
  /* return. */
  if (is_return(*c) &&
      !is_label(next(*c), &label1) &&
      is_label(nextby(*c, 2), &label2)) {
    return replace_modified(c, 3, makeCODEreturn(makeCODElabel(label2, NULL)));
  }
  return 0;
}

/*
 * areturn 
 * ...
 * label
 * --------->
 * return 
 * label
 * Explanation: code after an areturn instruction that is not a label cannot be reached and therefore
 * could be completely neglected
 */

int delete_unreachable_code_areturn(CODE **c)
{ int label1, label2;
  /* areturn. */
  if (is_areturn(*c) &&
      !is_label(next(*c), &label1) &&
      is_label(nextby(*c, 2), &label2)) {
    return replace_modified(c, 3, makeCODEareturn(makeCODElabel(label2, NULL)));
  }
  return 0;
}

/*
 * ireturn 
 * ...
 * label
 * --------->
 * return 
 * label
 * Explanation: code after an ireturn instruction that is not a label cannot be reached and therefore
 * could be completely neglected
 */
int delete_unreachable_code_ireturn(CODE **c)
{ int label1, label2;
  if (is_ireturn(*c) &&
      !is_label(next(*c), &label1) &&
      is_label(nextby(*c, 2), &label2)) {
    return replace_modified(c, 3, makeCODEireturn(makeCODElabel(label2, NULL)));
  }
  return 0;
}

/* 
 * ldc x 
 * dup
 * ifnull L
 * --------->
 * ldc_int x
 * Explanation: constants(integers) cannot be null, only objects. So we can eliminate the loading, duplication,
 * and then the popping and checking of null by just loading.
 */

int simplify_ifnull_constant_integer(CODE **c)
{ int x, label;
  char* s;

  if (is_ldc_int(*c, &x) && 
    is_dup(next(*c)) && 
    is_ifnull(nextby(*c, 2), &label)) {
    droplabel(label);
    return replace(c, 3, makeCODEldc_int(x, NULL));
  }

  return 0;
}

/* 
 * ldc x
 * dup
 * ifnull L
 * --------->
 * ldc_string x
 * Explanation: constants(strings) cannot be null, only objects. So we can eliminate the loading, duplication,
 * and then the popping and checking of null by just loading.
 */
int simplify_ifnull_constant_string(CODE **c)
{ int x, label;
  char* s;

  if (is_ldc_string(*c, &s) &&
   is_dup(next(*c)) &&
    is_ifnull(nextby(*c, 2), &label)) {
    droplabel(label);
    return replace(c, 3, makeCODEldc_string(s, NULL));
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
  if (is_if_icmpeq(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_icmpne(false_label_3,NULL));
  }
  return 0;
}

/* if_acmpeq true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_acmpne false_2
 * ...
 *
 * Explanation: Same as above, but for if_acmpeq
 */
int simplify_if_acmpeq(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_acmpeq(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_acmpne(false_label_3,NULL));
  }
  return 0;
}

/* if_icmpne true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_icmpeq false_2
 * ...
 *
 * Explanation: Same as above, but for if_icmpne
 */
int simplify_if_icmpne(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_icmpne(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_icmpeq(false_label_3,NULL));
  }
  return 0;
}

/* if_acmpne true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_acmpeq false_2
 * ...
 *
 * Explanation: Same as above, but for if_acmpne
 */
int simplify_if_acmpne(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_acmpne(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_acmpeq(false_label_3,NULL));
  }
  return 0;
}

/* if_icmplt true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_icmpge false_2
 * ...
 *
 * Explanation: Same as above, but for if_icmplt
 */
int simplify_if_icmplt(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_icmplt(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_icmpge(false_label_3,NULL));
  }
  return 0;
}

/* if_icmple true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_icmpgt false_2
 * ...
 *
 * Explanation: Same as above, but for if_icmple
 */
int simplify_if_icmple(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_icmple(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_icmpgt(false_label_3,NULL));
  }
  return 0;
}

/* if_icmpgt true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_icmple false_2
 * ...
 *
 * Explanation: Same as above, but for if_icmpgt
 */
int simplify_if_icmpgt(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_icmpgt(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_icmple(false_label_3,NULL));
  }
  return 0;
}

/* if_icmpge true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * if_icmplt false_2
 * ...
 *
 * Explanation: Same as above, but for if_icmpge
 */
int simplify_if_icmpge(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_if_icmpge(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEif_icmplt(false_label_3,NULL));
  }
  return 0;
}

/* ifnull true
 * iconst_0
 * goto false_1
 * true:
 * iconst_1 
 * false_1: 
 * ifeq false_2 ---
 * ...
 * false_2:
 * --------->
 * ifnonnull false_2
 * ...
 *
 * Explanation: Same as above, but for ifnull
 */
int simplify_ifnull(CODE **c)
{
  int true_label_1, true_label_2, false_label_1, false_label_2, false_label_3;
  int x,y;
  if (is_ifnull(*c,&true_label_1) &&
      is_ldc_int(next(*c),&x) &&
      x == 0 &&
      is_goto(next(next(*c)), &false_label_1) &&
      is_label(next(next(next(*c))),&true_label_2) &&
      true_label_1 == true_label_2 &&
      is_ldc_int(next(next(next(next(*c)))),&y) &&
      y == 1 &&
      is_label(next(next(next(next(next(*c))))), &false_label_2) &&
      false_label_1 == false_label_2 &&
      is_ifeq(next(next(next(next(next(next(*c)))))), &false_label_3)) 
  {
    droplabel(true_label_1);
    droplabel(false_label_1);
    return replace(c, 7, makeCODEifnonnull(false_label_3,NULL));
  }
  return 0;
}

/*
* dup 
* aload_x
* swap
* putfield X Y
* pop
* ----->
* aload_x
* ldc x
* putfield X Y
* Explanation: dup and pop are redundant
*/
int simplify_pop_after_dup_putfield(CODE **c) {
  int x;
  char *y;
  if (is_dup(*c) &&
      is_aload(next(*c),&x)   &&
      is_swap(next(next(*c))) &&
      is_putfield(next(next(next(*c))), &y) &&
      is_pop(next(next(next(next(*c)))))) {
      return replace(c,5,makeCODEaload(x, makeCODEswap(makeCODEputfield(y , NULL))));
  }
  return 0;
}

/*
* stuff
* L1:
* stuff
* ---->
* stuff
* Explanation:
*/

int delete_deadlabel(CODE **c)
{ int label;
  if (is_label(*c, &label) && deadlabel(label)) {
     return kill_line(c);
  }
  return 0;
}

/*
 * branch1 label1     
 *
 * goto label2
 * label1:
 * --------->
 * branch2 label2  
 * Explanation:      
 */

int collapse_branch(CODE **c)
{ int label1, label2, label3;
  /* ifnull, ldc integer */
  if (is_ifnull(*c, &label1) &&
      uniquelabel(label1) &&
      is_goto(next(*c), &label2) &&
      is_label(nextby(*c, 2), &label3) &&
       label3 == label1) {
    return replace(c, 3, makeCODEifnonnull(label2, NULL));
  }

  /* ifnonnull, ldc integer */
  if (is_ifnonnull(*c, &label1) &&
      uniquelabel(label1) &&
      is_goto(next(*c), &label2) &&
      is_label(nextby(*c, 2), &label3) &&
       label3 == label1) {
    return replace(c, 3, makeCODEifnull(label2, NULL));
  }
  return 0;
}

void init_patterns(void) {
  ADD_PATTERN(simplify_multiplication_right);
  ADD_PATTERN(simplify_astore);
  ADD_PATTERN(positive_increment);
  ADD_PATTERN(simplify_goto_goto);

  // /* Custom patterns */
  // ADD_PATTERN(simplify_istore);
  // ADD_PATTERN(positive_increment_left);
  // ADD_PATTERN(simplify_increment_0);
  // ADD_PATTERN(simplify_negative_increment);
  // ADD_PATTERN(simplify_negative_increment_left);
  // ADD_PATTERN(simplify_astore_aload);
  // ADD_PATTERN(simplify_istore_iload);
  // ADD_PATTERN(simplify_multiplication_left);
  // ADD_PATTERN(simplify_division);

  // // ADD_PATTERN(simplify_add_0);
  // // ADD_PATTERN(simplify_add_0_left);
  // ADD_PATTERN(simplify_noop);
  // ADD_PATTERN(simplify_swaps);
  // ADD_PATTERN(simplify_goto_label);
  // ADD_PATTERN(simplify_pop_after_dup_putfield);
  // ADD_PATTERN(delete_goto_deadlabel);
  // ADD_PATTERN(delete_unnecessary_goto);
  // ADD_PATTERN(simplify_goto_label_return);
  // ADD_PATTERN(delete_unreachable_code_return);
  // ADD_PATTERN(delete_unreachable_code_areturn);
  // ADD_PATTERN(delete_unreachable_code_ireturn);
  // ADD_PATTERN(collapse_branch);

  // /* Conditional branches */
  // ADD_PATTERN(simplify_if_icmpeq);
  // ADD_PATTERN(simplify_if_acmpeq);
  // ADD_PATTERN(simplify_if_icmpne);
  // ADD_PATTERN(simplify_if_acmpne);
  // ADD_PATTERN(simplify_if_icmplt);
  // ADD_PATTERN(simplify_if_icmple);
  // ADD_PATTERN(simplify_if_icmpgt);
  // ADD_PATTERN(simplify_if_icmpge);
  // ADD_PATTERN(simplify_ifnull);
  // ADD_PATTERN(simplify_ifnull_constant_integer);
  // ADD_PATTERN(simplify_ifnull_constant_string);
}

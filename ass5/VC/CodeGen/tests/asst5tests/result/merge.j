.class public merge
.super java/lang/Object
	
	
	; standard class static initializer 
.method static <clinit>()V
	
	
	; set limits used by this method
.limit locals 0
.limit stack 0
	return
.end method
	
	; standard constructor initializer 
.method public <init>()V
.limit stack 1
.limit locals 1
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method
.method merge([II[II[I)V
L0:
.var 0 is this Lmerge; from L0 to L1
.var 1 is a [I from L0 to L1
.var 2 is m I from L0 to L1
.var 3 is b [I from L0 to L1
.var 4 is n I from L0 to L1
.var 5 is sorted [I from L0 to L1
.var 6 is i I from L0 to L1
.var 7 is j I from L0 to L1
.var 8 is k I from L0 to L1
	iconst_0
	dup
	istore 8
	dup
	istore 7
	pop
	iconst_0
	dup
	istore 6
	pop
L2:
	iload 6
	iload_2
	iload 4
	iadd
	if_icmplt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
L6:
	iload 6
	invokestatic VC/lang/System/putIntLn(I)V
	iload 7
	iload_2
	if_icmplt L10
	iconst_0
	goto L11
L10:
	iconst_1
L11:
	ifeq L8
	iload 8
	iload 4
	if_icmplt L12
	iconst_0
	goto L13
L12:
	iconst_1
L13:
	ifeq L8
	iconst_1
	goto L9
L8:
	iconst_0
L9:
	ifeq L14
L16:
	ldc "less"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
	aload_1
	iload 7
	iaload
	aload_3
	iload 8
	iaload
	if_icmplt L18
	iconst_0
	goto L19
L18:
	iconst_1
L19:
	ifeq L20
L22:
	aload 5
	iload 6
	aload_1
	iload 7
	iaload
	dup_x2
	iastore
	pop
	iload 7
	iconst_1
	iadd
	dup
	istore 7
	pop
L23:
	goto L21
L20:
L24:
	aload 5
	iload 6
	aload_3
	iload 8
	iaload
	dup_x2
	iastore
	pop
	iload 8
	iconst_1
	iadd
	dup
	istore 8
	pop
L25:
L21:
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
L17:
	goto L15
L14:
	iload 7
	iload_2
	if_icmpeq L26
	iconst_0
	goto L27
L26:
	iconst_1
L27:
	ifeq L28
L30:
	ldc "j max"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
L32:
	iload 6
	iload_2
	iload 4
	iadd
	if_icmplt L34
	iconst_0
	goto L35
L34:
	iconst_1
L35:
	ifeq L33
L36:
	aload 5
	iload 6
	aload_3
	iload 8
	iaload
	dup_x2
	iastore
	pop
	iload 8
	iconst_1
	iadd
	dup
	istore 8
	pop
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
L37:
	goto L32
L33:
L31:
	goto L29
L28:
L38:
	ldc "k max"
	invokestatic VC/lang/System/putStringLn(Ljava/lang/String;)V
L40:
	iload 6
	iload_2
	iload 4
	iadd
	if_icmplt L42
	iconst_0
	goto L43
L42:
	iconst_1
L43:
	ifeq L41
L44:
	aload 5
	iload 6
	aload_1
	iload 7
	iaload
	dup_x2
	iastore
	pop
	iload 7
	iconst_1
	iadd
	dup
	istore 7
	pop
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
L45:
	goto L40
L41:
L39:
L29:
L15:
L7:
	goto L2
L3:
L1:
	
	; return may not be present in a VC function returning void
	; The following return inserted by the VC compiler
	return
	
	; set limits used by this method
.limit locals 9
.limit stack 4
.end method
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Lmerge; from L0 to L1
	new merge
	dup
	invokenonvirtual merge/<init>()V
	astore_1
.var 2 is a [I from L0 to L1
	bipush 100
	newarray int
	astore_2
.var 3 is b [I from L0 to L1
	bipush 100
	newarray int
	astore_3
.var 4 is m I from L0 to L1
.var 5 is n I from L0 to L1
.var 6 is c I from L0 to L1
.var 7 is sorted [I from L0 to L1
	sipush 200
	newarray int
	astore 7
	ldc "Input number of elements in first array
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	dup
	istore 4
	pop
	ldc "Input integers
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iconst_0
	dup
	istore 6
	pop
L2:
	iload 6
	iload 4
	if_icmplt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
L6:
	aload_2
	iload 6
	invokestatic VC/lang/System.getInt()I
	dup_x2
	iastore
	pop
L7:
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
	goto L2
L3:
	ldc "Input number of elements in second array
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	dup
	istore 5
	pop
	ldc "Input integers
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iconst_0
	dup
	istore 6
	pop
L8:
	iload 6
	iload 5
	if_icmplt L10
	iconst_0
	goto L11
L10:
	iconst_1
L11:
	ifeq L9
L12:
	aload_3
	iload 6
	invokestatic VC/lang/System.getInt()I
	dup_x2
	iastore
	pop
L13:
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
	goto L8
L9:
	aload_1
	aload_2
	iload 4
	aload_3
	iload 5
	aload 7
	invokevirtual merge/merge([II[II[I)V
	ldc "Sorted array:
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iconst_0
	dup
	istore 6
	pop
L14:
	iload 6
	iload 4
	iload 5
	iadd
	if_icmplt L16
	iconst_0
	goto L17
L16:
	iconst_1
L17:
	ifeq L15
L18:
	aload 7
	iload 6
	iaload
	invokestatic VC/lang/System/putIntLn(I)V
L19:
	iload 6
	iconst_1
	iadd
	dup
	istore 6
	pop
	goto L14
L15:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 8
.limit stack 6
.end method

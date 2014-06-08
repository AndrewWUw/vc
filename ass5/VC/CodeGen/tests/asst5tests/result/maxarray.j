.class public maxarray
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
.method public static main([Ljava/lang/String;)V
L0:
.var 0 is argv [Ljava/lang/String; from L0 to L1
.var 1 is vc$ Lmaxarray; from L0 to L1
	new maxarray
	dup
	invokenonvirtual maxarray/<init>()V
	astore_1
.var 2 is array [I from L0 to L1
	bipush 100
	newarray int
	astore_2
.var 3 is maximum I from L0 to L1
.var 4 is size I from L0 to L1
.var 5 is c I from L0 to L1
.var 6 is location I from L0 to L1
	iconst_1
	istore 6
	ldc "Enter the number of elements in array
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	dup
	istore 4
	pop
	ldc "Enter integers
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iconst_0
	dup
	istore 5
	pop
L2:
	iload 5
	iload 4
	if_icmplt L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
	aload_2
	iload 5
	invokestatic VC/lang/System.getInt()I
	dup_x2
	iastore
	pop
	iload 5
	iconst_1
	iadd
	dup
	istore 5
	pop
	goto L2
L3:
	aload_2
	iconst_0
	iaload
	dup
	istore_3
	pop
	iconst_1
	dup
	istore 5
	pop
L6:
	iload 5
	iload 4
	if_icmplt L8
	iconst_0
	goto L9
L8:
	iconst_1
L9:
	ifeq L7
L10:
	aload_2
	iload 5
	iaload
	iload_3
	if_icmpgt L12
	iconst_0
	goto L13
L12:
	iconst_1
L13:
	ifeq L14
L15:
	aload_2
	iload 5
	iaload
	dup
	istore_3
	pop
	iload 5
	iconst_1
	iadd
	dup
	istore 6
	pop
L16:
L14:
L11:
	iload 5
	iconst_1
	iadd
	dup
	istore 5
	pop
	goto L6
L7:
	ldc "Maximum element is present at location "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload 6
	invokestatic VC/lang/System.putInt(I)V
	ldc " and it's value is "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload_3
	invokestatic VC/lang/System/putIntLn(I)V
	return
L1:
	return
	
	; set limits used by this method
.limit locals 7
.limit stack 4
.end method

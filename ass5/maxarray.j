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
	iconst_0
	istore_3
.var 4 is size I from L0 to L1
	iconst_0
	istore 4
.var 5 is c I from L0 to L1
	iconst_0
	istore 5
.var 6 is location I from L0 to L1
	iconst_1
	istore 6
	invokestatic VC/lang/System.getInt()I
	dup
	istore 4
L2:
	iconst_0
	dup
	istore 5
	iload 5
	iload 4
	if_icmplt L6
	iconst_0
	goto L7
L6:
	iconst_1
L7:
	ifeq L3
	aload_2
	iload 5
	invokestatic VC/lang/System.getInt()I
	dup_x2
	iastore
	iload 5
	iconst_1
	iadd
	dup
	istore 5
	goto L2
L3:
	aload_2
	iconst_0
	iaload
	dup
	istore_3
L10:
	iconst_1
	dup
	istore 5
	iload 5
	iload 4
	if_icmplt L14
	iconst_0
	goto L15
L14:
	iconst_1
L15:
	ifeq L11
L16:
	aload_2
	iload 5
	iaload
	iload_3
	if_icmpgt L20
	iconst_0
	goto L21
L20:
	iconst_1
L21:
	ifeq L22
L23:
	aload_2
	iload 5
	iaload
	dup
	istore_3
	iload 5
	iconst_1
	iadd
	dup
	istore 6
L24:
L22:
L17:
	iload 5
	iconst_1
	iadd
	dup
	istore 5
	goto L10
L11:
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
.limit stack 25
.end method

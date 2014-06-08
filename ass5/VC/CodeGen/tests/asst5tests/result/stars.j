.class public stars
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
.var 1 is vc$ Lstars; from L0 to L1
	new stars
	dup
	invokenonvirtual stars/<init>()V
	astore_1
.var 2 is row I from L0 to L1
.var 3 is c I from L0 to L1
.var 4 is n I from L0 to L1
.var 5 is temp I from L0 to L1
	ldc "Enter the number of rows in pyramid of stars you wish to see "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	invokestatic VC/lang/System.getInt()I
	dup
	istore 4
	pop
	iload 4
	dup
	istore 5
	pop
	iconst_1
	dup
	istore_2
	pop
L2:
	iload_2
	iload 4
	if_icmple L4
	iconst_0
	goto L5
L4:
	iconst_1
L5:
	ifeq L3
L6:
	iconst_1
	dup
	istore_3
	pop
L8:
	iload_3
	iload 5
	if_icmplt L10
	iconst_0
	goto L11
L10:
	iconst_1
L11:
	ifeq L9
	ldc " "
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload_3
	iconst_1
	iadd
	dup
	istore_3
	pop
	goto L8
L9:
	iload 5
	iconst_1
	isub
	dup
	istore 5
	pop
	iconst_1
	dup
	istore_3
	pop
L12:
	iload_3
	iconst_2
	iload_2
	imul
	iconst_1
	isub
	if_icmple L14
	iconst_0
	goto L15
L14:
	iconst_1
L15:
	ifeq L13
	ldc "*"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
	iload_3
	iconst_1
	iadd
	dup
	istore_3
	pop
	goto L12
L13:
	ldc "
"
	invokestatic VC/lang/System/putString(Ljava/lang/String;)V
L7:
	iload_2
	iconst_1
	iadd
	dup
	istore_2
	pop
	goto L2
L3:
	return
L1:
	return
	
	; set limits used by this method
.limit locals 6
.limit stack 3
.end method
